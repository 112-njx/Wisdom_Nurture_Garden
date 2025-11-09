# ==============================
# 构建阶段：使用 Maven 打包 Spring Boot Jar
# ==============================
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app

# 拷贝 pom.xml 和源码
COPY pom.xml .
COPY src ./src

# 打包项目（跳过测试）
RUN mvn clean package -DskipTests

# ==============================
# 运行阶段
# ==============================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 从上一阶段复制 Jar 文件到镜像中
COPY --from=builder /app/target/*.jar app.jar

# 设置时区为东八区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    wget \
    curl \
    openssl \
    && rm -rf /var/lib/apt/lists/*

# 更新系统 CA 证书（基础更新）
RUN update-ca-certificates

# 获取并安装微信 API 证书到 Java 信任库
RUN echo "=== 开始配置微信 API SSL 证书 ===" && \
    # 获取微信 API 证书
    openssl s_client -connect api.weixin.qq.com:443 -showcerts < /dev/null 2>/dev/null | \
    openssl x509 -outform PEM > /tmp/wechat.pem && \
    # 检查证书是否获取成功
    if [ -s /tmp/wechat.pem ]; then \
        echo "✅ 成功获取微信 API 证书" && \
        # 添加到 Java 信任库
        keytool -importcert \
            -alias wechat-api \
            -keystore $JAVA_HOME/lib/security/cacerts \
            -file /tmp/wechat.pem \
            -storepass changeit \
            -noprompt && \
        echo "✅ 微信证书已添加到 Java 信任库" && \
        # 同时更新系统 CA 证书
        cp /tmp/wechat.pem /usr/local/share/ca-certificates/wechat-api.crt && \
        update-ca-certificates && \
        echo "✅ 系统 CA 证书更新完成" && \
        # 清理临时文件
        rm -f /tmp/wechat.pem; \
    else \
        echo "⚠️ 未能获取微信证书，将使用系统默认证书" && \
        # 如果获取失败，安装一个更全面的 CA 证书包作为备选
        wget -O /tmp/cacert.pem https://curl.se/ca/cacert.pem && \
        keytool -import -noprompt -trustcacerts \
            -file /tmp/cacert.pem \
            -keystore $JAVA_HOME/lib/security/cacerts \
            -storepass changeit \
            -alias mozilla-ca-bundle && \
        rm -f /tmp/cacert.pem && \
        echo "✅ 已安装备选 CA 证书包"; \
    fi && \
    echo "=== 微信证书配置完成 ==="

# 验证证书安装
RUN echo "=== 验证证书安装 ===" && \
    echo "已安装的微信相关证书:" && \
    keytool -list -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit | grep -i wechat || \
    echo "未找到微信证书，但其他证书已配置"

# 暴露端口（Spring Boot 默认 8080）
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
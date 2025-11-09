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
# 运行阶段（详细证书处理）
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
    openssl \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 详细的微信 jscode2session 证书处理
RUN echo "=== 详细处理微信 jscode2session 证书 ==="

# 1. 获取完整的证书链
RUN openssl s_client -connect api.weixin.qq.com:443 -servername api.weixin.qq.com -showcerts < /dev/null 2>/dev/null > /tmp/wechat-full-chain.txt

# 2. 提取所有证书（可能有多个）
RUN cat /tmp/wechat-full-chain.txt | \
    awk '/BEGIN CERT/{a++} /BEGIN CERT/,/END CERT/{print > "/tmp/cert" a ".pem"}'

# 3. 导入每个证书到 JDK 信任库
RUN for cert in /tmp/cert*.pem; do \
    if [ -f "$cert" ] && [ -s "$cert" ]; then \
        alias_name="wechat-cert-$(basename $cert .pem)" && \
        echo "导入证书: $alias_name" && \
        keytool -import -v \
            -alias "$alias_name" \
            -keystore $JAVA_HOME/lib/security/cacerts \
            -file "$cert" \
            -storepass changeit \
            -noprompt && \
        echo "$alias_name 导入成功"; \
    fi; \
    done

# 4. 清理临时文件
RUN rm -f /tmp/wechat-full-chain.txt /tmp/cert*.pem

# 暴露端口
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
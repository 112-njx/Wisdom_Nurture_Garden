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

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 从上一阶段复制 Jar 文件到镜像中
COPY --from=builder /app/target/*.jar app.jar

# 设置时区为东八区（可选）
ENV TZ=Asia/Shanghai

# 更新 CA 证书以信任微信证书
RUN apt-get update && \
    apt-get install -y ca-certificates wget && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# 手动更新到最新的 CA 证书包（更彻底）
RUN wget -O /tmp/cacert.pem https://curl.se/ca/cacert.pem && \
    keytool -import -noprompt -trustcacerts -file /tmp/cacert.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit && \
    rm /tmp/cacert.pem

# 暴露端口（Spring Boot 默认 8080）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
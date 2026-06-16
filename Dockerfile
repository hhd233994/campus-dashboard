# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# 先下载依赖，利用 Docker 缓存
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre

WORKDIR /app

# 从构建阶段复制 jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 时区与编码
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM 参数优化（限制内存，适配 Railway 免费环境）
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseG1GC -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=prod"]

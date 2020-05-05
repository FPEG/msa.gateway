# 基础镜像
FROM openjdk:14-alpine
# 复制文件，源位置为相对 Docker 工作目录的位置，这个在 Gradle 脚本中指定
ARG FILENAME
COPY "dependency/$FILENAME" "/app/app.jar"
# 启动容器时运行的命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar","--spring.profiles.active=test"]
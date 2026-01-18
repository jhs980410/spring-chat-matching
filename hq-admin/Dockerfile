FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 동일한 메모리 제한 전략 적용
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Seoul"

# HQ Admin 서비스 포트 명시
EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# ❗ 메모리 최적화: t2.micro의 1GB 램을 고려해 256~512MB로 제한
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Seoul"

# 매니저 서비스 포트 명시
EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
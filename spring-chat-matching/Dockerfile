# 1. Build stage (프리티어 사양을 고려해 빌드는 GitHub Actions에서 수행하고 여기서는 실행만 합니다)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 복사 (GitHub Actions 빌드 결과물)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 메모리 제한 옵션 추가 (t2.micro 생존 전략)
# -Xms: 시작 메모리, -Xmx: 최대 메모리 제한
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Seoul"

# 5. 실행
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
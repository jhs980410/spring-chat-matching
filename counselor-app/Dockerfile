# 1단계: 빌드 (메모리 절약을 위해 가벼운 alpine 이미지 사용)
FROM node:20-alpine AS build
WORKDIR /app

# 의존성 설치 (캐싱 활용)
COPY package*.json ./
RUN npm install

# 소스 복사 및 빌드
COPY . .
# Vite 빌드 결과물은 기본적으로 'dist' 폴더에 생성됩니다.
RUN npm run build

# 2단계: 서빙 (실행 시점에는 Nginx만 사용)
FROM nginx:alpine
# 빌드 단계에서 생성된 정적 파일들만 복사
COPY --from=build /app/dist /usr/share/nginx/html
# React Router(SPA) 사용을 위한 Nginx 설정 파일 복사 (아래 2번 참고)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
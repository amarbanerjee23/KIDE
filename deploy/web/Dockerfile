FROM node:22-bookworm-slim AS builder
ARG VITE_KIDE_API_ORIGIN
ARG VITE_KIDE_LSP_ORIGIN
ARG VITE_FIREBASE_API_KEY
ARG VITE_FIREBASE_PROJECT_ID
ENV VITE_KIDE_API_ORIGIN=${VITE_KIDE_API_ORIGIN}
ENV VITE_KIDE_LSP_ORIGIN=${VITE_KIDE_LSP_ORIGIN}
ENV VITE_FIREBASE_API_KEY=${VITE_FIREBASE_API_KEY}
ENV VITE_FIREBASE_PROJECT_ID=${VITE_FIREBASE_PROJECT_ID}

WORKDIR /src/web
COPY web/package.json ./
RUN npm install --no-audit --no-fund
COPY web/ ./
RUN npm run build

FROM nginx:1.27.5-alpine
COPY deploy/web/nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=builder /src/web/dist/ /usr/share/nginx/html/

ENV PORT=8080
EXPOSE 8080

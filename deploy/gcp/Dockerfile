FROM node:22-bookworm-slim AS web-builder
WORKDIR /src/web
COPY web/package.json web/package-lock.json ./
RUN npm install --no-audit --no-fund
COPY web/ ./
RUN npm run build

FROM maven:3.9.11-eclipse-temurin-21 AS kide-builder
WORKDIR /src
COPY . .
RUN mvn -B -ntp -DskipTests clean verify

FROM eclipse-temurin:21-jre-jammy AS runtime
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get install -y --no-install-recommends nginx curl gettext-base ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 kide \
    && useradd --uid 10001 --gid 10001 --create-home --shell /bin/bash kide \
    && mkdir -p /opt/kide /usr/share/nginx/html /data \
    && chown -R kide:kide /data /var/lib/nginx /var/log/nginx

COPY --from=web-builder /src/web/dist/ /usr/share/nginx/html/
COPY --from=kide-builder /src/releng/com.kide.languageserver.repository/target/products/com.kide.languageserver.product/linux/gtk/x86_64/ /opt/kide/
COPY deploy/gcp/nginx.conf.template /opt/kide-cloud/nginx.conf.template
COPY deploy/gcp/entrypoint.sh /usr/local/bin/kide-cloud-run-entrypoint

RUN chmod 0755 /usr/local/bin/kide-cloud-run-entrypoint /opt/kide/kide-languageserver-headless \
    && chown -R kide:kide /opt/kide-cloud /usr/share/nginx/html

USER 10001:10001
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/kide-cloud-run-entrypoint"]

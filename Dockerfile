# --- Build stage: compile Tailwind CSS ---
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies first (better layer caching)
COPY package.json package-lock.json ./
RUN npm ci

# Copy the sources Tailwind needs to scan, then build the stylesheet
COPY tailwind.config.js ./
COPY src ./src
COPY index.html ./
RUN npm run build

# --- Runtime stage: serve the static site with nginx ---
FROM nginx:1.27-alpine AS runtime

# Site configuration (gzip, cache headers)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Static assets
COPY index.html /usr/share/nginx/html/index.html
COPY --from=build /app/assets /usr/share/nginx/html/assets

# EasyPanel proxies to this port
EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -q --spider http://localhost:80/ || exit 1

# nginx:alpine already runs `nginx -g 'daemon off;'` by default

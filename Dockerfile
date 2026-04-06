# syntax=docker/dockerfile:1.7

########################
# Stage 1: extract layered Spring Boot jar
########################
# FROM bellsoft/liberica-openjre-debian:25-cds AS builder
# FROM cgr.dev/chainguard/jdk:latest AS extractor
FROM bellsoft/liberica-openjre-debian:25-cds AS extractor
WORKDIR /workspace

# Maven build output
ARG JAR_FILE=target/*.jar

# Copy the Spring Boot fat jar
COPY ${JAR_FILE} application.jar

# Extract Spring Boot layers
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

########################
# Stage 2: create Java 25 AOT cache
########################
#FROM cgr.dev/chainguard/jdk:latest AS aotcache
FROM bellsoft/liberica-openjre-debian:25-cds AS aotcache

WORKDIR /application

# Copy extracted application layout
COPY --from=extractor /workspace/extracted/dependencies/ ./
COPY --from=extractor /workspace/extracted/spring-boot-loader/ ./
COPY --from=extractor /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extractor /workspace/extracted/application/ ./

# Optional training env; tune for your app if needed
ENV SPRING_MAIN_WEB_APPLICATION_TYPE=none

# Generate AOT cache
RUN java \
    -XX:AOTCacheOutput=app.aot \
    -Dspring.context.exit=onRefresh \
    -jar application.jar

########################
# Stage 3: build a minimized custom Java runtime
########################
#FROM cgr.dev/chainguard/jdk:latest AS jrebuild
FROM bellsoft/liberica-openjre-debian:25-cds AS jrebuild

WORKDIR /workspace

ARG JAR_FILE=target/sbom-vault-backend.jar
COPY ${JAR_FILE} application.jar

# Determine required Java modules and build a trimmed runtime.
# jdk.crypto.ec is commonly needed for TLS.
RUN jdeps \
      --ignore-missing-deps \
      --recursive \
      --multi-release 25 \
      --print-module-deps \
      application.jar \
      > modules.txt \
 && jlink \
      --add-modules "$(cat modules.txt),jdk.crypto.ec" \
      --strip-debug \
      --no-header-files \
      --no-man-pages \
      --compress=2 \
      --output /opt/jre

########################
# Stage 4: final hardened runtime image
########################
FROM cgr.dev/chainguard/jre:latest

WORKDIR /application

# Replace stock runtime with the minimized runtime
COPY --from=jrebuild /opt/jre /opt/jre
ENV JAVA_HOME=/opt/jre
ENV PATH=/opt/jre/bin:/usr/sbin:/usr/bin:/sbin:/bin

# Copy extracted Spring Boot layers in cache-friendly order
COPY --from=extractor /workspace/extracted/dependencies/ ./
COPY --from=extractor /workspace/extracted/spring-boot-loader/ ./
COPY --from=extractor /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extractor /workspace/extracted/application/ ./

# Copy generated Java 25 AOT cache
COPY --from=aotcache /application/app.aot ./app.aot

# Conservative runtime defaults
ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom"

EXPOSE 8080

ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "application.jar"]
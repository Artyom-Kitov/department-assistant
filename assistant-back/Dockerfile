#FROM alpine:edge AS build
#
#WORKDIR /app
#
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#
#RUN apk update && apk add --no-cache openjdk21-jdk
#
#RUN chmod +x mvnw
#RUN ./mvnw dependency:go-offline -B
#
#COPY src ./src
#
#RUN ./mvnw clean package -DskipTests
#
#FROM alpine:edge
#
#RUN apk update && apk add --no-cache openjdk21-jre
#
#COPY --from=build /app/target/*.jar /app/app.jar
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "/app/app.jar"]

FROM alpine:edge AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN apk update && apk add --no-cache openjdk21-jdk maven

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src

# Добавляем возможность передавать параметры сборки через ARG
ARG DEBUG=false
ARG SKIP_TESTS=true

RUN if [ "$DEBUG" = "true" ]; then \
      ./mvnw clean package -DskipTests=$SKIP_TESTS -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"; \
    else \
      ./mvnw clean package -DskipTests=$SKIP_TESTS; \
    fi

FROM alpine:edge

RUN apk update && apk add --no-cache openjdk21-jre curl

COPY --from=build /app/target/*.jar /app/app.jar

# Открываем порты для приложения и отладки
EXPOSE 8080 5006

# Добавляем healthcheck для проверки работоспособности
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Запускаем приложение с возможностью отладки
CMD if [ "$DEBUG" = "true" ]; then \
      exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006 -jar /app/app.jar; \
    else \
      exec java -jar /app/app.jar; \
    fi
FROM eclipse-temurin:21-jdk

RUN addgroup --system --gid 1002 appgroup \
    && adduser --system --uid 1002 --ingroup appgroup appuser

WORKDIR /app

COPY target/kafka-on-k8s-producer-0.0.1.jar app.jar

USER 1002

ENTRYPOINT ["java", "-jar", "app.jar"]

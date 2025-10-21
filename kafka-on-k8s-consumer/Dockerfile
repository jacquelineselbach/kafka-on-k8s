FROM eclipse-temurin:21-jdk

RUN addgroup --system --gid 1001 appgroup \
    && adduser --system --uid 1001 --ingroup appgroup appuser

WORKDIR /app

COPY target/kafka-on-k8s-consumer-0.0.1.jar app.jar

USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]

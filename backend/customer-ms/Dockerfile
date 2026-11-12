
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app


COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
# Os testes ficam fora do build da imagem: o contextLoads precisa de um Postgres real
RUN ./gradlew bootJar -x test --no-daemon


FROM eclipse-temurin:26-jre
WORKDIR /app

COPY --from=build /app/build/libs/customer-ms-0.0.1-SNAPSHOT.jar customer-ms.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "customer-ms.jar"]

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

# Curinga: o nome do jar muda com a versão do build.gradle (só o bootJar é gerado)
COPY --from=build /app/build/libs/*.jar customer-ms.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "customer-ms.jar"]
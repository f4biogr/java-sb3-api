FROM openjdk:17-oracle AS build
COPY . /app/
WORKDIR /app
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM bellsoft/liberica-openjdk-alpine
WORKDIR /app
COPY --from=0 /app/target/api-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "api-0.0.1-SNAPSHOT.jar"]

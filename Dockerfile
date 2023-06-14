FROM eclipse-temurin:17-jdk as build

ARG JAR_FILE=target/*.jar
WORKDIR /builder
COPY . .
RUN ./mvnw clean package -Dmaven.test.skip=true
RUN java -Djarmode=layertools -jar ${JAR_FILE} extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /application
COPY --from=build /builder/dependencies/ ./
COPY --from=build /builder/spring-boot-loader/ ./
COPY --from=build /builder/snapshot-dependencies/ ./
COPY --from=build /builder/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

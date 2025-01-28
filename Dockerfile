FROM openjdk:17-jdk-alpine
LABEL authors="Emeka Kanu"
COPY target/symptom-checker-0.0.1-SNAPSHOT.jar symptom-checker.jar
ENTRYPOINT ["java","-jar","/symptom-checker.jar"]

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY JobEngineApplication.java index.html ./
RUN javac JobEngineApplication.java
EXPOSE 8080
CMD ["java", "JobEngineApplication"]

## Используем образ с JDK (например, OpenJDK 23)
#FROM openjdk:23-jdk-slim
#
## Устанавливаем Maven
#RUN apt-get update && apt-get install -y maven
#
## Устанавливаем рабочую директорию
#WORKDIR /app
#
## Копируем pom.xml в контейнер
#COPY pom.xml .
#
## Загружаем зависимости Maven
#RUN mvn dependency:go-offline
#
## Копируем весь исходный код
#COPY . .
#
## Собираем проект с помощью Maven
#RUN mvn clean package -DskipTests
#
## Указываем команду для запуска приложения
#CMD ["java", "-jar", "target/EurekaServer-0.0.1-SNAPSHOT.jar"]

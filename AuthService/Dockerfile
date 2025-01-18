## Используем официальный образ OpenJDK 23 для сборки и запуска приложения
#FROM openjdk:23-jdk-slim as builder
#
## Указываем рабочую директорию внутри контейнера
#WORKDIR /app
#
## Копируем файл pom.xml и загружаем зависимости
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
## Копируем исходный код проекта
#COPY src ./src
#
## Собираем приложение
#RUN mvn clean package -DskipTests
#
## Используем тот же образ для запуска приложения
#FROM openjdk:23-jdk-slim
#
## Указываем рабочую директорию
#WORKDIR /app
#
## Копируем артефакт из предыдущего этапа
#COPY --from=builder /app/target/authservice.jar /app/authservice.jar
#
## Открываем порт, на котором будет работать приложение
#EXPOSE 8081
#
## Команда для запуска приложения
#ENTRYPOINT ["java", "-jar", "authservice.jar"]

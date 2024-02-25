# Руководство по использованию стартера для аудита пользователя

Этот стартер предоставляет возможность выполнять аудит пользовательских действий в вашем приложении. Для этого необходимо следовать следующим шагам:

## Шаг 1: Подключение стартера к проекту

Добавьте зависимость нашего стартера в файл `pom.xml` вашего проекта:

```xml
<dependency>
    <groupId>com.denknd</groupId>
    <artifactId>action-audit-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version> 
</dependency>
```
или build.gradle
```groovy
dependencies {
    implementation 'com.denknd:action-audit-starter:0.0.1-SNAPSHOT' 
}
```
## Шаг 2: Создание сервиса для получения информации о пользователе
Создайте сервис, реализующий интерфейс UserIdentificationService, который предоставляет информацию о пользователе, выполняющем действие:
```java
package com.denknd.audit.api;

public interface UserIdentificationService {
    Long getUserId();
}
```
## Шаг 3: Настройка подключения к базе данных для хранения данных аудита

Добавьте следующую конфигурацию в ваш файл application.yml (или application.properties), чтобы указать данные вашей базы данных, где будут храниться записи аудита:

```yaml
audit:
  db:
    url: jdbc:postgresql://localhost:5433/auditDb
    username: auditUser
    password: auditPassword
    driver-class-name: org.postgresql.Driver

```
## Шаг 4: Пометьте методы, требующие аудита, аннотацией `@AuditRecording`
Отметьте методы, которые требуется отслеживать для аудита, аннотацией @AuditRecording, предоставляя краткое описание действия, которое выполняется методом.
Пример использования:
```java
package com.denknd.audit.example;

import com.denknd.audit.api.AuditRecording;

public class ExampleService {

    @AuditRecording("Метод для создания нового пользователя")
    public void createUser() {
        // Логика создания пользователя
    }

    @AuditRecording("Метод для обновления информации о пользователе")
    public void updateUser() {
        // Логика обновления информации о пользователе
    }
}
```
Это все! Теперь вы можете использовать этот стартер для отслеживания пользовательских действий в вашем приложении.
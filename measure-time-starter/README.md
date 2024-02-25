# Spring Boot Starter для измерения времени выполнения методов

Этот Spring Boot Starter позволяет измерять время выполнения методов, помеченных определенными аннотациями, и выводить соответствующие сообщения в логи.

## Использование
1. Добавьте зависимость нашего стартера в файл pom.xml вашего проекта:
```xml
<dependency>
    <groupId>com.denknd</groupId>
    <artifactId>measure-time-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version> <!-- замените на актуальную версию -->
</dependency>
```
или build.gradle
```groovy
dependencies {
    implementation 'com.denknd:measure-time-starter:0.0.1-SNAPSHOT' // Замените версию на актуальную
}
```
2. На класс конфигурации вашего приложения добавьте аннотацию @EnableMeasureTime, чтобы включить функциональность измерения времени выполнения:
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.denknd.time.api.EnableMeasureTime;

@EnableMeasureTime
@SpringBootApplication
public class YourApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```
3. Пометьте методы, время выполнения которых вы хотите измерить, аннотацией @MeasureExecutionTime:
```java
import org.springframework.stereotype.Service;
import com.denknd.time.api.MeasureExecutionTime;

@Service
public class YourService {

    @MeasureExecutionTime
    public void yourMethod() {
        // Ваш код здесь
    }
}
```
После запуска приложения в логах будут выводиться сообщения о времени выполнения методов, помеченных аннотацией `@MeasureExecutionTime`.
### Пример вывода
```
14:38:34.332 [http-nio-8081-exec-3] INFO  c.d.t.a.MeasureExecutionTimeAspect - Метод: MeterReadingController.getHistoryMeterReading(..) Время работы метода: 54 milliseconds
```
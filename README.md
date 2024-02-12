# Monitoring Service

## Описание

Приложение для приема и хранения показаний счетчиков

## Сборка и запуск

Чтобы собрать и запустить проект, убедитесь, что у вас установлены следующие зависимости:

- Gradle
- Java
- Docker
- Docker Compose
  


### Шаг 1: Создание базы данных
Чтобы приложение работало, необходимо создать базу данных с помощью Docker Compose.
1. Убедитесь, что Docker и Docker Compose установлены и запущены в вашей системе.
2. Перейдите в корневой каталог проекта и выполните следующую команду:
```shell
docker-compose up -d
```
### Шаг 2: Создание проекта

1. Откройте терминал и перейдите в корневой каталог проекта.

2. Выполните команду сборки с помощью Gradle:

```shell
gradle build
```
### Шаг 3: Запустите проект

1. После успешной сборки выполните команду:

```shell
./gradlew jettyRun
```

Это запустит ваше приложение и сделает его доступным для использования.

## Доступные запросы:

### Регистрация(roles: USER):
Чтобы зарегистрировать нового пользователя, отправьте POST запрос на следующий эндпоинт:
```
POST http://localhost:8081/users/signup
```
И передайте следующие данные в формате JSON:
```json
{
"email": "test@gmail.com",
"password": "123",
"firstName": "Den",
"lastName": "LastName"
}
```
### Вход (аутентификация) пользователя(roles: USER, ADMIN):

Чтобы войти в систему и получить доступ к защищенным ресурсам, отправьте POST запрос на следующий эндпоинт:
```
POST http://localhost:8081/auth/login
```
И добавьте следующий заголовок `Authorization`, содержащий базовую аутентификацию в формате Base64, где email и пароль разделены двоеточием:
```
Authorization: Basic dGVzdEBtYWlsLmNvbToxMjM=
```
Этот запрос выполнит вход в систему и вернет токен доступа, который будет храниться в Cookie для последующего использования при доступе к защищенным ресурсам.

### Выход из системы и блокировка токена доступа(roles: USER, ADMIN)

Чтобы выйти из системы и завершить текущую сессию, а также заблокировать токен доступа, отправьте POST запрос на следующий эндпоинт:
```
POST http://localhost:8081/auth/logout
```
Добавьте заголовок `Cookie`, содержащий токен доступа, полученный при входе в систему:
```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```
Этот запрос завершит текущую сессию пользователя и выйдет из системы, а также заблокирует токен доступа для дополнительной защиты.

### Получение информации о пользователе(roles: USER, ADMIN)

Чтобы получить информацию о пользователе, отправьте GET запрос на следующий эндпоинт:
```
GET http://localhost:8081/users/user
```
Добавьте заголовок `Cookie`, содержащий токен доступа пользователя:

```
Cookie: __Host-auth-token=eyJraWQiOiIzYjEwMDk1Ny03NDFmLTRlZWEtYjE2Yy0yODgwZWMxMDk0OWUiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..87nB33M46pVwDVWy.IKEWtwSImCDPC--MEYV66f45M_BVXHZ_wZ4Tb-njqE7dRBh3p3dcSiLex8YCqb0-LdLbzDWIoeqYVNCT-IUqhLJtvcie1vVUIOu0VXlqTAhqbX00oNh_222MMp9B6Oo9lAFI6R00h5Lb7d9K0FAyVyw55O0qDIKOVjrZ5E20eUid_t1kDYM.0iIKu1jeH7YtjHZsXNAIUw
```
Для роли "Юзер" (обычного пользователя) эта команда вернет информацию о самом пользователе.

Для роли "Админ" (администратора) вы должны добавить дополнительные параметры в запросе:

- Для получения информации о пользователе по его идентификатору (userId), добавьте параметр `id`:
```
GET http://localhost:8081/users/user?id={userId}
```
- Для получения информации о пользователе по его электронной почте (email), добавьте параметр `email`:
```
GET http://localhost:8081/users/user?email={email}
```
Эти дополнительные параметры позволяют администратору получить информацию о других пользователях по их идентификатору или электронной почте.

### Добавление адреса(roles: USER)

Чтобы добавить новый адрес, отправьте POST запрос на следующий эндпоинт:
```
POST http://localhost:8081/address
```

И добавьте следующие данные в формате JSON:

```json
{
  "region": "Мурманская область",
  "city": "Кандалакша",
  "street": "Кировская",
  "house": "31",
  "apartment": "5",
  "postalCode": 184040
}
```
Не забудьте добавить заголовок Cookie, содержащий токен доступа пользователя:
```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```
Этот запрос добавит новый адрес для пользователя с ролью USER.

### Отправка показаний счетчика (роли: USER)

Чтобы отправить показания счетчика, выполните POST запрос на следующий эндпоинт:

```
POST http://localhost:8081/meter-readings/send
```

И добавьте следующие данные в формате JSON:

```json
{
  "addressId": 12,
  "typeMeterId": 3,
  "meterValue": 123.456
}
```
- addressId: идентификатор вашего адреса,
- typeMeterId: идентификатор типа показаний, которые вы хотите отправить,
- meterValue: показания счетчика.

Не забудьте добавить заголовок Cookie, содержащий токен доступа пользователя:
```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```
Этот запрос отправит показания счетчика для указанного адреса.
### История показаний счетчика (роли: USER, ADMIN)

Чтобы получить историю показаний счетчика, отправьте GET запрос на следующий эндпоинт:
```
GET http://localhost:8081/meter-readings/history
```

И добавьте следующий заголовок `Cookie`, содержащий токен доступа пользователя:

```
Cookie: __Host-auth-token=eyJraWQiOiIzYjEwMDk1Ny03NDFmLTRlZWEtYjE2Yy0yODgwZWMxMDk0OWUiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..87nB33M46pVwDVWy.IKEWtwSImCDPC--MEYV66f45M_BVXHZ_wZ4Tb-njqE7dRBh3p3dcSiLex8YCqb0-LdLbzDWIoeqYVNCT-IUqhLJtvcie1vVUIOu0VXlqTAhqbX00oNh_222MMp9B6Oo9lAFI6R00h5Lb7d9K0FAyVyw55O0qDIKOVjrZ5E20eUid_t1kDYM.0iIKu1jeH7YtjHZsXNAIUw
```

Для администратора обязательный параметр:

- `userId`: покажет историю показаний для указанного пользователя.

Необязательные параметры для всех пользователей:

- `typeId`: типы показаний, по которым нужна история (пример: typeId=1,2,3). Если не передан, выведется история по всем типам.
- `addrId`: идентификатор адреса. Если не передан, выведется история по всем доступным пользователю адресам.
- `start_date`: дата, от которой нужна история (паттерн: yyyy-MM). Если не передан, выведется история с начала подачи показаний.
- `end_date`: дата, по которую нужна история (паттерн: yyyy-MM). Если не передан, выведется история по последним показаниям.

Этот запрос вернет историю показаний счетчика с учетом указанных параметров.

Пример для администратора:
```
GET http://localhost:8081/meter-readings/history?userId=123&typeId=1,2,3&addrId=456&start_date=2024-01&end_date=2024-02
```
### Получение актуальных показаний счетчика (роли: USER, ADMIN)

Чтобы получить актуальные показания счетчика, выполните GET запрос на следующий эндпоинт:

```
GET http://localhost:8081/meter-readings/get-meter-readings
```

Не забудьте добавить заголовок `Cookie`, содержащий токен доступа пользователя:

```
Cookie: __Host-auth-token=eyJraWQiOiIzYjEwMDk1Ny03NDFmLTRlZWEtYjE2Yy0yODgwZWMxMDk0OWUiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..87nB33M46pVwDVWy.IKEWtwSImCDPC--MEYV66f45M_BVXHZ_wZ4Tb-njqE7dRBh3p3dcSiLex8YCqb0-LdLbzDWIoeqYVNCT-IUqhLJtvcie1vVUIOu0VXlqTAhqbX00oNh_222MMp9B6Oo9lAFI6R00h5Lb7d9K0FAyVyw55O0qDIKOVjrZ5E20eUid_t1kDYM.0iIKu1jeH7YtjHZsXNAIUw
```

Для администратора обязательный параметр:

- `userId`: пользователь, для которого нужно вывести показания.

Необязательные параметры для всех пользователей:

- `typeId`: типы показаний, по которым нужны показания (пример: typeId=1,2,3). Если не передан, будут выведены показания по всем типам показаний.
- `addrId`: идентификатор адреса. Если не передан, будут выведены показания по всем доступным пользователю адресам.
- `date`: если параметр передан, будут выведены показания именно по этой дате, а не актуальные (паттерн: yyyy-MM).

Этот запрос вернет актуальные показания счетчика в зависимости от переданных параметров.
### Добавление нового типа показаний счетчика (роли: ADMIN)

Чтобы добавить новый тип показаний счетчика, выполните POST запрос на следующий эндпоинт:
```
POST http://localhost:8081/meter-types
```

И добавьте следующие данные в формате JSON:

```json
{
  "typeCode": "exampleTypeCode",
  "typeDescription": "exampleTypeDescription",
  "metric": "exampleMetric"
}
```
- `typeCode`: краткое обозначение для данного типа показаний.
- `typeDescription`: небольшое описание типа показаний.
- `metric`: единица измерения данного типа показаний.

Не забудьте добавить заголовок Cookie, содержащий токен доступа администратора:
```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```
Этот запрос добавит новый тип показаний счетчика в систему.

### Получение информации о всех типах показаний счетчика (роли: USER, ADMIN)

Чтобы получить информацию о всех типах показаний счетчика, выполните GET запрос на следующий эндпоинт:

```
GET http://localhost:8081/meter-types
```

Не забудьте добавить заголовок `Cookie`, содержащий токен доступа пользователя:

```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```

Этот запрос вернет информацию о всех типах показаний счетчика, доступных в системе.
### Дополнение информации о счетчике(роли: ADMIN)

Чтобы дополнить информацию о счетчике, выполните PUT запрос на следующий эндпоинт:
```
PUT http://localhost:8081/counter-info
```

И добавьте следующие данные в формате JSON:

```json
{
  "addressId": 123456789,
  "typeMeterId": 987654321,
  "serialNumber": "ABCD1234",
  "meterModel": "ExampleModel"
}
```
- `addressId`: идентификатор адреса, к которому относится счетчик.
- `typeMeterId`: идентификатор типа показаний счетчика.
- `serialNumber`: серийный номер счетчика.
- `meterModel`: модель счетчика.
Не забудьте добавить заголовок Cookie, содержащий токен доступа пользователя:
```
Cookie: __Host-auth-token=eyJraWQiOiI5YWQ0NDAxMi05ZjM2LTRmMjItYTY3Zi1jM2YyNGMzMGUzYWMiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..PmjPaTMS1cU62Wyh.PGKfVhJNDmOUVdqCOb1v1XLMQ0AXnvssrvkJi9wxizYLK3lAtcTkNckRqj4kVKuIWQl1oKRQkv8qnxtiV3mLGJw38Wc-M-A3FGmkdCmWmY9VRTL5zmfFD3Uxp1lj-WguyVHhcCW2NpsajS_eMUxZOOyWIHFAElvV04EJrp9leEGku9YFIQo.hynJ32B4N4K59YapnnZJxw
```
Этот запрос дополнит информацию о счетчике в системе.

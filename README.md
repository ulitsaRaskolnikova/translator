# translator
Данный проект является тестовым заданием для отбора на Финтех-курсы от Т-Банка.
### Технологии
1. `Java`
2. `Spring Boot`
3. `PostgreSQL`
4. `YandexTranslateAPI`
### Описание
Приложение является REST-сервисом, которое пословно переводит текстовую строку с помощью `YandexTranslateAPI`, используя `RestTemplate`.
В приложении используется СУБД `PostgreSQL`, работа с которой происходит посредством чистого `JDBC`. В базе данных есть одна сущность `translation`, которая хранит исходный текст, переведённый текст, исходный язык, язык перевода, ip пользователя и время запроса.
### Сборка и старт
1. Клонируйте репозиторий:
```
git clone https://github.com/ulitsaRaskolnikova/translator.git
```
2. В файле `src/main/resources/application.properties` впишите свои значения в указанные поля.
```properties
spring.application.name=translator  
  
server.port=8081
  
application.endpoint.translate=api/v1/translate  
  
yandex.cloud.url=https://translate.api.cloud.yandex.net/translate/v2/translate  
yandex.cloud.token=<Ваш IAM-токен от YandexCloud>
yandex.cloud.folder.id=<Ваш id от каталога на YandexCloud>
  
datasource.url=jdbc:postgresql://localhost:5432/postgres  
datasource.username=<Ваше имя пользователя от учётной записи базы данных>  
datasource.password=<Ваш пароль от учётной записи базы данных>  
datasource.driver-class-name=org.postgresql.Driver  
datasource.script-file-path=src/main/resources/schema.sql
```
3. Для сборки проекта используйте команду `./mvnw clean package` в корневом каталоге проекта `translator/`.
4. Для запуска проекта используйте команду `java -jar target/translator-0.0.1-SNAPSHOT.jar` в корневом каталоге проекта `translator/`.
### Взаимодействие с приложением
Взаимодействие с приложением происходит посредством HTTP-запроса вида POST, в теле которого передаётся JSON с нужными данными. В ответ приложение возвращает HTTP-ответ, в теле которого находится JSON c полем `message`. Если запрос корректный и никаких проблем не возникло, то приложение вернёт ответ с HTTP-кодом, равным 200, и JSON, в поле `message` которого находится перевод строки. Если возникли проблемы на стороне клиента или на стороне сервера, то вернётся ответ с HTTP-кодом, равным 4xx или 5xx, и JSON, в поле `message` которого будет лежать описание ошибки.

| Тип запроса | Путь               |
| ----------- | ------------------ |
| POST        | ./api/v1/translate |
#### Пример запроса
Путь: `localhost:8081/api/v1/translate`
```json
{

"sourceLang": "ru",

"targetLang": "en",

"text": "ты у меня самая лучшая"

}
```
#### Пример ответа
Код: 200
```json
{

"message": "you at me the most the best"

}
```

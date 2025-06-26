# Chat Backend

**Приложение на Spring Boot для чат-системы с JWT-аутентификацией и WebSocket**

## Оглавление

* [Описание](#описание)
* [Установка и запуск](#установка-и-запуск)
* [Конфигурация](#конфигурация)
* [REST API](#rest-api)
* [WebSocket API](#websocket-api)
* [Postman Collection](#postman-collection)

---

## Описание

Эта служба реализует бэкенд чат-приложения с возможностями:

* Регистрация и аутентификация пользователей через JWT.
* Создание и хранение приватных чат-комнат между двумя пользователями.
* Отправка и получение сообщений (сохранение в базе данных PostgreSQL).
* Режим реального времени с помощью WebSocket (STOMP).

## Установка и запуск

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/ysabina/chat-backend.git
   cd chat-backend
   ```
2. Запустите базу данных PostgreSQL (порт по умолчанию 5433) через Docker:

   ```bash
   docker-compose up -d
   ```
3. Соберите и запустите приложение:

   ```bash
   ./gradlew bootRun
   ```
4. По умолчанию сервер доступен на `http://localhost:8080`.

## Конфигурация

В файле `src/main/resources/application.yml` задаются параметры подключения к БД и секрет для JWT:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/chat
    username: chat_user
    password: secret
jwt:
  secret: <ваш-base64-секрет-не-короче-256-бит>
```

## REST API

### 1. Регистрация пользователя

* **Метод**: `POST /auth/register`
* **Тело запроса**:

  ```json
  {
    "username": "user1",
    "password": "pass123"
  }
  ```
* **Ответ**: `200 OK` при успешной регистрации.

### 2. Логин пользователя

* **Метод**: `POST /auth/login`
* **Тело запроса**:

  ```json
  {
    "username": "user1",
    "password": "pass123"
  }
  ```
* **Ответ**: `200 OK` и JSON:

  ```json
  {
    "token": "<JWT-токен>"
  }
  ```

### 3. Получение списка комнат пользователя

* **Метод**: `GET /chat/rooms/{userId}`
* **Параметры пути**:

  * `userId` — ID пользователя.
* **Заголовок**: `Authorization: Bearer <JWT-токен>`
* **Ответ**: список объектов `ChatRoomDto`:

  ```json
  [
    {
      "id": 10,
      "name": "2_4",
      "createdAt": "2025-06-25T13:11:08.276Z",
      "participants": ["user2","user4"]
    }
  ]
  ```

### 4. Получение истории сообщений комнаты

* **Метод**: `GET /chat/rooms/{roomId}/messages`
* **Параметры пути**:

  * `roomId` — ID комнаты.
* **Заголовок**: `Authorization: Bearer <JWT-токен>`
* **Ответ**: список `MessageDto`:

  ```json
  [
    {
      "id": 7,
      "roomId": 10,
      "sender": "user2",
      "content": "Привет!",
      "sentAt": "2025-06-25T14:00:00Z"
    }
  ]
  ```

### 5. Отправка сообщения

* **Метод**: `POST /chat/rooms/{roomId}/messages`
* **Параметры пути**:

  * `roomId` — ID комнаты (или произвольно любое число, если комнаты нет — она создастся автоматически при первом сообщении).
* **Query Params**:

  * `senderId` — ID отправителя.
  * `content` — текст сообщения.
* **Заголовок**: `Authorization: Bearer <JWT-токен>`
* **Ответ**: объект `MessageDto` с данными сохраненного сообщения.

## WebSocket API

* **Endpoint**: `ws://localhost:8080/ws-chat`
* **STOMP destination для отправки**: `/app/chat.send.{roomId}`
* **Топик для подписки**: `/topic/{roomId}`
* **Формат сообщения** (`MessageDto`):

  ```json
  {
    "content": "Привет!"
  }
  ```

## Postman Collection

В директории `postman/ChatBackend.postman_collection.json` находится набор тестовых запросов:

1. **Register** — `POST /auth/register`
2. **Login** — `POST /auth/login`
3. **Get User Rooms** — `GET /chat/rooms/:userId`
4. **Get Room History** — `GET /chat/rooms/:roomId/messages`
5. **Send Message** — `POST /chat/rooms/:roomId/messages`

Для корректной работы в Postman:

* Добавьте переменную окружения `{{baseUrl}} = http://localhost:8080`
* После логина сохраните `{{jwtToken}}` из ответа и установите в `Authorization: Bearer {{jwtToken}}`.

---


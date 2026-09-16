# Пример работы с API

Запустите оба приложения и инфраструктуру по README. В примерах используются одноразовые демонстрационные пароли; выберите собственные для своей установки. Значения `<...>` замените результатами запросов.

1. Авторизуйтесь администратором (`ADMIN_PASSWORD` — переменная вашего терминала):

```sh
curl -i -c admin.cookies -X POST http://localhost:8080/login \
  --data-urlencode username=admin --data-urlencode "password=$ADMIN_PASSWORD"
```

2. Создайте клиента:

```sh
curl -b admin.cookies -H 'Content-Type: application/json' \
  -d '{"login":"alice","password":"demo-client-pass","name":"Alice","age":25,"gender":"FEMALE","hairColor":"black"}' \
  http://localhost:8080/api/registration/client
```

3. Авторизуйтесь клиентом и откройте счёт:

```sh
curl -i -c client.cookies -X POST http://localhost:8080/login \
  --data-urlencode username=alice --data-urlencode password=demo-client-pass
curl -b client.cookies -X POST http://localhost:8080/api/accounts
```

4. Пополните счёт, затем запросите баланс и историю:

```sh
curl -b client.cookies -X PATCH -H 'Content-Type: application/json' \
  -d '{"accountId":"<account-id>","money":1000.00}' http://localhost:8080/api/accounts/put
curl -b client.cookies 'http://localhost:8080/api/accounts/<account-id>/balance?currencyCode=USD'
curl -b client.cookies http://localhost:8080/api/operation/<account-id>
```

Перевод: `PATCH /api/accounts/transfer` с `senderId`, `recipientId`, `money`. Снятие: `PATCH /api/accounts/withdraw` с `accountId`, `money`. Свои счета: `GET /api/accounts/users/<user-id>`.

Неавторизованный запрос возвращает 401, запрещённый доступ — 403, отсутствующая сущность — 404, некорректная сумма — 400. Если свежего курса нет и поставщик не ответил, запрос конвертации возвращает 503; обычные рублёвые операции продолжают работать.

Файлы cookies содержат сессию: не добавляйте их в Git. Выход: `POST /logout` с соответствующим cookie.

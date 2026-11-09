# Backend — Spring Boot API

Java 21, Maven, Spring Boot 3.4, MySQL, JWT auth.

## Run locally

```bash
# from repo root
cp .env.example .env
docker compose up -d            # MySQL + Redis

cd backend
mvn spring-boot:run             # or run ImpiloHealthApplication in IntelliJ
mvn test                        # tests use in-memory H2, no MySQL needed
```

Set the variables from `.env` as environment variables in your IntelliJ run configuration
(`MYSQL_USER`, `MYSQL_PASSWORD`, `DB_URL`, `JWT_SECRET`). Defaults in `application.yml` are for local dev only.

## Endpoints so far

| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | none | Create account, returns JWT |
| POST | `/api/auth/login` | none | Login, returns JWT |
| GET | `/api/users/me` | Bearer token | Current user profile |
| POST | `/api/ai/chat` | Bearer token | AI health chat (`{"message":"..."}`), remembers last 10 turns |
| GET | `/actuator/health` | none | Health check |

## Huawei integration

Set `AI_PROVIDER=pangu` plus `PANGU_ENDPOINT` / `PANGU_API_KEY` to use Huawei Pangu NLP. Default is `mock`, so everything runs without credentials. Full technology map: [docs/huawei-technologies.md](../docs/huawei-technologies.md).

Register body:
```json
{"fullName":"Jennifer M","email":"j@example.com","password":"password123","gender":"FEMALE","dateOfBirth":"2000-01-01"}
```
`gender`: `FEMALE`, `MALE`, `PREFER_NOT_TO_SAY`. Send the token as `Authorization: Bearer <token>`.

## Package layout (`com.impilohealth`)

```
auth/        register, login
user/        User entity, repository, /me
common/      security (JWT, config), exception handling
cycle/       period tracker          (next)
shop/        products, cart, orders  (next)
donation/    Princess Hamper Project (next)
nutrition/   meal logs, macros       (next)
community/   NGOs, schools, stats    (next)
health/      AI chat, mood           (next)
```

Do not commit `application-local.*` or any real secrets.

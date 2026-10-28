# Backend — Spring Boot API

Generate the project here with Spring Initializr (https://start.spring.io): Maven, Java 17, dependencies: Web, Security, Data JPA, MySQL Driver, Validation, Data Redis.

Suggested package layout (`src/main/java/com/impilohealth/`):

```
auth/        login, register, JWT
health/      personal health AI chat, mood
cycle/       period tracker
shop/        products, cart, orders
donation/    Princess Hamper Project
nutrition/   meal logs, macros
community/   NGOs, schools, impact stats
common/      config, exceptions, security
```

Config via environment variables (see root `.env.example`). Do not commit `application-local.*`.

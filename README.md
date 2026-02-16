# User Service

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green) ![Postgres](https://img.shields.io/badge/PostgreSQL-17-blue)

Микросервис для управления пользователями и их платежными картами. Сервис предоставляет REST API для создания, чтения, обновления и деактивации пользователей и карт.

##  Стек технологий

* **Core:** Java 21, Spring Boot 3.5 (Web, Data JPA, Validation, Cache)
* **Database:** PostgreSQL (Liquibase для миграций)
* **Cache:** Redis (кэширование User + Cards)
* **Tools:** MapStruct, Docker & Docker Compose, TestContainers
* **CI/CD:** GitHub Actions

##  Функциональность

* **CRUD** операции для пользователей и карт.
* **Бизнес-логика:** Лимит — не более 5 карт на пользователя.
* **Поиск:** Пагинация и фильтрация (Spring Data Specifications).
* **Аудит:** Автоматическое заполнение `created_at` / `updated_at`.
* **Кэширование:** Автоматическая инвалидация кэша при обновлении/удалении.

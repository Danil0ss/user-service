DELETE FROM payment_cards;
DELETE FROM users;

-- Пользователь 1
INSERT INTO users (id, name, surname, birth_date, email, active, created_at, updated_at)
VALUES (1, 'John', 'Doe', '1990-01-15', 'john.doe@example.com', true, NOW(), NOW());

-- Карты для пользователя 1
INSERT INTO payment_cards (id, user_id, number, holder, expiration_date, active, created_at, updated_at)
VALUES (101, 1, '1111222233334444', 'John Doe', '2028-12-31 23:59:59', true, NOW(), NOW());

INSERT INTO payment_cards (id, user_id, number, holder, expiration_date, active, created_at, updated_at)
VALUES (102, 1, '5555666677778888', 'John Doe', '2027-10-31 23:59:59', true, NOW(), NOW());

-- Сбрасываем последовательности, чтобы следующий автоинкрементный ID был правильным
ALTER SEQUENCE users_id_seq RESTART WITH 2;
ALTER SEQUENCE payment_cards_id_seq RESTART WITH 103;
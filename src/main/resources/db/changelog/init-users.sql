-- changeset author:folk
-- comment: Создание тестовых пользователей
INSERT INTO "user" (id, name, date_of_birth, password)
VALUES
    (1, 'Иван Петров', '1990-05-15', '$2a$10$rmYFTIpMN7g/9y/0B2hSPumuuJguGZK6RuE/KRF4MiMahfIe.2l/.'), -- password123
    (2, 'Анна Сидорова', '1985-11-22', '$2a$10$bK7rZjRmJHQyc0LQY9PLKu6A8zQj4JhHw8zyWtqQNANep7NJH1cMK'); -- qwerty456

-- changeset author:folk
-- comment: Создание аккаунтов для пользователей
INSERT INTO account (id, user_id, balance, start_deposit)
VALUES
    (1, 1, 1000.00, 1000.00),
    (2, 2, 2000.00, 2000.00);

-- changeset author:folk
-- comment: Добавление email пользователей
INSERT INTO email_data (id, user_id, email)
VALUES
    (1, 1, 'ivan@example.com'),
    (2, 1, 'ivan.petrov@work.com'),
    (3, 2, 'anna@example.com');

-- changeset author:folk
-- comment: Добавление телефонов пользователей
INSERT INTO phone_data (id, user_id, phone)
VALUES
    (1, 1, '+79161234567'),
    (2, 2, '+79269876543'),
    (3, 2, '+79269876333');

-- changeset author:folk
-- comment: обновление последовательностей
SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));
SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('email_data_id_seq', (SELECT MAX(id) FROM email_data));
SELECT setval('phone_data_id_seq', (SELECT MAX(id) FROM phone_data));
INSERT INTO users (id, email, name, password, role)
VALUES (1, 'kim.dany@yandex.ru', 'DaniilKim', null, 'USER'),
       (2, 'dany47788@gmail.com', 'DaniilKocherov', null, 'ADMIN'),
       (3, 'trishina12@mail.ru', 'Anastasiia', null, 'MODERATOR');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO file (extension, name, user_id)
VALUES ('txt', 'document1', 1),
       ('png', 'document2', 2),
       ('jpeg', 'document3', 3);
SELECT SETVAL('file_id_seq', (SELECT MAX(id) FROM file));
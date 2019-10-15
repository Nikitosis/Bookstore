INSERT INTO bookstore.roles
    VALUES (NULL,'USER');

INSERT INTO bookstore.roles
    VALUES(NULL,'ADMIN');

INSERT INTO bookstore.users
    VALUES (NULL,'administrator','$2a$10$HgxHRvKGrYbbWLtpbHClzuLOHSKgwEPrvOSOv6.QNd7PaFSplhaaW','Admin','Admin',NULL,NULL,'MALE',NULL,NULL,NULL,0,0,NULL);

INSERT INTO bookstore.user_role
    VALUES (
        (SELECT id FROM bookstore.users WHERE username='administrator'),
        (SELECT id from bookstore.roles WHERE name='ADMIN')
    );
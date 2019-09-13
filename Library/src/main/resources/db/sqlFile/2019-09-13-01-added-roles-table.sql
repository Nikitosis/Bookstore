CREATE TABLE IF NOT EXISTS bookstore.roles(
    name VARCHAR(255) NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS bookstore.user_role(
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    CONSTRAINT FK_user_role_users FOREIGN KEY (user_id) REFERENCES bookstore.users (username),
    CONSTRAINT FK_user_role_roles FOREIGN KEY (role_id) REFERENCES bookstore.roles (name),
    UNIQUE (user_id, role_id)
);
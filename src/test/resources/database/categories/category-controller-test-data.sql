DELETE FROM users_roles;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM books_categories;
DELETE FROM books;
DELETE FROM categories;

ALTER TABLE categories AUTO_INCREMENT = 1;

INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES
(1, 'user@example.com', 'password', 'user', 'user', false);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES
(2, 'admin@example.com', 'password', 'admin', 'admin', false);

INSERT INTO roles (id, name) VALUES (1, 'USER');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 1);

INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'Fiction', 'A genre of imaginative, non-factual storytelling.', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 12.99,
        'A classic novel about the American Dream.', 'https://example.com/gatsby.jpg', false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);

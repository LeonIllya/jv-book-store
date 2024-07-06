INSERT INTO categories (id, name, description)
VALUES (1, 'Default Category 1', 'A default category for books 1'),
       (2, 'Default Category 2', 'A default category for books 2');

INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (1, 'Book Title 1', 'Author 1', 'ISBN-00001', 5.99, 'Description for Book 1', 'coverImage1.jpg'),
       (2, 'Book Title 2', 'Author 2', 'ISBN-00002', 6.99, 'Description for Book 2', 'coverImage2.jpg'),
       (3, 'Book Title 3', 'Author 3', 'ISBN-00003', 7.99, 'Description for Book 3', 'coverImage3.jpg'),
       (4, 'Book Title 4', 'Author 4', 'ISBN-00004', 8.99, 'Description for Book 4', 'coverImage4.jpg'),
       (5, 'Book Title 5', 'Author 5', 'ISBN-00005', 9.99, 'Description for Book 5', 'coverImage5.jpg');

INSERT INTO books_categories (book_id, categories_id)
SELECT id, 1
FROM books
WHERE isbn IN (
    'ISBN-00001', 'ISBN-00003', 'ISBN-00005'
);

INSERT INTO books_categories (book_id, categories_id)
SELECT id, 2
FROM books
WHERE isbn IN (
    'ISBN-00002', 'ISBN-00004'
);

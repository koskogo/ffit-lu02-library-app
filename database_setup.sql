-- Database setup for LibraryApp
-- Run this script to create the necessary tables

-- Create the books table
CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publication_year INTEGER NOT NULL
);

-- Create an index on ISBN for faster lookups
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);

-- Insert some sample data (optional)
INSERT INTO books (id, isbn, title, author, publication_year) VALUES
    (1, '978-3-8362-9544-4', 'Java ist auch eine Insel', 'Christian Ullenboom', 2023),
    (2, '978-3-658-43573-8', 'Grundkurs Java', 'Dietmar Abts', 2024)
ON CONFLICT (id) DO NOTHING;

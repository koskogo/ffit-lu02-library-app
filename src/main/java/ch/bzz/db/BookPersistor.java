package ch.bzz.db;

import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookPersistor {
    private static final Logger log = LoggerFactory.getLogger(BookPersistor.class);

    public static List<Book> loadBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        log.debug("Loading books from database");
        
        try (Connection connection = Database.getConnection()) {
            String sql = "SELECT id, isbn, title, author, publication_year FROM books";
            log.debug("Executing SQL: {}", sql);
            
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String isbn = resultSet.getString("isbn");
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    int year = resultSet.getInt("publication_year");
                    
                    Book book = new Book(id, isbn, title, author, year);
                    books.add(book);
                }
            }
            log.info("Successfully loaded {} books from database", books.size());
        } catch (SQLException e) {
            log.error("Database error while loading books", e);
            return new ArrayList<>();
        }
        
        return books;
    }

    public static void saveBooksToDatabase(List<Book> books) {
        if (books.isEmpty()) {
            log.warn("No books to save");
            return;
        }
        
        log.debug("Saving {} books to database", books.size());
        try (Connection connection = Database.getConnection()) {
            String sql = "INSERT INTO books (id, isbn, title, author, publication_year) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "isbn = EXCLUDED.isbn, " +
                        "title = EXCLUDED.title, " +
                        "author = EXCLUDED.author, " +
                        "publication_year = EXCLUDED.publication_year";
            log.debug("Executing SQL: {}", sql);
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Book book : books) {
                    statement.setInt(1, book.getId());
                    statement.setString(2, book.getIsbn());
                    statement.setString(3, book.getTitle());
                    statement.setString(4, book.getAuthor());
                    statement.setInt(5, book.getYear());
                    statement.executeUpdate();
                }
            }
            
            log.info("Successfully saved {} books to database", books.size());
        } catch (SQLException e) {
            log.error("Database error during save", e);
        }
    }
}


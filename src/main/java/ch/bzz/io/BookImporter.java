package ch.bzz.io;

import ch.bzz.db.BookPersistor;
import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class BookImporter {
    private static final Logger log = LoggerFactory.getLogger(BookImporter.class);
    
    public static void importBooksFromFile(String filePath) {
        log.info("Starting import from file: {}", filePath);
        
        try {
            List<Book> books = DelimitedFileReader.readBooksFromFile(filePath);
            
            if (books.isEmpty()) {
                log.warn("No books found in file or file is empty: {}", filePath);
                return;
            }
            
            BookPersistor.saveBooksToDatabase(books);
            log.info("Successfully imported {} books from {}", books.size(), filePath);
        } catch (Exception e) {
            log.error("Error importing books from file: {}", filePath, e);
        }
    }
}


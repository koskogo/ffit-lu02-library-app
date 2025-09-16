package ch.bzz.io;

import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DelimitedFileReader {
    private static final Logger log = LoggerFactory.getLogger(DelimitedFileReader.class);
    
    public static List<Book> readBooksFromFile(String filePath) {
        List<Book> books = new ArrayList<>();
        log.debug("Reading books from file: {}", filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false;
                    log.debug("Skipping header line: {}", line);
                    continue; // Skip header line
                }
                
                String[] parts = line.split("\t");
                if (parts.length >= 5) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String isbn = parts[1].trim();
                        String title = parts[2].trim();
                        String author = parts[3].trim();
                        int year = Integer.parseInt(parts[4].trim());
                        
                        Book book = new Book(id, isbn, title, author, year);
                        books.add(book);
                        log.debug("Parsed book: {} by {} ({})", title, author, year);
                    } catch (NumberFormatException e) {
                        log.warn("Error parsing line {}: {} - {}", lineNumber, line, e.getMessage());
                    }
                } else {
                    log.warn("Invalid line format at line {}: {} (expected 5 tab-separated values)", lineNumber, line);
                }
            }
            log.info("Successfully read {} books from file: {}", books.size(), filePath);
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", filePath, e);
            return new ArrayList<>();
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            return new ArrayList<>();
        }
        
        return books;
    }
}


package ch.bzz;

import ch.bzz.db.BookPersistor;
import ch.bzz.io.BookImporter;
import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class LibraryAppMain {

    private static final Logger log = LoggerFactory.getLogger(LibraryAppMain.class);
    private static final Book BOOK_1 = new Book(1, "978-3-8362-9544-4", "Java ist auch eine Insel", "Christian Ullenboom", 2023);
    private static final Book BOOK_2 = new Book(2, "978-3-658-43573-8", "Grundkurs Java", "Dietmar Abts", 2024);
    private static final Book[] BOOKS = {BOOK_1, BOOK_2};

    public static void main(String[] args) {
        log.info("Starting LibraryApp");
        
        if (args.length > 0) {
            // Join all arguments to form the complete command
            String command = String.join(" ", args).toLowerCase();
            log.debug("Executing command from arguments: {}", command);
            if (command.startsWith("importbooks ") && args.length > 1) {
                String filePath = args[1];
                BookImporter.importBooksFromFile(filePath);
                return;
            } else {
                executeCommand(command);
                return;
            }
        }
        
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            log.info("Welcome to LibraryApp! Type 'help' for commands.");
            while (true) {
                System.out.print("> ");
                if (scanner.hasNextLine()) {
                    input = scanner.nextLine().trim().toLowerCase();
                    if ("quit".equals(input)) {
                        log.info("Exiting");
                        break;
                    } else {
                        executeCommand(input);
                    }
                } else {
                    log.warn("No input available, exiting.");
                    break;
                }
            }
            scanner.close();
        } catch (Exception e) {
            log.error("Error in interactive mode", e);
            log.info("Running in fallback mode - showing books from database:");
            executeCommand("listbooks");
        }
    }
    
    private static void executeCommand(String command) {
        log.debug("Executing command: {}", command);
        
        if (command.equals("help")) {
            log.info("commands:");
            log.info("- help");
            log.info("- quit");
            log.info("- listbooks [LIMIT]");
            log.info("- importbooks <FILE_PATH>");
        } else if (command.equals("listbooks") || command.startsWith("listbooks ")) {
            int limit = -1;
            if (command.startsWith("listbooks ")) {
                String limitStr = command.substring("listbooks ".length()).trim();
                try {
                    limit = Integer.parseInt(limitStr);
                    log.debug("Listing books with limit: {}", limit);
                } catch (NumberFormatException e) {
                    log.warn("Invalid limit parameter '{}' for listbooks command, ignoring limit", limitStr);
                }
            }
            
            log.info("Books:");
            List<Book> books = BookPersistor.loadBooksFromDatabase();
            if (books.isEmpty()) {
                log.warn("No books found in database or database connection failed.");
                for (Book book : BOOKS) {
                    log.info("- {} by {} ({})", book.getTitle(), book.getAuthor(), book.getYear());
                }
            } else {
                int count = 0;
                for (Book book : books) {
                    if (limit > 0 && count >= limit) {
                        break;
                    }
                    log.info("- {} by {} ({})", book.getTitle(), book.getAuthor(), book.getYear());
                    count++;
                }
                if (limit > 0 && books.size() > limit) {
                    log.info("... and {} more books (showing first {})", books.size() - limit, limit);
                }
            }
        } else if (command.startsWith("importbooks ")) {
            String filePath = command.substring("importbooks ".length()).trim();
            if (filePath.isEmpty()) {
                log.warn("Usage: importbooks <FILE_PATH>");
            } else {
                BookImporter.importBooksFromFile(filePath);
            }
        } else {
            log.warn("Unknown command: '{}'. Type 'help' for a list of commands.", command);
        }
    }
}

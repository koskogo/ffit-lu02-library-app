package ch.bzz;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class LibraryAppMain {

    public static class Book {
        private final int id;
        private final String isbn;
        private final String title;
        private final String author;
        private final int year;

        public Book(int id, String isbn, String title, String author, int year) {
            this.id = id;
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public int getId() { return id; }
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getYear() { return year; }
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
            System.err.println("Please create config.properties from config.properties.template");
            return null;
        }
        return properties;
    }

    public static List<Book> loadBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        Properties config = loadConfiguration();
        if (config == null) {
            return books;
        }
        
        String url = config.getProperty("DB_URL");
        String user = config.getProperty("DB_USER");
        String password = config.getProperty("DB_PASSWORD");
        
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT id, isbn, title, author, publication_year FROM books";
            
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
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return new ArrayList<>();
        }
        
        return books;
    }

    public static void importBooksFromFile(String filePath) {
        Properties config = loadConfiguration();
        if (config == null) {
            System.err.println("Cannot import books: configuration not available");
            return;
        }
        
        String url = config.getProperty("DB_URL");
        String user = config.getProperty("DB_USER");
        String password = config.getProperty("DB_PASSWORD");
        
        List<Book> books = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
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
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }
        
        if (books.isEmpty()) {
            System.err.println("No books found in file or file is empty");
            return;
        }
        
        // Save books to database
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO books (id, isbn, title, author, publication_year) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "isbn = EXCLUDED.isbn, " +
                        "title = EXCLUDED.title, " +
                        "author = EXCLUDED.author, " +
                        "publication_year = EXCLUDED.publication_year";
            
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
            
            System.out.println("Successfully imported " + books.size() + " books from " + filePath);
        } catch (SQLException e) {
            System.err.println("Database error during import: " + e.getMessage());
        }
    }

    private static final Book BOOK_1 = new Book(1, "978-3-8362-9544-4", "Java ist auch eine Insel", "Christian Ullenboom", 2023);
    private static final Book BOOK_2 = new Book(2, "978-3-658-43573-8", "Grundkurs Java", "Dietmar Abts", 2024);
    private static final Book[] BOOKS = {BOOK_1, BOOK_2};


    public static void main(String[] args) {
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("importbooks") && args.length > 1) {
                String filePath = args[1];
                importBooksFromFile(filePath);
                return;
            } else {
                executeCommand(command);
                return;
            }
        }
        
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            System.out.println("Welcome to LibraryApp! Type 'help' for commands.");
            while (true) {
                System.out.print("> ");
                if (scanner.hasNextLine()) {
                    input = scanner.nextLine().trim().toLowerCase();
                    if ("quit".equals(input)) {
                        System.out.println("Exiting");
                        break;
                    } else {
                        executeCommand(input);
                    }
                } else {
                    System.out.println("No input available, exiting.");
                    break;
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error in interactive mode: " + e.getMessage());
            System.out.println("Running in fallback mode - showing books from database:");
            executeCommand("listbooks");
        }
    }
    
    private static void executeCommand(String command) {
        if (command.equals("help")) {
            System.out.println("commands:");
            System.out.println("- help");
            System.out.println("- quit");
            System.out.println("- listbooks");
            System.out.println("- importbooks <FILE_PATH>");
        } else if (command.equals("listbooks")) {
            System.out.println("Books:");
            List<Book> books = loadBooksFromDatabase();
            if (books.isEmpty()) {
                System.out.println("No books found in database or database connection failed.");
                for (Book book : BOOKS) {
                    System.out.println("- " + book.getTitle() + " by " + book.getAuthor() + " (" + book.getYear() + ")");
                }
            } else {
                for (Book book : books) {
                    System.out.println("- " + book.getTitle() + " by " + book.getAuthor() + " (" + book.getYear() + ")");
                }
            }
        } else if (command.startsWith("importbooks ")) {
            String filePath = command.substring("importbooks ".length()).trim();
            if (filePath.isEmpty()) {
                System.out.println("Usage: importbooks <FILE_PATH>");
            } else {
                importBooksFromFile(filePath);
            }
        } else {
            System.out.println("Unknown command: '" + command + "'. Type 'help' for a list of commands.");
        }
    }
}

package ch.bzz;

import ch.bzz.Book;


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

        private static final Book BOOK_1 = new Book(1, "978-3-8362-9544-4", "Java ist auch eine Insel", "Christian Ullenboom", 2023);
        private static final Book BOOK_2 = new Book(2, "978-3-658-43573-8", "Grundkurs Java", "Dietmar Abts", 2024);
        private static final Book[] BOOKS = {BOOK_1, BOOK_2};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to LibraryApp! Type 'help' for commands.");
        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("quit")) {
                System.out.println("Exiting");
                break;
            } else if (input.equals("help")) {
                System.out.println("commands:");
                System.out.println("- help");
                System.out.println("- quit");
            } else if (input.equals("listbooks")) {
                for (Book book : BOOKS) {
                    System.out.println(book.getTitle());
                }
            } else {
                System.out.println("Unknown command: '" + input + "'. Type 'help' for a list of commands.");
            }
        }
        scanner.close();
    }
}

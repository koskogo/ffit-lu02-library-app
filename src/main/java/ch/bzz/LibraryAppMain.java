package ch.bzz;


import java.util.Scanner;

public class LibraryAppMain {

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
            } else {
                System.out.println("Unknown command: '" + input + "'. Type 'help' for a list of commands.");
            }
        }
        scanner.close();
    }
}

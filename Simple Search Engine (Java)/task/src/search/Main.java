package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static List<String> people = new ArrayList<>();
    private static Map<String, List<Integer>> invertedIndex = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Check for command line arguments for file input
        if (args.length < 2 || !args[0].equals("--data")) {
            System.out.println("Usage: --data filename");
            return;
        }
        initializeData(args[1]); // Read data from file and populate the inverted index
        showMenu(); // Show menu for user options
    }

    // Method to read data from a file and initialize the inverted index
    private static void initializeData(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            int lineIndex = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    people.add(line);
                    for (String word : line.toLowerCase().split("\\s+")) {
                        invertedIndex.putIfAbsent(word, new ArrayList<>());
                        invertedIndex.get(word).add(lineIndex);
                    }
                    lineIndex++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    // Method to show the menu and handle user input
    private static void showMenu() {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            System.out.print("> ");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1: findPerson(); break;
                case 2: printAllPeople(); break;
                case 0: System.out.println("Bye!"); return;
                default: System.out.println("Incorrect option! Try again.");
            }
        }
    }

    // Method to find a person based on the selected strategy
    private static void findPerson() {
        System.out.print("Select a matching strategy: ALL, ANY, NONE\n> ");
        String strategyInput = scanner.nextLine().toUpperCase();
        System.out.print("Enter a name or email to search all suitable people.\n> ");
        String query = scanner.nextLine().toLowerCase();

        Set<Integer> results = new HashSet<>(); // Use a Set to avoid duplicates
        String[] words = query.split("\\s+");

        // Execute the appropriate search strategy
        if ("ALL".equals(strategyInput)) {
            results.addAll(invertedIndex.getOrDefault(words[0], new ArrayList<>()));
            for (String word : words) {
                results.retainAll(invertedIndex.getOrDefault(word, new ArrayList<>()));
            }
        } else if ("ANY".equals(strategyInput)) {
            for (String word : words) {
                results.addAll(invertedIndex.getOrDefault(word, new ArrayList<>()));
            }
        } else if ("NONE".equals(strategyInput)) {
            Set<Integer> excluded = new HashSet<>();
            for (String word : words) {
                excluded.addAll(invertedIndex.getOrDefault(word, new ArrayList<>()));
            }
            for (int i = 0; i < people.size(); i++) {
                if (!excluded.contains(i)) results.add(i);
            }
        } else {
            System.out.println("Invalid strategy! Please choose ALL, ANY, or NONE.");
            return;
        }

        // Print results
        System.out.println(results.size() + " person(s) found:");
        for (Integer index : results) {
            System.out.println(people.get(index));
        }
    }

    // Method to print all stored people
    private static void printAllPeople() {
        System.out.println("\n=== List of people ===");
        for (String person : people) {
            System.out.println(person);
        }
    }
}

package library;
import library.catalog.BookCatalog;
import library.catalog.BookCsvLoader;
import library.catalog.SearchCriteria;
import library.model.Book;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
public  final class Main {
    private static final String DEFAULT_DATA_FILE = "data/books.csv";
    private static final int MAX_PRINTED = 20;

    private Main() {
    }

    public static void main(String[] args) {
        // Вывод и ввод всегда в UTF-8, чтобы кириллица одинаково работала в разных консолях.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true,
                StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true,
                StandardCharsets.UTF_8));

        Path dataFile = Path.of(args.length > 0 ? args[0] : DEFAULT_DATA_FILE);
        BookCatalog catalog = new BookCatalog();
        try {
            catalog.addAll(BookCsvLoader.load(dataFile));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Не удалось загрузить каталог из " + dataFile + ": " + e.getMessage());
            System.exit(1);
            return;
        }
        System.out.println("Каталог загружен, книг: " + catalog.size());

        try (Scanner in = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                printMenu();
                if (!in.hasNextLine()) {
                    return;
                }
                String choice = in.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> printBooks(catalog.searchByTitle(
                                ask(in, "Название или его часть: ")));
                        case "2" -> printBooks(catalog.searchByAuthor(
                                ask(in, "Автор или его часть: ")));
                        case "3" -> printBooks(catalog.searchByGenre(
                                ask(in, "Жанр (полностью): ")));
                        case "4" -> combinedSearch(in, catalog);
                        case "5" -> System.out.println("Жанры: " + String.join(", ", catalog.getGenres()));
                        case "0" -> {
                            return;
                        }
                        default -> System.out.println("Неизвестная команда: " + choice);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1 - поиск по названию");
        System.out.println("2 - поиск по автору");
        System.out.println("3 - поиск по жанру");
        System.out.println("4 - комбинированный поиск (название, автор, жанр, годы)");
        System.out.println("5 - список жанров");
        System.out.println("0 - выход");
        System.out.print("> ");
    }

    private static void combinedSearch(Scanner in, BookCatalog catalog) {
        SearchCriteria criteria = SearchCriteria.builder()
                .title(ask(in, "Название (Enter - пропустить): "))
                .author(ask(in, "Автор (Enter - пропустить): "))
                .genre(ask(in, "Жанр (Enter - пропустить): "))
                .yearFrom(askYear(in, "Год издания от (Enter - пропустить): "))
                .yearTo(askYear(in, "Год издания до (Enter - пропустить): "))
                .build();
        if (criteria.isEmpty()) {
            System.out.println("Не задано ни одного условия поиска.");
        } else {
            printBooks(catalog.search(criteria));
        }
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine() : "";
    }

    private static Integer askYear(Scanner in, String prompt) {
        String text = ask(in, prompt).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Год должен быть целым числом: " + text);
        }
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        System.out.println("Найдено: " + books.size());
        for (int i = 0; i < Math.min(books.size(), MAX_PRINTED); i++) {
            System.out.println("  " + books.get(i));
        }
        if (books.size() > MAX_PRINTED) {
            System.out.println("  ... и ещё " + (books.size() - MAX_PRINTED));
        }
    }
}

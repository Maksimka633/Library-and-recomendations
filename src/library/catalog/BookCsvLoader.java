package library.catalog;
import library.model.Book;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Загрузка книг из CSV-файла в кодировке UTF-8.
 * <p>
 * Формат: разделитель «;», первая строка — заголовок, далее по одной книге
 * в строке: {@code id;название;автор;жанр;год}. Кавычки не поддерживаются,
 * поэтому в полях не должно быть символа «;».
 */
public class BookCsvLoader {
    private static final String SEPARATOR = ";";
    private static final int FIELDS_COUNT = 5;

    private BookCsvLoader() {
    }

    /**
     * @throws IOException              если файл не удалось прочитать
     * @throws IllegalArgumentException если какая-то строка имеет неверный формат
     *                                  (в сообщении указан номер строки)
     */
    public static List<Book> load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Book> books = new ArrayList<>();
        // Строка с индексом 0 — заголовок, пропускаем её.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                books.add(parseLine(line, i + 1));
            }
        }
        return books;
    }

    static Book parseLine(String line, int lineNumber) {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != FIELDS_COUNT) {
            throw new IllegalArgumentException("Строка " + lineNumber + ": ожидается "
                    + FIELDS_COUNT + " полей, найдено " + parts.length);
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            int year = Integer.parseInt(parts[4].trim());
            return new Book(id, parts[1], parts[2], parts[3], year);
        } catch (IllegalArgumentException e) {
            // NumberFormatException тоже наследуется от IllegalArgumentException
            throw new IllegalArgumentException("Строка " + lineNumber + ": " + e.getMessage(), e);
        }
    }
}


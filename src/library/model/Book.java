package library.model;
import java.time.Year;
import java.util.Objects;

/**
 * Книга в каталоге библиотеки.
 * <p>
 * Объект неизменяемый. Две книги считаются одинаковыми, если совпадает id.
 */
public final class Book {

    private final int id;
    private final String title;
    private final String author;
    private final String genre;
    private final int year;

    /**
     * @throws IllegalArgumentException если id не положительный, год вне диапазона
     *                                  или текстовое поле пустое
     */
    public Book(int id, String title, String author, String genre, int year) {
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом: " + id);
        }
        if (year < 1 || year > Year.now().getValue()) {
            throw new IllegalArgumentException("Некорректный год издания: " + year);
        }
        this.id = id;
        this.title = requireText(title, "Название");
        this.author = requireText(author, "Автор");
        this.genre = requireText(genre, "Жанр");
        this.year = year;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        return value.trim();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id == ((Book) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d «%s» — %s [%s, %d]", id, title, author, genre, year);
    }
}

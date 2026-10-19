package library.catalog;
import library.model.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Каталог книг: хранение, поиск по id и поиск по нескольким полям.
 * <p>
 * Внутри два индекса: по id и по жанру (жанр в нижнем регистре).
 * Порядок книг в результатах совпадает с порядком добавления.
 */
public class BookCatalog {
    private final Map<Integer, Book> booksById = new LinkedHashMap<>();
    private final Map<String, List<Book>> booksByGenre = new HashMap<>();

    /**
     * @throws IllegalArgumentException если книга с таким id уже есть
     * @throws NullPointerException     если book равен null
     */
    public void add(Book book) {
        Objects.requireNonNull(book, "book");
        if (booksById.containsKey(book.getId())) {
            throw new IllegalArgumentException(
                    "Книга с id " + book.getId() + " уже есть в каталоге");
        }
        booksById.put(book.getId(), book);
        booksByGenre.computeIfAbsent(normalize(book.getGenre()), key -> new ArrayList<>())
                .add(book);
    }

    public void addAll(Collection<Book> books) {
        for (Book book : books) {
            add(book);
        }
    }

    public Optional<Book> findById(int id) {
        return Optional.ofNullable(booksById.get(id));
    }

    /** @return неизменяемая копия списка всех книг */
    public List<Book> getAll() {
        return List.copyOf(booksById.values());
    }

    public int size() {
        return booksById.size();
    }

    /** @return жанры, имеющиеся в каталоге, по алфавиту без учёта регистра */
    public Set<String> getGenres() {
        Set<String> genres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Book book : booksById.values()) {
            genres.add(book.getGenre());
        }
        return genres;
    }

    /** Поиск по части названия. Пустой запрос возвращает все книги. */
    public List<Book> searchByTitle(String text) {
        return search(SearchCriteria.builder().title(text).build());
    }

    /** Поиск по части имени автора. Пустой запрос возвращает все книги. */
    public List<Book> searchByAuthor(String text) {
        return search(SearchCriteria.builder().author(text).build());
    }

    /** Поиск по жанру (точное совпадение). Пустой запрос возвращает все книги. */
    public List<Book> searchByGenre(String genre) {
        return search(SearchCriteria.builder().genre(genre).build());
    }

    /**
     * Поиск по нескольким полям сразу. Книга попадает в результат,
     * только если удовлетворяет всем заданным условиям.
     * Если условий нет, возвращаются все книги.
     *
     * @return неизменяемый список найденных книг (пустой, если ничего не найдено)
     */
    public List<Book> search(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria");

        // Если задан жанр, берём кандидатов из индекса, а не перебираем весь каталог.
        Collection<Book> candidates = criteria.getGenre() == null
                ? booksById.values()
                : booksByGenre.getOrDefault(normalize(criteria.getGenre()), List.of());

        List<Book> result = new ArrayList<>();
        for (Book book : candidates) {
            if (matches(book, criteria)) {
                result.add(book);
            }
        }
        return List.copyOf(result);
    }

    private static boolean matches(Book book, SearchCriteria criteria) {
        if (criteria.getTitle() != null && !contains(book.getTitle(), criteria.getTitle())) {
            return false;
        }
        if (criteria.getAuthor() != null && !contains(book.getAuthor(), criteria.getAuthor())) {
            return false;
        }
        if (criteria.getYearFrom() != null && book.getYear() < criteria.getYearFrom()) {
            return false;
        }
        if (criteria.getYearTo() != null && book.getYear() > criteria.getYearTo()) {
            return false;
        }
        return true;
    }

    private static boolean contains(String text, String part) {
        return normalize(text).contains(normalize(part));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}


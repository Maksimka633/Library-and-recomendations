package library.catalog;

public class SearchCriteria {

    private final String title;
    private final String author;
    private final String genre;
    private final Integer yearFrom;
    private final Integer yearTo;

    private SearchCriteria(Builder builder) {
        this.title = builder.title;
        this.author = builder.author;
        this.genre = builder.genre;
        this.yearFrom = builder.yearFrom;
        this.yearTo = builder.yearTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** @return часть названия или {@code null}, если условие не задано */
    public String getTitle() {
        return title;
    }

    /** @return часть имени автора или {@code null}, если условие не задано */
    public String getAuthor() {
        return author;
    }

    /** @return жанр или {@code null}, если условие не задано */
    public String getGenre() {
        return genre;
    }

    /** @return нижняя граница года или {@code null} */
    public Integer getYearFrom() {
        return yearFrom;
    }

    /** @return верхняя граница года или {@code null} */
    public Integer getYearTo() {
        return yearTo;
    }

    /** @return {@code true}, если не задано ни одного условия */
    public boolean isEmpty() {
        return title == null && author == null && genre == null
                && yearFrom == null && yearTo == null;
    }

    public static final class Builder {
        private String title;
        private String author;
        private String genre;
        private Integer yearFrom;
        private Integer yearTo;

        private Builder() {
        }

        public Builder title(String value) {
            this.title = clean(value);
            return this;
        }

        public Builder author(String value) {
            this.author = clean(value);
            return this;
        }

        public Builder genre(String value) {
            this.genre = clean(value);
            return this;
        }

        public Builder yearFrom(Integer value) {
            this.yearFrom = value;
            return this;
        }

        public Builder yearTo(Integer value) {
            this.yearTo = value;
            return this;
        }

        /**
         * @throws IllegalArgumentException если год «от» больше года «до»
         */
        public SearchCriteria build() {
            if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
                throw new IllegalArgumentException(
                        "Год «от» (" + yearFrom + ") больше года «до» (" + yearTo + ")");
            }
            return new SearchCriteria(this);
        }

        private static String clean(String value) {
            return (value == null || value.isBlank()) ? null : value.trim();
        }
    }
}


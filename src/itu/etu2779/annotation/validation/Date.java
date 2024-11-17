package itu.etu2779.annotation.validation;

public @interface Date {
    public String pattern() default "yyyy-MM-dd";
}

package edu.school21.annotations;

public @interface OrmColumn {
    String name();

    int length() default 100;
}

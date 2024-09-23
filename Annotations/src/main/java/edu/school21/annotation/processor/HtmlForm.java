package edu.school21.annotation.processor;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface HtmlForm {
    String fileName();
    String action();
    String method();
}

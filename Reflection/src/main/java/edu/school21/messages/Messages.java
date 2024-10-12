package edu.school21.messages;

public enum Messages {
    ERROR("error when you try: %s"),
    ENTER_METHOD_NAME("Enter name of the method for call:\n"),
    ENTER_VALUE("Enter %s value:\n"),
    DELIMITER("---------------------\n"),
    METHOD_RETURNED("Method returned:\n %s"),
    ENTER_CLASS_NAME("Enter class name:\n"),
    ENTER_FIELD_NAME("Enter name of the field for changing:\n"),
    OBJECT_UPDATED("Object update: %s\n" + DELIMITER),
    OBJECT_CREATED("Object update: %s\n" + DELIMITER),
    CREATE_OBJECT("Let’s create an object.\n"),
    CLASSES("Classes:\n"),
    FIELDS("fields:\n"),
    LINE("\t %s %s\n"),
    METHODS("methods:\n");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public void print(String ... args) {
        System.out.format(message, args);;
    }

    public String getMessage() {
        return message;
    }
}
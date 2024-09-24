package edu.school21.application;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;


public class Application {

    static final String CLASS_PATH = "edu.school21.classes";
    static final String DELIMITER = "-----------------\n";

    private Object object;
    private final Scanner scanner = new Scanner(System.in);
    private Field[] fields;
    private Method[] methods;

    void start() {
        showClasses();
        try {
            initObject(scanner.next());
            showData();
            setFields();
            fieldChanging();
            executeMethod();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            System.out.println("error when you try:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeMethod() throws InvocationTargetException, IllegalAccessException {
        System.out.println("Enter name of the method for call:");
        Method method = findMethod(scanner.next());
        method.setAccessible(true);
        Object[] params = new Object[method.getParameterCount()];
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            System.out.println("Enter " + paramTypes[i].getSimpleName() + " value:");
            params[i] = fieldValue(paramTypes[i]);
        }
        System.out.println("Method returned:\n" + method.invoke(object, params).toString());
    }

    private Method findMethod(final String desiredMethod) {
        for (Method method : methods) {
            String[] fullNameMethod = method.toString().split("\\.");
            if (fullNameMethod[fullNameMethod.length - 1].equals(desiredMethod)) {
                return method;
            }
        }
        throw new IllegalArgumentException("method not found");
    }

    private void fieldChanging() throws IllegalAccessException {
        System.out.println("Enter name of the field for changing:");
        Field requiredField = null;
        String fieldName = scanner.next();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                requiredField = field;
            }
        }
        if (requiredField == null) {
            throw new IllegalArgumentException("field not found");
        }
        System.out.println("Enter " + requiredField.getType().getSimpleName() + " value:");
        requiredField.set(object, fieldValue(requiredField.getType()));
        System.out.println("Object update: " + object.toString() + "\n" + DELIMITER);
    }

    private void setFields() throws IllegalAccessException, InputMismatchException {
        System.out.println("Let’s create an object.");
        for (Field field : fields) {
            System.out.println(field.getName());
            field.setAccessible(true);
            field.set(object, fieldValue(field.getType()));
        }
        System.out.println("Object created: " + object.toString() + "\n" + DELIMITER);
    }

    private Object fieldValue(final Class<?> type) throws IllegalArgumentException {
        Object result;
        try {
            if (type.equals(String.class)) {
                result = scanner.next();
            } else if (type.equals(int.class)) {
                result = scanner.nextInt();
            } else if (type.equals(double.class)) {
                result = scanner.nextDouble();
            } else if (type.equals(long.class)) {
                result = scanner.nextLong();
            } else if (type.equals(boolean.class)) {
                result = scanner.nextBoolean();
            } else {
                throw new IllegalArgumentException("sorry that type is not supported");
            }
        } catch (InputMismatchException e) {
            throw new IllegalArgumentException("wrong type");
        }
        return result;
    }

    private void initObject(final String next) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName(CLASS_PATH + "." + next);
        object = clazz.getDeclaredConstructor().newInstance();
        fields = clazz.getDeclaredFields();
        methods = clazz.getDeclaredMethods();
    }

    void showClasses() {
        System.out.println("Classes:");
        Reflections reflections = new Reflections(CLASS_PATH, new SubTypesScanner(false));
        reflections.getSubTypesOf(Object.class).forEach(aClass -> {
            System.out.println(aClass.getSimpleName());
        });
        System.out.println(DELIMITER + "Enter class name:");
    }

    void showData() throws IllegalAccessException {
        System.out.println("fields:");
        for (Field field : fields) {
            System.out.println("\t" + field.getType().getSimpleName() + " " + field.getName());
        }
        System.out.println("methods:");
        for (Method method : methods) {
            String[] fullNameMethod = method.toString().split("\\.");
            System.out.println("\t" + method.getReturnType().getSimpleName() + " " + fullNameMethod[fullNameMethod.length - 1]);
        }
        System.out.println(DELIMITER);
    }

}

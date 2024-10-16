package edu.school21.application;

import edu.school21.messages.Messages;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Application {

    private static final String CLASS_PATH = "edu.school21.classes";

    private final Scanner scanner = new Scanner(System.in);
    private Object object;
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
            Messages.ERROR.print(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showClasses() {
        Messages.CLASSES.print();
        Reflections reflections = new Reflections(CLASS_PATH,
                new SubTypesScanner(false));
        reflections.getSubTypesOf(Object.class).forEach(aClass -> {
            System.out.println(aClass.getSimpleName());
        });
        Messages.DELIMITER.print();
        Messages.ENTER_CLASS_NAME.print();
    }

    private void initObject(final String text) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName(CLASS_PATH + "." + text);
        object = clazz.getDeclaredConstructor().newInstance();
        fields = clazz.getDeclaredFields();
        methods = clazz.getDeclaredMethods();
    }

    void showData() throws IllegalAccessException {
        Messages.FIELDS.print();
        for (Field field : fields) {
            Messages.LINE.print(field.getType().getSimpleName(), field.getName());
        }
        Messages.METHODS.print();
        for (Method method : methods) {
            String[] fullNameMethod = method.toString().split("\\.");
            Messages.LINE.print(method.getReturnType().getSimpleName(),
                    fullNameMethod[fullNameMethod.length - 1]);
        }
        Messages.DELIMITER.print();
    }

    private void setFields() throws IllegalAccessException, InputMismatchException {
        Messages.CREATE_OBJECT.print();
        for (Field field : fields) {
            System.out.println(field.getName());
            field.setAccessible(true);
            field.set(object, fieldValue(field.getType()));
        }
        Messages.OBJECT_CREATED.print(object.toString());
    }

    private void fieldChanging() throws IllegalAccessException {
        Messages.ENTER_FIELD_NAME.print();
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
        Messages.ENTER_VALUE.print(requiredField.getType().getSimpleName());
        requiredField.set(object, fieldValue(requiredField.getType()));
        Messages.OBJECT_UPDATED.print(object.toString());
    }

    private void executeMethod() throws InvocationTargetException, IllegalAccessException {
        Messages.ENTER_METHOD_NAME.print();
        Method method = findMethod(scanner.next());
        method.setAccessible(true);
        Object[] params = new Object[method.getParameterCount()];
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Messages.ENTER_VALUE.print(paramTypes[i].getSimpleName());
            params[i] = fieldValue(paramTypes[i]);
        }
        Messages.METHOD_RETURNED.print(method.invoke(object, params).toString());
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

}

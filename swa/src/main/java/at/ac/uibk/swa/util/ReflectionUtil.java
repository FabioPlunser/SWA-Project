package at.ac.uibk.swa.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class ReflectionUtil {
    public static boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    @SneakyThrows
    public static Object getStaticFieldValue(Field field) {
        return field.get(null);
    }

    @SneakyThrows
    public static<T> T getStaticFieldValueTyped(Field field) {
        return (T) field.get(null);
    }

    public static Predicate<Field> isAssignableFrom(Class clazz) {
        return field -> field.getType().isAssignableFrom(clazz);
    }
}

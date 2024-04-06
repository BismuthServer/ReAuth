package technicianlp.reauth.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {
    private static Field findFieldInternal(Class<?> clz, String name) throws NoSuchFieldException {
        Field field = clz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static Field findField(Class<?> clz, String name) {
        try {
            return findFieldInternal(clz, name);
        } catch (ReflectiveOperationException exception) {
            throw new UncheckedReflectiveOperationException("Unable to find Field: " + name, exception);
        }
    }

    public static void unlockFinalField(Field field) {
        try {
            Field fieldModifiers = findField(Field.class, "modifiers");
            fieldModifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (ReflectiveOperationException exception) {
            throw new UncheckedReflectiveOperationException("Unable to unlock final field", exception);
        }
    }

    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new UncheckedReflectiveOperationException("Failed Reflective set", exception);
        }
    }

    public static <T> T getField(Field field, Object target) {
        try {
            //noinspection unchecked
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new UncheckedReflectiveOperationException("Failed Reflective get", exception);
        }
    }

    public static class UncheckedReflectiveOperationException extends RuntimeException {

        public UncheckedReflectiveOperationException(String message, ReflectiveOperationException cause) {
            super(message, cause);
        }
    }
}

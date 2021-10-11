package org.cga.sctp.mis.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ReflectionUtils {
    public static boolean isArray(Object object) {
        return object.getClass().isArray();
    }

    public static <T> T getFieldValue(Object instance, String fieldName) {
        Class<?> klazz = instance.getClass();
        Field field = null;
        while (klazz != null) {
            try {
                field = klazz.getField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                try {
                    field = klazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e2) {
                    klazz = klazz.getSuperclass();
                }
            }
        }
        try {
            field.setAccessible(true);
            // Suppressed unchecked
            return (T) field.get(instance);
        } catch (IllegalAccessException ignore) {
        }
        return null;
    }

    public static class ArrayIterable implements Iterable<Object> {
        private final Object arrayInstance;

        public ArrayIterable(Object arrayInstance) {
            this.arrayInstance = arrayInstance;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ArrayIterator(arrayInstance);
        }

        @Override
        public Spliterator<Object> spliterator() {
            throw new UnsupportedOperationException();
        }
    }

    public static class SymbolResolver {
        private final Object item;
        private final String symbolIdentifier;

        public SymbolResolver(Object item, String symbolIdentifier) {
            this.item = item;
            this.symbolIdentifier = symbolIdentifier;
        }

        public Object getValue() {
            try {
                if (symbolIdentifier.endsWith("()")) {
                    Method method = getMethod(symbolIdentifier.replaceAll("[()]", ""), item.getClass());
                    method.trySetAccessible();
                    return method.invoke(item);
                } else {
                    Field field = getField(symbolIdentifier, item.getClass());
                    field.trySetAccessible();
                    return field.get(item);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error obtaining value from class " + item.getClass(), e);
            }
        }

        private Method getMethod(String name, Class<?> clazz) {
            try {
                return clazz.getMethod(name);
            } catch (NoSuchMethodException nsme) {
                try {
                    return clazz.getDeclaredMethod(name);
                } catch (NoSuchMethodException nsme2) {
                    throw new RuntimeException("Error resolving method " + name);
                }
            }
        }

        private Field getField(String name, Class<?> clazz) {
            try {
                return clazz.getField(name);
            } catch (NoSuchFieldException nsfe) {
                try {
                    return clazz.getDeclaredField(name);
                } catch (NoSuchFieldException nsfe2) {
                    throw new RuntimeException("Error resolving field " + name);
                }
            }
        }
    }

    private static class ArrayIterator implements Iterator<Object> {
        private int offset;
        private final int size;
        private final Object array;

        ArrayIterator(Object array) {
            this.offset = -1;
            this.array = array;
            this.size = Array.getLength(array);
        }

        @Override
        public boolean hasNext() {
            return offset < size - 1;
        }

        @Override
        public Object next() {
            return Array.get(array, ++offset);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super Object> action) {
            throw new UnsupportedOperationException();
        }
    }
}

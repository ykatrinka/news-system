package ru.clevertec.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtil {

    public static <T extends Annotation> List<String> getFieldsByAnnotation(
            Class<?> clazz,
            Class<T> annotation
    ) {
        Field[] fields = clazz.getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(annotation))
                .map(Field::getName)
                .toList();
    }
}

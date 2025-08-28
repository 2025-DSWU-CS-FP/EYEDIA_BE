package com.eyedia.eyedia.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class CaseInsensitiveEnumConverterFactory implements ConverterFactory<String, Enum> {
    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            if (source == null) return null;
            try {
                return (T) Enum.valueOf(targetType, source.trim().toUpperCase());
            } catch (IllegalArgumentException e) {

                throw e;
            }
        };
    }
}


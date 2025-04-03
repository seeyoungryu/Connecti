package com.seeyoungryu.connecti.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.model.AlarmArgs;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AlarmArgsConverter implements AttributeConverter<AlarmArgs, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AlarmArgs attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);  // 객체 → JSON 문자열
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AlarmArgs 직렬화 실패", e);
        }
    }

    @Override
    public AlarmArgs convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, AlarmArgs.class);  // JSON 문자열 → 객체
        } catch (Exception e) {
            throw new RuntimeException("AlarmArgs 역직렬화 실패", e);
        }
    }
}

package me.oska.plugins.hibernate.converter;

import me.oska.plugins.ConvertibleAttribute;

import javax.persistence.AttributeConverter;

public class ConvertibleAttributeConverter implements AttributeConverter<ConvertibleAttribute, Integer>  {

    @Override
    public Integer convertToDatabaseColumn(ConvertibleAttribute attribute) {
        return attribute.getValue();
    }

    @Override
    public ConvertibleAttribute convertToEntityAttribute(Integer dbData) {
        return new ConvertibleAttribute(dbData);
    }

}

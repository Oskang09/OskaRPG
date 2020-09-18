package me.oska.plugins.hibernate.converter;

import com.google.gson.Gson;
import me.oska.minecraft.OskaRPG;
import me.oska.plugins.BasicAttribute;

import javax.persistence.AttributeConverter;

public class BasicAttributeConverter implements AttributeConverter<BasicAttribute, String> {
    private Gson json = OskaRPG.getGson();

    @Override
    public String convertToDatabaseColumn(BasicAttribute attribute) {
        return json.toJson(attribute);
    }

    @Override
    public BasicAttribute convertToEntityAttribute(String dbData) {
        return json.fromJson(dbData, BasicAttribute.class);
    }
}


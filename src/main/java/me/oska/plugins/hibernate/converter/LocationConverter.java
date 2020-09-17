package me.oska.plugins.hibernate.converter;

import com.google.gson.Gson;
import me.oska.minecraft.OskaRPG;
import org.bukkit.Location;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class LocationConverter implements AttributeConverter<Location, String>{

    private Gson json = OskaRPG.getGson();

    @Override
    public String convertToDatabaseColumn(Location attribute) {
        return json.toJson(attribute.serialize());
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        Map map = json.fromJson(dbData, Map.class);
        return Location.deserialize(map);
    }
}
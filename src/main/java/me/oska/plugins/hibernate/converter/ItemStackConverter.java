package me.oska.plugins.hibernate.converter;

import com.google.gson.Gson;
import me.oska.minecraft.OskaRPG;
import org.bukkit.inventory.ItemStack;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;


@Converter
public class ItemStackConverter implements AttributeConverter<ItemStack, String> {

    private Gson json = OskaRPG.getGson();

    @Override
    public String convertToDatabaseColumn(ItemStack attribute) {
        return json.toJson(attribute.serialize());
    }

    @Override
    public ItemStack convertToEntityAttribute(String dbData) {
        Map map = json.fromJson(dbData, Map.class);
        return ItemStack.deserialize(map);
    }
}

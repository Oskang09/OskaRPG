package me.oska.plugins.hibernate.converter;

import com.google.gson.Gson;
import me.oska.minecraft.OskaRPG;
import org.bukkit.inventory.ItemStack;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;


@Converter
public class ItemConverter implements AttributeConverter<ItemStack, Map<String, Object>> {

    @Override
    public Map<String, Object> convertToDatabaseColumn(ItemStack attribute) {
        return attribute.serialize();
    }

    @Override
    public ItemStack convertToEntityAttribute(Map<String, Object> dbData) {
        return ItemStack.deserialize(dbData);
    }
}

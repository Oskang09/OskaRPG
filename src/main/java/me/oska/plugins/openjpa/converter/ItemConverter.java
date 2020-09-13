package me.oska.plugins.openjpa.converter;

import me.oska.minecraft.OskaRPG;
import org.bukkit.inventory.ItemStack;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class ItemConverter implements AttributeConverter<ItemStack, String> {
    @Override
    public String convertToDatabaseColumn(ItemStack attribute) {
        return OskaRPG.getGson().toJson(attribute.serialize());
    }

    @Override
    public ItemStack convertToEntityAttribute(String dbData) {
        Map serializedItem = OskaRPG.getGson().fromJson(dbData, Map.class);
        return ItemStack.deserialize(serializedItem);
    }
}

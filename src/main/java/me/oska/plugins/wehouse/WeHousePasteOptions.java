package me.oska.plugins.wehouse;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;

public class WeHousePasteOptions {
    @Getter
    private Player player;

    @Getter
    private double x;

    @Getter
    private double y;

    @Getter
    private double z;

    @Getter
    private double size;

    @Getter
    @Setter
    private File storage;

    public WeHousePasteOptions(Player player, Block block, double size) {
        this.player = player;
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();

        if (this.size % 2 == 0) {
            throw new IllegalArgumentException("size must be odd number.");
        }
    }

    public double getMidpoint() {
        return size / 2;
    }
}

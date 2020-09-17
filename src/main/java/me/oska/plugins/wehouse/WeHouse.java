package me.oska.plugins.wehouse;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.oska.minecraft.OskaRPG;
import me.oska.plugins.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WeHouse {

    /*
        // Pasting
        cut   -> uuid-terrain.schem
        paste -> uuid-house.schem

        // Cutting
        cut -> uuid-house.schem
        paste -> uuid-terrain.schem
    */

    public static void register(JavaPlugin plugin) {

    }

    public static void cut(WeHouseCutOptions options) throws WorldEditException, IOException {
        double x = options.getX();
        double y = options.getY();
        double z = options.getZ();
        double height = options.getHeight();
        double midpoint = options.getMidpoint();
        Player player = options.getPlayer();

        World world = BukkitAdapter.adapt(player.getWorld());
        BlockVector3 pos1 = BlockVector3.at(x + midpoint, y, z - midpoint);
        BlockVector3 pos2 = BlockVector3.at(x - midpoint, y + (height - 1), z + midpoint);
        CuboidRegion region = new CuboidRegion(world, pos1, pos2);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSessionFactory factory = WorldEdit.getInstance().getEditSessionFactory();
        File file = new File(OskaRPG.getWeHouseFolder(), player.getUniqueId().toString() +".schem");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (
                EditSession session = factory.getEditSession(world, -1);
                ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file));
        ) {
            BlockReplace replacement = new BlockReplace(session, (pos) -> BlockTypes.AIR.getDefaultState().toBaseBlock());
            ForwardExtentCopy copy = new ForwardExtentCopy(session, region, clipboard, region.getMinimumPoint());
            copy.setCopyingBiomes(false);
            copy.setRemovingEntities(true);
            copy.setCopyingEntities(true);
            copy.setSourceFunction(replacement);
            Operations.complete(copy);
            writer.write(clipboard);
        }
    }

    public static void paste(WeHousePasteOptions options) throws WorldEditException, IOException {
        Player player = options.getPlayer();
        String uuid = player.getUniqueId().toString();
        double x = options.getX();
        double y = options.getY();
        double z = options.getZ();
        double midpoint = options.getMidpoint();
        Clipboard clipboard;
        World world = BukkitAdapter.adapt(player.getWorld());
        File file = new File(OskaRPG.getWeHouseFolder(), uuid + ".schem");
        EditSessionFactory factory = WorldEdit.getInstance().getEditSessionFactory();
        try (
                ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(file));
                EditSession session = factory.getEditSession(world, -1);
        ) {
            clipboard = reader.read();
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(session)
                    .copyEntities(true)
                    .ignoreAirBlocks(true)
                    .to(BlockVector3.at(x - midpoint,y,z-midpoint))
                    .build();
            Operations.complete(operation);
        }
    }
}

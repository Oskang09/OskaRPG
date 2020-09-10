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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class WeHouseAPI {

    private static final HashSet<String> cdMap = new HashSet();

    public static void cut(Player player, double x, double y, double z, double size, double height) {
        if (size % 2 == 0) {
            throw new IllegalArgumentException("size must be odd number.");
        }

        double midpoint = (size - 1) / 2;
        World world = BukkitAdapter.adapt(player.getWorld());
        BlockVector3 pos1 = BlockVector3.at(x + midpoint, y, z - midpoint);
        BlockVector3 pos2 = BlockVector3.at(x - midpoint, y + (height - 1), z + midpoint);
        CuboidRegion region = new CuboidRegion(world, pos1, pos2);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSessionFactory factory = WorldEdit.getInstance().getEditSessionFactory();
        File file = new File(OskaRPG.getWeHouseFolder(), player.getUniqueId().toString());
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
        } catch (WorldEditException | IOException e) {
            e.printStackTrace();
        } finally {
            Bukkit.getConsoleSender().sendMessage("clipboard copied successfully.");
        }
    }

    public static boolean paste(Player player, double x, double y, double z, double size) {
        String uuid = player.getUniqueId().toString();
        if (cdMap.contains(uuid)) {
            return false;
        }

        Clipboard clipboard;
        World world = BukkitAdapter.adapt(player.getWorld());
        double midpoint = (size - 1) / 2;
        File file = new File(OskaRPG.getWeHouseFolder(), uuid);
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
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        } finally {
            cdMap.add(uuid);
        }
        return true;
    }
}

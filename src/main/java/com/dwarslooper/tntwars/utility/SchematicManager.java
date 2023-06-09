package com.dwarslooper.tntwars.utility;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.arena.ArenaManager;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicManager {

    public static void paste(Location loc, File file, boolean ignoreAir) {
        Thread t = new Thread(() -> {
            BlockVector3 to = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            World world = FaweAPI.getWorld(loc.getWorld().getName());
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader = null;
            try {
                reader = format.getReader(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Clipboard clipboard = null;
            try {
                clipboard = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(to)
                        .ignoreAirBlocks(ignoreAir)
                        .build();
                Operations.complete(operation);
            }


            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                if(file.getName().equalsIgnoreCase("arena.schem")) {
                    Arena a = ArenaManager.getByName(file.getParentFile().getName());
                    if(a != null) {
                        a.setStatus(0);
                        a.updateSigns();
                    }
                }
            });
        });
        t.start();
    }

}

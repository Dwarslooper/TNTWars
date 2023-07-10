package com.dwarslooper.tntwars.arena;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.utility.DebugMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    public static HashMap<String, Arena> ARENAS = new HashMap<>();
    public static HashMap<Player, String> currently_editing = new HashMap<>();
    public static HashMap<Player, String> changeName = new HashMap<>();
    public static List<String> arena_list = new ArrayList<>();

    public static void reload() {

        if(!Main.getArenas().getConfiguration().isSet("arenas")) {
            Main.getArenas().getConfiguration().createSection("arenas");
            Main.getArenas().save();
            ArenaManager.reload();
            return;
        }

        ARENAS.clear();

        arena_list = new ArrayList<>(Main.getArenas().getConfiguration().getConfigurationSection("arenas").getKeys(false));

        Set<String> arenas = Main.getArenas().getConfiguration().getConfigurationSection("arenas").getKeys(false);

        for(String id_ : arenas) {
            ConfigurationSection arena = Main.getArenas().getConfiguration().getConfigurationSection("arenas").getConfigurationSection(id_);

            String id = arena.getString("id");
            String name = arena.getString("name");
            Location team1 = arena.getLocation("yellow.spawn");
            Location team2 = arena.getLocation("green.spawn");
            Location bound1 = arena.getLocation("bound1");
            Location bound2 = arena.getLocation("bound2");
            Location villager1 = arena.getLocation("yellow.villager");
            Location villager2 = arena.getLocation("green.villager");
            Location spawner1 = arena.getLocation("yellow.spawner");
            Location spawner2 = arena.getLocation("green.spawner");
            Location beacon1 = arena.getLocation("yellow.beacon");
            Location beacon2 = arena.getLocation("green.beacon");
            Location paste = arena.getLocation("paste");

            if(!validate(id, name, team1, team2, bound1, bound2, villager1, villager2, spawner1, spawner2, paste)) return;

            if(!arena.getBoolean("registered")) {
                Main.getInstance().getServer().getConsoleSender().sendMessage(Main.PREFIX + "§cArena §6" + id_ + " §chad all settings set but is not registered! To use arena you have to register it in the edit GUI!");
                return;
            }

            Arena a = new Arena(id, name, team1, team2, bound1, bound2, villager1, villager2, spawner1, spawner2, beacon1, beacon2, paste);
            ARENAS.put(id_, a);
            a.setStatus(0);
            a.updateSigns();
        }

        for(Arena a : ARENAS.values()) {
            Main.getInstance().getServer().getConsoleSender().sendMessage(Main.PREFIX + "§aArena registered: §6" + a.getName());
        }

    }

    public static Arena getByName(String name) {
        for(Arena a : ARENAS.values()) {
            if(a.getId().equalsIgnoreCase(name)) return a;
        }
        return null;
    }

    private static boolean validate(Object... values) {
        for(Object o : values) {
            if(o == null) return false;
        }
        return true;
    }

}

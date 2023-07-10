package com.dwarslooper.tntwars;

import com.dwarslooper.tntwars.arena.ArenaManager;
import com.dwarslooper.tntwars.commands.MainCommand;
import com.dwarslooper.tntwars.listener.ChatListener;
import com.dwarslooper.tntwars.listener.PlayerListener;
import com.dwarslooper.tntwars.listener.ServerListener;
import com.dwarslooper.tntwars.shop.ShopManager;
import com.dwarslooper.tntwars.utility.Config;
import com.dwarslooper.tntwars.utility.Screen;
import com.dwarslooper.tntwars.utility.Translate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    private static Main instance;
    public static Logger LOGGER;
    public static String PREFIX;
    public static Config arenas;

    public static String VERSION = "0.8";
    public static String STATE = "§cUNSTABLE";

    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        // Plugin startup logic

        LOGGER = Bukkit.getLogger();
        instance = this;
        saveDefaultConfig();
        arenas = new Config("arenas.yml", getDataFolder());
        PREFIX = "§8[§cTNT§8] §7";

        Translate.init();

        MainCommand command = new MainCommand();
        getCommand("tntwars").setExecutor(command);
        getCommand("tntwars").setTabCompleter(command);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Screen(1, ""), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new ServerListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);

        ArenaManager.reload();
        ShopManager.init();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return instance;
    }

    public static Config getArenas() {
        return arenas;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}

package com.dwarslooper.tntwars.utility;

import com.dwarslooper.tntwars.Main;
import org.bukkit.Bukkit;

public class DebugMode {

    public static void log(String log) {
        if(Main.getInstance().getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§7[§eDEBUG§7] " + log);
        }
    }

}

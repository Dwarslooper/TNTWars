package com.dwarslooper.tntwars.listener;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import static com.dwarslooper.tntwars.utility.Translate.translate;

public class ChatListener implements Listener {

    @EventHandler
    public void onChatAsync(AsyncPlayerChatEvent event) {
        if(ArenaManager.changeName.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                String arena = ArenaManager.changeName.get(event.getPlayer());
                ArenaManager.changeName.remove(event.getPlayer());
                ConfigurationSection section = Main.getArenas().getConfiguration().getConfigurationSection("arenas." + arena);
                if(section == null) {
                    return;
                } else if(event.getMessage().equalsIgnoreCase("EXIT;")) {
                    event.getPlayer().sendMessage(translate(Main.PREFIX + "::gui.edit.message.rename.cancelled"));
                    return;
                }
                String formatted = event.getMessage().replace("&", "§");
                Main.getArenas().getConfiguration().set("arenas." + arena + ".name", formatted);
                Main.getArenas().save();
                ArenaManager.reload();
                event.getPlayer().sendMessage(translate(Main.PREFIX + "::gui.edit.message.rename.success", formatted));
            });
        }
    }

}

package com.dwarslooper.tntwars.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.dwarslooper.tntwars.lobby.GameLobby;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import static com.dwarslooper.tntwars.lobby.LobbyHandler.GAMES;

public class ServerListener implements Listener {

    @EventHandler
    public void onTick(ServerTickStartEvent e) {
        for(GameLobby gl : GAMES) {
            gl.tick();
        }
    }

    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        if(event.getEntity() instanceof Fireball && event.getEntity().hasMetadata("isShotFromPlayer")) {
            event.setCancelled(true);
            event.getEntity().getLocation().createExplosion(2, false, true);
        }
    }

    @EventHandler
    public void entityMove(EntityMoveEvent event) {
        if(event.getEntity() instanceof Villager && event.getEntity().hasMetadata(".tntwarsShopVillager")) {
            event.setCancelled(true);
        }
    }
}

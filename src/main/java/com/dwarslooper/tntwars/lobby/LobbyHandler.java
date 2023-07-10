package com.dwarslooper.tntwars.lobby;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.utility.SchematicManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.dwarslooper.tntwars.utility.Translate.translate;

@SuppressWarnings("unchecked")
public class LobbyHandler {

    public static HashMap<UUID, Long> interactCooldown = new HashMap<>();
    public static HashMap<Player, GameLobby> playerInGame = new HashMap<>();
    public static ArrayList<GameLobby> GAMES = new ArrayList<>();

    public static int createGame(Arena arena) {
        if(arena.getCurrentLobby() != null) return 0;
        GameLobby lobby = new GameLobby(arena);
        GAMES.add(lobby);
        arena.setStatus(1);
        arena.updateSigns();
        return 1;
    }

    public static void resetGame(Arena arena) {
        arena.setStatus(3);
        arena.updateSigns();
        SchematicManager.paste(arena.getPaste(), arena.getFile(), false);
        GameLobby gl = arena.getCurrentLobby();
        arena.setCurrentLobby(null);
        if(gl != null) {
            GAMES.remove(gl);
            for(Entity e : gl.deleteOnReset) {
                if(!e.isDead()) {
                    e.remove();
                }
            }
            ((ArrayList<Player>)gl.getPlayers().clone()).forEach(gl::handleLeft);
        }
    }

    public static void startGame(GameLobby gameLobby) {
        gameLobby.getArena().setStatus(2);
        gameLobby.getArena().updateSigns();
        gameLobby.start();
    }

    public static void addToGame(GameLobby lobby, Player player) {
        if(!checkCooldown(player, false)) return;
        lobby.handleJoined(player);
    }

    public static void removeFromGame(GameLobby lobby, Player player) {
        if(!checkCooldown(player, false)) return;
        lobby.handleLeft(player);
    }

    public static boolean checkCooldown(Player p, boolean silent) {
        if(!onCooldown(p, Main.getInstance().getConfig().getInt("ratelimit"), silent)) {
            p.sendMessage(Main.PREFIX + translate("::system.ratelimited"));
            return false;
        }
        return true;
    }

    public static boolean onCooldown(Player player, int cooldown, boolean silent) {
        if(interactCooldown.containsKey(player.getUniqueId())) {
            long secondsLeft = ((interactCooldown.get(player.getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);
            if(secondsLeft > 0) {
                return false;
            }
        }
        if(!silent) interactCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }

}

package com.dwarslooper.tntwars.listener;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.arena.ArenaManager;
import com.dwarslooper.tntwars.commands.MainCommand;
import com.dwarslooper.tntwars.lobby.GameLobby;
import com.dwarslooper.tntwars.lobby.LobbyHandler;
import com.dwarslooper.tntwars.shop.ShopItem;
import com.dwarslooper.tntwars.shop.ShopManager;
import com.dwarslooper.tntwars.utility.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static com.dwarslooper.tntwars.utility.Translate.translate;

public class PlayerListener implements Listener {

    @EventHandler
    public void playerDamage(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if(p.getKiller() != null) {
            Player killer = p.getKiller();
            for(ItemStack itemStack : p.getInventory().getStorageContents()) {
                if(itemStack != null) killer.getInventory().addItem(itemStack);
            }
        }
        if(LobbyHandler.playerInGame.containsKey(p)) {
            event.setCancelled(true);
            GameLobby lobby = LobbyHandler.playerInGame.get(p);
            if(lobby.isIngame()) {
                p.getInventory().setStorageContents(null);
                p.getInventory().setItemInOffHand(null);
            }
            p.setFallDistance(0.0F);
            p.setHealth(20.0F);
            p.setFoodLevel(20);
            p.teleport(lobby.getTeamYellow().contains(p) ? lobby.getArena().getSpawnTeamYellow() : lobby.getArena().getSpawnTeamGreen());
        }
    }

    @EventHandler
    public void onPickupItem(PlayerAttemptPickupItemEvent event) {
        GameLobby lobby = LobbyHandler.playerInGame.get(event.getPlayer());
        if(lobby != null) {
            if(!lobby.isIngame()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().hasMetadata(".tntwarsShopVillager") && LobbyHandler.playerInGame.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            ShopManager.blocksGUI(p);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        GameLobby lobby = LobbyHandler.playerInGame.get(event.getPlayer());
        if(lobby != null) {
            lobby.handleLeft(event.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(LobbyHandler.playerInGame.containsKey(event.getPlayer())) {
            GameLobby lobby = LobbyHandler.playerInGame.get(event.getPlayer());
            if(!lobby.getBox().contains(Utils.locationToVector(event.getTo()))) {
                if(event.getTo().getY() < lobby.getBox().getMinY()) {
                    event.getPlayer().setHealth(0.0);
                    return;
                }
                event.setCancelled(true);
                return;
            }
            lobby.getMines().removeIf(lobby.getMineRemove()::contains);
            lobby.getMineRemove().clear();
            for (Location mine : lobby.getMines()) {
                if(mine.distance(event.getTo().clone().add(0, -1, 0)) < 0.8) {
                    mine.createExplosion(4, true, true);
                    lobby.getMineRemove().add(mine);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(LobbyHandler.playerInGame.containsKey(event.getPlayer())) {
            GameLobby lobby = LobbyHandler.playerInGame.get(event.getPlayer());
            if(lobby.getMines().contains(event.getBlock().getLocation())) {
                event.getBlock().getLocation().createExplosion(4, true, true);
                lobby.getMines().remove(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void playerClick(PlayerInteractEvent event) {
        if(LobbyHandler.playerInGame.containsKey(event.getPlayer())) {
            GameLobby lobby = LobbyHandler.playerInGame.get(event.getPlayer());

            if(!lobby.getBox().contains(Utils.locationToVector(event.getPlayer().getLocation()))) {
                event.setCancelled(true);
                return;
            }
            if(event.getItem() == null) return;
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                //All

                if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                    event.setCancelled(true);
                    if(!MainCommand.checkPermission(event.getPlayer(), "game.start")) return;
                    if(lobby.getPlayers().size() >= 2) {
                        LobbyHandler.startGame(lobby);
                    } else {
                        event.getPlayer().sendMessage(translate(Main.PREFIX + "::command.forcestart.error"));
                    }
                } else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.RED_BED) {
                    event.setCancelled(true);
                    lobby.handleLeft(event.getPlayer());
                }

                if(event.getItem().getType() == Material.FIRE_CHARGE) {
                    event.setCancelled(true);
                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                        IngameUtils.shootFireball(event.getPlayer());
                    } else {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().createExplosion(2);
                    }
                    ShopItem.removeItems(event.getPlayer().getInventory(), Material.FIRE_CHARGE, 1);
                }

                //Block

                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(event.getItem().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                        event.setCancelled(true);
                        lobby.getMines().add(event.getClickedBlock().getLocation());
                        ShopItem.removeItems(event.getPlayer().getInventory(), Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1);
                    }
                }

                //Air

            }
        } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock().getState() instanceof Sign sign) {
                for(Arena a : ArenaManager.ARENAS.values()) {
                    if(a.getSigns().contains(sign.getLocation())) {
                        if(a.getStatus() == 0 || a.getStatus() == 1) {
                            if(a.getCurrentLobby() == null) {
                                if(LobbyHandler.checkCooldown(event.getPlayer(), true)) {
                                    LobbyHandler.createGame(a);
                                    event.getPlayer().chat("/tntwars game " + a.getId() + " join");
                                }
                            } else {
                                event.getPlayer().chat("/tntwars game " + a.getId() + " join");
                            }
                        } else {
                            event.getPlayer().sendMessage(Main.PREFIX + translate("::game.message.already_started"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        for (HumanEntity entity : event.getViewers()) {
            if (entity instanceof Player player) {
                ItemStack[] item = event.getInventory().getMatrix();
                if(LobbyHandler.playerInGame.containsKey(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player) {
            GameLobby lobby = LobbyHandler.playerInGame.get(player);
            if(lobby != null) {
                if(!lobby.isIngame()) {
                    event.setCancelled(true);
                }
                if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                event.setDamage(event.getDamage() / 2);
            }

        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1);
        int index = command.indexOf(' ');
        command = (index >= 0 ? command.substring(0, index) : command);
        if(Main.getInstance().getConfig().getStringList("blocked_commands").contains(command)) {
            event.getPlayer().sendMessage(Main.PREFIX + translate("::ingame.blocked_command"));
            event.setCancelled(true);
            return;
        }
    }

}

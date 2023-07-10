package com.dwarslooper.tntwars.lobby;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.utility.StackCreator;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dwarslooper.tntwars.utility.Translate.translate;


public class GameLobby {

    boolean isIngame = false;
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Player> teamYellow = new ArrayList<>();
    ArrayList<Player> teamGreen = new ArrayList<>();
    ArrayList<Entity> deleteOnReset = new ArrayList<>();
    HashMap<Player, ItemStack[]> inventory = new HashMap<>();
    HashMap<Player, Location> prevPos = new HashMap<>();
    Arena arena;
    int cd = 0;
    int seconds = 20;
    int timePlayed;
    int copper;
    int iron;
    int gold;
    BoundingBox box;
    List<Location> mines = new ArrayList<>();
    List<Location> mineRemove = new ArrayList<>();

    public GameLobby(Arena arena) {
        this.arena = arena;
        box = new BoundingBox().resize(getArena().getBound1().getX(), getArena().getBound1().getY(), getArena().getBound1().getZ(), getArena().getBound2().getX(), getArena().getBound2().getY(), getArena().getBound2().getZ());
        arena.setCurrentLobby(this);
    }

    public void start() {
        getArena().getBeaconYellow().getBlock().setType(Material.BEACON);
        getArena().getBeaconGreen().getBlock().setType(Material.BEACON);
        isIngame = true;
        seconds = -42;
        players.forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setStorageContents(null);
            player.getInventory().setItemInOffHand(null);
        });
        Entity greenVillager = getArena().getPaste().getWorld().spawnEntity(getArena().getVillagerGreen(), EntityType.VILLAGER);
        greenVillager.setMetadata(".tntwarsShopVillager", new FixedMetadataValue(Main.getInstance(), true));
        greenVillager.setInvulnerable(true);
        deleteOnReset.add(greenVillager);
        Entity yellowVillager = getArena().getPaste().getWorld().spawnEntity(getArena().getVillagerYellow(), EntityType.VILLAGER);
        yellowVillager.setMetadata(".tntwarsShopVillager", new FixedMetadataValue(Main.getInstance(), true));
        yellowVillager.setInvulnerable(true);
        deleteOnReset.add(yellowVillager);
    }

    public void tick() {
        for(Player p : getPlayers()) {
            p.setFoodLevel(20);
        }

        if((!isIngame) && (seconds > -42)) {
            if (getPlayers().size() > 1) {
                if (cd < 20) {
                    cd++;
                } else {
                    countdown();
                    cd = 0;
                }
            } else {
                seconds = 20;
                for (Player player : getPlayers()) {
                    player.sendActionBar(translate("::game.message.waiting"));
                }
            }
        }

        if(!isIngame) return;

        for (Entity entity : getArena().getPaste().getWorld().getEntities()) {
            if(!(entity instanceof Item)) continue;
            if(box.contains(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ())) {
                deleteOnReset.add(entity);
            }
        }

        if(getArena().getBeaconYellow().getBlock().getType() != Material.BEACON) {
            winEvent(0);
        } else if(getArena().getBeaconGreen().getBlock().getType() != Material.BEACON) {
            winEvent(1);
        }

        timePlayed += 1;
        copper++;
        iron++;
        gold++;

        if(copper == 20) {
            generateItem(Material.COPPER_INGOT, translate("::game.item.copper_ingot"));
            copper = 0;
        }
        if(iron == 200) {
            generateItem(Material.IRON_INGOT, translate("::game.item.iron_ingot"));
            iron = 0;
        }
        if(gold == 1200) {
            generateItem(Material.GOLD_INGOT, translate("::game.item.gold_ingot"));
            gold = 0;
        }

    }

    public void handleJoined(Player p) {
        if(!getPlayers().contains(p)) {
            if(getArena().getStatus() == 2 || getArena().getStatus() == 3) {
                p.sendMessage(Main.PREFIX + translate("::game.message.already_started"));
                return;
            }
            LobbyHandler.GAMES.forEach(gameLobby -> {
                if(gameLobby.getPlayers().contains(p)) gameLobby.handleLeft(p);
            });

            p.setGameMode(GameMode.ADVENTURE);
            prevPos.put(p, p.getLocation());
            LobbyHandler.playerInGame.put(p, this);
            p.getInventory().clear();
            if(p.hasPermission("tntwars.admin.forcestart")) {
                p.getInventory().setItem(0, StackCreator.createItem(Material.EMERALD, 1, translate("::game.hud.start")));
            }
            p.getInventory().setItem(8, StackCreator.createItem(Material.RED_BED, 1, translate("::game.hud.leave")));
            addPlayer(p);
            getArena().setStatus(1);
            getArena().updateSigns();
        } else {
            p.sendMessage(Main.PREFIX + translate("::game.message.already_in_game"));
        }
    }

    public void handleLeft(Player p) {
        if(getPlayers().contains(p)) {
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);

            //Maybe keep this as a feature? Opinions?
/*
            if(SettingManager.inventoryManager) {
                p.getInventory().setContents(inventory.get(p));
                p.updateInventory();
                inventory.remove(p);
            }
 */

            p.teleport(prevPos.get(p));
            prevPos.remove(p);
            removePlayer(p);
            LobbyHandler.playerInGame.remove(p);
            if(getPlayers().size() <= 1) {
                if (getArena().getStatus() == 3) {
                    // Resetting, do nothing
                    return;
                } else if (getArena().getStatus() == 2) {
                    // Ingame, one player can't play so reset
                    for (Player player : getPlayers()) {
                        player.sendMessage(Main.PREFIX + translate("::game.message.no_opponent"));
                    }
                    LobbyHandler.resetGame(getArena());
                    return;
                } else if (getPlayers().isEmpty() && getArena().getStatus() == 1) {
                    // Waiting phase, no reset, just set status to inactive
                    getArena().setStatus(0);
                }
            }
            // Finally, update all signs for status
            getArena().updateSigns();
        } else {
            p.sendMessage(Main.PREFIX + translate("::game.message.not_in_game"));
        }
    }

    private void countdown() {
        for (Player player : getPlayers()) {
            player.sendActionBar(translate("::game.message.start_actionbar",""+seconds));
            if(seconds == 20 || seconds == 10 || seconds == 5 || seconds <= 3) {
                player.sendTitle((seconds == 0 ? "GO!" : ""+seconds), "", 0, 40, 0);
            }
        }
        if(seconds == 0) {
            start();
        }
        seconds--;
    }

    public Arena getArena() {
        return arena;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Entity> getDeleteOnReset() {
        return deleteOnReset;
    }

    public void group(Player player) {

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 10, true);
        meta.setUnbreakable(true);
        if(teamYellow.size() <= teamGreen.size()) {
            meta.setColor(Color.fromRGB(255, 230, 0));
            teamYellow.add(player);
            player.teleport(getArena().getSpawnTeamYellow());
        } else {
            meta.setColor(Color.fromRGB(31, 240, 31));
            teamGreen.add(player);
            player.teleport(getArena().getSpawnTeamGreen());
        }
        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boots.setItemMeta(meta);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

    }

    public void addPlayer(Player player) {
        getPlayers().add(player);
        group(player);
        getPlayers().forEach(player1 -> player1.sendMessage(Main.PREFIX + translate("::game.message.joined", getTeamYellow().contains(player) ? translate("::game.team.yellow") : translate("::game.team.green"), player.getName())));
    }

    public void removePlayer(Player player) {
        getPlayers().forEach(player1 -> player1.sendMessage(Main.PREFIX + translate("::game.message.left", getTeamYellow().contains(player) ? translate("::game.team.yellow") : translate("::game.team.green"), player.getName())));
        teamYellow.remove(player);
        teamGreen.remove(player);
        getPlayers().remove(player);
    }

    public ArrayList<Player> getTeamYellow() {
        return teamYellow;
    }

    public ArrayList<Player> getTeamGreen() {
        return teamGreen;
    }

    public void generateItem(Material material, String name) {
        World world = getArena().getPaste().getWorld();
        deleteOnReset.add(world.dropItem(getArena().getSpawnerYellow(), StackCreator.createItem(material, 1, name)));
        deleteOnReset.add(world.dropItem(getArena().getSpawnerGreen(), StackCreator.createItem(material, 1, name)));
    }

    public void winEvent(int team) {

        for(Entity e : deleteOnReset) {
            if(!e.isDead()) {
                e.remove();
            }
        }

        for (Player player : getPlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
            if((getTeamGreen().contains(player) ? 0 : 1) == team) {
                player.sendTitle(translate("::game.title.win"), translate("::game.subtitle.win"));
            } else {
                player.sendTitle(translate("::game.title.lose"), translate("::game.subtitle.lose"));
            }
        }

        isIngame = false;

        getArena().setStatus(3);
        getArena().updateSigns();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            LobbyHandler.resetGame(getArena());
        }, 20 * 20);
    }

    public boolean isIngame() {
        return isIngame;
    }

    public HashMap<Player, ItemStack[]> getInventory() {
        return inventory;
    }

    public List<Location> getMines() {
        return mines;
    }

    public List<Location> getMineRemove() {
        return mineRemove;
    }

    public BoundingBox getBox() {
        return box;
    }
}

package com.dwarslooper.tntwars.arena;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.lobby.GameLobby;
import com.dwarslooper.tntwars.utility.Utils;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.ArrayList;
import static com.dwarslooper.tntwars.utility.Translate.translate;

public class Arena {

    String id;
    String name;
    Location spawnTeamYellow;
    Location spawnTeamGreen;
    Location bound1;
    Location bound2;
    Location villagerYellow;
    Location villagerGreen;
    Location spawnerYellow;
    Location spawnerGreen;
    Location beaconYellow;
    Location beaconGreen;
    Location paste;
    ArrayList<Location> signs;
    int status;
    GameLobby currentLobby;

    public Arena(String id, String name, Location spawnTeamYellow, Location spawnTeamGreen, Location bound1, Location bound2, Location villagerYellow, Location villagerGreen, Location spawnerYellow, Location spawnerGreen, Location beaconYellow, Location beaconGreen, Location paste) {
        this.id = id;
        this.name = name;
        this.spawnTeamYellow = spawnTeamYellow;
        this.spawnTeamGreen = spawnTeamGreen;
        this.bound1 = bound1;
        this.bound2 = bound2;
        this.villagerYellow = villagerYellow;
        this.villagerGreen = villagerGreen;
        this.spawnerYellow = spawnerYellow;
        this.spawnerGreen = spawnerGreen;
        this.beaconYellow = beaconYellow;
        this.beaconGreen = beaconGreen;
        this.paste = paste;
        this.signs = new ArrayList<>();
        updateSignList();
    }

    public File getFile() {
        return new File(getFolder().getAbsolutePath() + "/arena.schem");
    }

    public ConfigurationSection getConfig() {
        return Main.getArenas().getConfiguration().getConfigurationSection("arenas." + getId());
    }

    public void saveConfig() {
        Main.getArenas().save();
    }

    public ArrayList<Location> getSigns() {
        return signs;
    }

    private void updateSignList() {
        if(getConfig().getList("signs") == null) return;
        signs.clear();
        getConfig().getList("signs").forEach(sign -> signs.add((Location) sign));
    }

    public boolean removeSign(Location location) {
        if(getConfig().getList("signs") == null) return false;
        if(getConfig().getList("signs").contains(location)) {
            getConfig().getList("signs").remove(location);
            saveConfig();
            return true;
        } else {
            return false;
        }
    }

    public boolean addSign(Location location) {
        ArrayList<Location> list = new ArrayList<>();
        if(getConfig().getList("signs") != null) list = (ArrayList<Location>) getConfig().getList("signs");
        if(list.contains(location)) return false;
        list.add(location);
        getConfig().set("signs", list);
        saveConfig();
        return true;
    }

    public void setStatus(int status) {
        this.status = status;
        getConfig().set("status", status);
        saveConfig();
        updateSigns();
    }

    public void updateSigns() {
        updateSignList();
        for(Location loc : getSigns()) {
            if(loc.getBlock().getState() instanceof Sign) {
                Sign sign = ((Sign) loc.getBlock().getState());
                sign.setLine(0, getName());
                sign.setLine(1, "===============");
                sign.setLine(2, getStatusTranslation());
                sign.setLine(3, (getStatus() == 1 || getStatus() == 2) ? String.valueOf(getCurrentLobby().getPlayers().size()) : "");
                sign.update();
            } else {
                removeSign(loc);
            }
        }
    }

    public int getStatus() {
        return status;
    }

    public String getStatusTranslation() {
        if(status == 0) return translate("::game.status.inactive");
        if(status == 1) return translate("::game.status.waiting");
        else if(status == 2) return translate("::game.status.running");
        else if(status == 3) return translate("::game.status.resetting");
        else return translate("::game.status.unknown");
    }

    public File getFolder() {
        return new File(Main.getInstance().getDataFolder(), "arenas/" + getId());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return Utils.replaceLast(name.replaceFirst("'", ""), "'", "");
    }

    public GameLobby getCurrentLobby() {
        return currentLobby;
    }

    public void setCurrentLobby(GameLobby currentLobby) {
        this.currentLobby = currentLobby;
    }

    public Location getPaste() {
        return paste;
    }

    public Location getBeaconYellow() {
        return beaconYellow;
    }

    public Location getBeaconGreen() {
        return beaconGreen;
    }

    public Location getSpawnerGreen() {
        return spawnerGreen;
    }

    public Location getSpawnerYellow() {
        return spawnerYellow;
    }

    public Location getSpawnTeamGreen() {
        return spawnTeamGreen;
    }

    public Location getSpawnTeamYellow() {
        return spawnTeamYellow;
    }

    public Location getVillagerGreen() {
        return villagerGreen;
    }

    public Location getVillagerYellow() {
        return villagerYellow;
    }

    public Location getBound1() {
        return bound1;
    }

    public Location getBound2() {
        return bound2;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", spawnTeamYellow=" + spawnTeamYellow +
                ", spawnTeamGreen=" + spawnTeamGreen +
                ", bound1=" + bound1 +
                ", bound2=" + bound2 +
                ", villagerYellow=" + villagerYellow +
                ", villagerGreen=" + villagerGreen +
                ", spawnerYellow=" + spawnerYellow +
                ", spawnerGreen=" + spawnerGreen +
                ", beaconYellow=" + beaconYellow +
                ", beaconGreen=" + beaconGreen +
                ", paste=" + paste +
                ", signs=" + signs +
                ", status=" + status +
                ", currentLobby=" + currentLobby +
                '}';
    }
}

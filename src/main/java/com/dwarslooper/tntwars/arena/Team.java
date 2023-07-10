package com.dwarslooper.tntwars.arena;

import org.bukkit.Location;

public class Team {

    Location respawn;
    Location villager;
    Location spawner;
    Location beacon;

    public Team(Location respawn, Location villager, Location spawner, Location beacon) {
        this.respawn = respawn;
        this.villager = villager;
        this.spawner = spawner;
        this.beacon = beacon;
    }

}

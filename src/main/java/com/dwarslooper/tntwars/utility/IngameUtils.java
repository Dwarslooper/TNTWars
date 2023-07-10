package com.dwarslooper.tntwars.utility;

import com.dwarslooper.tntwars.Main;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class IngameUtils {

    public static void shootFireball(Player player) {
        Fireball fb = player.launchProjectile(Fireball.class);
        fb.setMetadata("isShotFromPlayer", new FixedMetadataValue(Main.getInstance(), true));
    }

}

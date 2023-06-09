package com.dwarslooper.tntwars.utility;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Utils {
    public static String getProgressBar(int current, int max, int totalBars, String symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

    public static void lookAt(Entity entity1, Entity entity2) {
        Vector direction = entity2.getLocation().subtract(entity1.getLocation()).toVector();
        double yaw = Math.atan2(direction.getX(), direction.getZ());
        double pitch = Math.asin(direction.getY());
        Location newLoc = entity1.getLocation().setDirection(direction);
        newLoc.setYaw((float) Math.toDegrees(yaw) * -1);
        newLoc.setPitch((float) Math.toDegrees(pitch));
        entity1.teleport(newLoc);
    }

    public static Vector locationToVector(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

}

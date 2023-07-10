package com.dwarslooper.tntwars.setup;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.ArenaManager;
import com.dwarslooper.tntwars.utility.Screen;

import java.io.File;
import java.util.Locale;

public class Setup {


    public static int createNew(String input) {

        new Screen(2, "").addCondition(1, () -> {
            return !(Math.random() < 0.2);
        });

        String name = input.replaceAll("[^a-zA-Z0-9]", "");
        if(name.equalsIgnoreCase("")) return 2;

        String line = "arenas." + name.toLowerCase(Locale.ROOT);

        if(Main.arenas.get(line) != null) {
            return 1;
        }

        Main.arenas.set(line + ".id", name.toLowerCase());
        Main.arenas.set(line + ".name", input);
        Main.arenas.set(line + ".registered", false);
        Main.arenas.getConfiguration().createSection(line + ".signs");
        new File(Main.getInstance().getDataFolder(), "/arenas/" + name).mkdirs();

        Main.arenas.save();
        ArenaManager.reload();

        return 0;

    }

}

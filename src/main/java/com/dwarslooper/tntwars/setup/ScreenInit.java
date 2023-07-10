package com.dwarslooper.tntwars.setup;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.utility.Screen;
import com.dwarslooper.tntwars.utility.StackCreator;
import org.bukkit.Material;

import static com.dwarslooper.tntwars.utility.Translate.translate;


public class ScreenInit {

    public static Screen getConfirm() {
        return new Screen(3, translate(Main.PREFIX + "§2" + "::gui.confirm.header"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createItem(Material.LIME_TERRACOTTA, 1, translate("::gui.confirm.yes")))
                .addButton(16, StackCreator.createItem(Material.RED_TERRACOTTA, 1, translate("::gui.confirm.no")));
    }

}

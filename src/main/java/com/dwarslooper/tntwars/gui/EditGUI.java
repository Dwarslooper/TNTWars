package com.dwarslooper.tntwars.gui;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.arena.ArenaManager;
import com.dwarslooper.tntwars.setup.ScreenInit;
import com.dwarslooper.tntwars.utility.Screen;
import com.dwarslooper.tntwars.utility.StackCreator;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dwarslooper.tntwars.utility.Translate.translate;


public class EditGUI extends ClickGUI {

    int baseSlot;
    String set = translate("::gui.edit.status.set");
    String unset = translate("::gui.edit.status.unset");
    String changeText = translate("::gui.edit.helper.change");
    ConfigurationSection section;

    public EditGUI() {
        super("::gui.edit.header");
    }

    @Override
    public Inventory open(Player player) {
        baseSlot = 10;
        String ARENA_ID = ArenaManager.currently_editing.get(player);

        section = Main.getArenas().getConfiguration().getConfigurationSection("arenas").getConfigurationSection(ARENA_ID);
        Arena a = ArenaManager.getByName(ARENA_ID);

        String isSchemSet = new File(Main.getInstance().getDataFolder(),"arenas/" + ARENA_ID + "/arena.schem").exists() ? set : unset;

        String isSignsSet = translate("::gui.edit.status.numset", "0","0");
        if(section.isSet("signs")) isSignsSet = translate("::gui.edit.status.numset", String.valueOf(section.getList("signs") != null ? Objects.requireNonNull(section.getList("signs")).size() : 0),"0");

        String isBoundsSet = translate("::gui.edit.status.numunset", "0","2");
        int bounds = 0;
        if(section.getLocation("bound1") != null) bounds++;
        if(section.getLocation("bound2") != null) bounds++;
        if(bounds == 2) {
            isBoundsSet = translate("::gui.edit.status.numset", "" + bounds, "2");
        }


        Screen s = new Screen(6, translate(Main.PREFIX + "§2" + "::gui.edit.header"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createItem(Material.YELLOW_BANNER, 1, translate("§e::gui.edit.title.team_yellow"), translate("::gui.edit.desc.team_yellow")))
                .addButton(19, StackCreator.createItem(Material.LIME_BANNER, 1, translate("§a::gui.edit.title.team_green"), translate("::gui.edit.desc.team_green")));
        manageTeamItems(s, section, "yellow", player);
        manageTeamItems(s, section, "green", player);

        s.addButton(37, StackCreator.createItem(Material.WOODEN_AXE, 1, translate("§d::gui.edit.title.bound"), translate("§d::gui.edit.desc.bound.main"), translate("§d::gui.edit.desc.bound.left"), translate("§d::gui.edit.desc.bound.right"), isBoundsSet, changeText), () -> {
            section.set("bound1", player.getLocation().getBlock().getLocation());
            saveAndOpen(player);
        }, InventoryAction.PICKUP_ALL).addInteraction(37, () -> {
            section.set("bound2", player.getLocation().getBlock().getLocation());
            saveAndOpen(player);
        }, InventoryAction.PICKUP_HALF).addButton(38, StackCreator.createItem(Material.REDSTONE_TORCH, 1, translate("§d::gui.edit.title.paste"), translate("::gui.edit.desc.paste"), status("paste"), changeText), () -> {
            section.set("paste", player.getLocation().getBlock().getLocation());
            saveAndOpen(player);
        }).addButton(39, StackCreator.createItem(Material.MAP, 1, translate("§d::gui.edit.title.file"), translate("::gui.edit.desc.file"), isSchemSet, changeText), () -> {
            player.sendMessage(Main.PREFIX + translate("§c::gui.edit.error.file"));
            open(player);
        }).addButton(40, StackCreator.createItem(Material.OAK_SIGN, 1, translate("::gui.edit.title.signs"), translate("::gui.edit.desc.signs.main"), translate("::gui.edit.desc.signs.left"), translate("::gui.edit.desc.signs.right"), isSignsSet, changeText), player::closeInventory).addInteraction(40, () -> {
            if(a.addSign(player.getTargetBlock(4).getLocation())) {
                a.updateSigns();
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.added"));
            } else {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.already_existing"));
            }
        }, InventoryAction.PICKUP_ALL).addInteraction(40, () -> {
            if(a.removeSign(player.getTargetBlock(4).getLocation())) {
                a.updateSigns();
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.removed"));
            } else {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.not_existing"));
            }
        }, InventoryAction.PICKUP_HALF).addCondition(40, () -> {
            Block b = player.getTargetBlock(4);
            if(b == null) {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.look_at"));
                return false;
            }
            if(!(b.getState() instanceof Sign)) {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.look_at"));
                return false;
            }
            if(a == null) {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.signs.must_be_registered"));
                return false;
            }
            return true;
        }).addButton(41, StackCreator.createItem(Material.NAME_TAG, 1, translate("::gui.edit.title.rename"), translate("::gui.edit.desc.rename.main"), translate("::gui.edit.desc.rename.current", section.getString("name")), set, changeText), () -> {
            ArenaManager.changeName.put(player, ARENA_ID);
            player.sendMessage(Main.PREFIX + translate("::gui.edit.message.rename.type"));
            player.closeInventory();
        }).addButton(42, StackCreator.createItem(Material.FIREWORK_ROCKET, 1, translate("::gui.edit.title.finish"), translate("::gui.edit.desc.finish"), section.getBoolean("registered") ? set : unset, changeText), () -> {
            int success = 0;

            ConfigurationSection green = section.getConfigurationSection("green");
            ConfigurationSection yellow = section.getConfigurationSection("yellow");

            if(section.getLocation("bound1") != null && section.getLocation("bound2") != null) success++;
            if(green != null && green.getKeys(false).size() == 4) success++;
            if(yellow != null && yellow.getKeys(false).size() == 4) success++;
            if(section.get("paste") != null) success++;
            if(new File(Main.getInstance().getDataFolder(),"arenas/" + ARENA_ID + "/arena.schem").exists()) success++;
            if(section.getBoolean("registered")) {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.finish.already_done", ARENA_ID));
                player.closeInventory();
                return;
            }

            if(success == 5) {
                section.set("registered", true);
                Main.getArenas().save();
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.finish.success", ARENA_ID));
                new EditGUI().open(player);
                ArenaManager.reload();
            } else {
                player.sendMessage(Main.PREFIX + translate("::gui.edit.message.finish.checklist"));
                player.closeInventory();
            }

        }).addButton(43, StackCreator.createItem(Material.FIREWORK_STAR, 1, translate("::gui.edit.title.delete"), translate("::gui.edit.desc.delete"), changeText), () -> {
            ScreenInit.getConfirm()
                    .addButton(13, StackCreator.createItem(Material.PAPER, 1, translate("::confirm.delete_arena.prompt", ARENA_ID)))
                    .addInteraction(10, () -> {
                        Main.getArenas().getConfiguration().set("arenas." + ARENA_ID, null);
                        Main.getArenas().save();
                        try {
                            FileUtils.deleteDirectory(new File(Main.getInstance().getDataFolder(), "arenas/" + ARENA_ID));
                        } catch (Exception exception) {
                            player.sendMessage(Main.PREFIX + translate("::system.exception"));
                            exception.printStackTrace();
                        }
                        ArenaManager.reload();
                        player.closeInventory();
                        player.sendMessage(Main.PREFIX + translate("::confirm.delete_arena.success", ARENA_ID));
                    })
                    .addInteraction(16, player::closeInventory)
                    .open(player);
        });


        s.open(player);

        return s.getInventory();
    }

    private void manageTeamItems(Screen screen, ConfigurationSection section, String teamID, Player player) {

        String spawn = section.get(teamID + ".spawn") != null ? set : unset;
        String spawner = section.get(teamID + ".spawner") != null ? set : unset;
        String villager = section.get(teamID + ".villager") != null ? set : unset;
        String beacon = section.get(teamID + ".beacon") != null ? set : unset;

        screen.addButton(baseSlot + 2, StackCreator.createItem(Material.ENDER_PEARL, 1, translate("§7::gui.edit.title.respawn"), translate("::gui.edit.desc.respawn"), spawn, changeText), () -> {
            section.set(teamID + ".spawn", player.getLocation());
            saveAndOpen(player);
        }).addButton(baseSlot + 3, StackCreator.createItem(Material.EMERALD, 1, translate("§7::gui.edit.title.spawner"), translate("::gui.edit.desc.spawner"), spawner, changeText), () -> {
            section.set(teamID + ".spawner", player.getLocation());
            saveAndOpen(player);
        }).addButton(baseSlot + 4, StackCreator.createItem(Material.VILLAGER_SPAWN_EGG, 1, translate("§7::gui.edit.title.villager"), translate("::gui.edit.desc.villager"), villager, changeText), () -> {
            section.set(teamID + ".villager", player.getLocation());
            saveAndOpen(player);
        }).addButton(baseSlot + 5, StackCreator.createItem(Material.BEACON, 1, translate("§7::gui.edit.title.beacon"), translate("::gui.edit.desc.beacon"), beacon, changeText), () -> {
            section.set(teamID + ".beacon", player.getLocation().getBlock().getLocation());
            saveAndOpen(player);
        });
        baseSlot += 9;
    }

    private String status(String tag) {
        return (section.get(tag) != null) ? set : unset;
    }

    private void saveAndOpen(Player player) {
        Main.getArenas().save();
        open(player);
    }
}

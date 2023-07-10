package com.dwarslooper.tntwars.shop;

import com.dwarslooper.tntwars.utility.Screen;
import com.dwarslooper.tntwars.utility.StackCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.dwarslooper.tntwars.utility.Translate.translate;

public class ShopManager {

    private static ArrayList<ShopItem> items = new ArrayList<>();


    public static void init() {
        items.clear();
        items.add(new ShopItem(0, Material.SANDSTONE, "", 16, 4, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(0, Material.TERRACOTTA, "", 12, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(0, Material.END_STONE, "", 12, 24, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(0, Material.LADDER, "", 8, 4, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(0, Material.WATER_BUCKET, "", 1, 4, ShopItem.Currenty.IRON));
        items.add(new ShopItem(0, Material.OAK_FENCE, "", 1, 4, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(0, Material.STONE_PRESSURE_PLATE, "", 1, 4, ShopItem.Currenty.COPPER));

        items.add(new ShopItem(1, Material.REDSTONE, "", 16, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.REPEATER, "", 4, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.COMPARATOR, "", 4, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.REDSTONE_TORCH, "", 8, 4, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.DISPENSER, "", 2, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.DROPPER, "", 2, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.HOPPER, "", 4, 16, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.PISTON, "", 2, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.STICKY_PISTON, "", 2, 2, ShopItem.Currenty.IRON));
        items.add(new ShopItem(1, Material.OBSERVER, "", 4, 8, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.SLIME_BLOCK, "", 4, 1, ShopItem.Currenty.IRON));
        items.add(new ShopItem(1, Material.STONE_BUTTON, "", 1, 2, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(1, Material.LEVER, "", 1, 2, ShopItem.Currenty.COPPER));

        items.add(new ShopItem(2, Material.TNT, "", 8, 16, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(2, Material.CREEPER_SPAWN_EGG, "creeper", 1, 2, ShopItem.Currenty.IRON));
        items.add(new ShopItem(2, Material.FIRE_CHARGE, "", 1, 4, ShopItem.Currenty.IRON));
        items.add(new ShopItem(2, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, "mine", 1, 1, ShopItem.Currenty.GOLD));

        items.add(new ShopItem(3, Material.WOODEN_SWORD, "", 1, 4, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(3, Material.STONE_SWORD, "", 1, 3, ShopItem.Currenty.IRON));
        items.add(new ShopItem(3, Material.IRON_SWORD, "", 1, 1, ShopItem.Currenty.GOLD));
        items.add(new ShopItem(3, Material.DIAMOND_SWORD, "", 1, 4, ShopItem.Currenty.GOLD));
        items.add(new ShopItem(3, Material.STICK, "", 1, 2, ShopItem.Currenty.IRON, new ItemEnchantment(Enchantment.KNOCKBACK, 2)));
        items.add(new ShopItem(3, Material.STONE_PICKAXE, "", 1, 20, ShopItem.Currenty.COPPER));
        items.add(new ShopItem(3, Material.IRON_PICKAXE, "", 1, 4, ShopItem.Currenty.IRON));
        items.add(new ShopItem(3, Material.DIAMOND_PICKAXE, "", 1, 3, ShopItem.Currenty.GOLD));

        items.add(new ShopItem(4, Material.IRON_CHESTPLATE, "iron_armor", 1, 20, ShopItem.Currenty.IRON));
        items.add(new ShopItem(4, Material.DIAMOND_CHESTPLATE, "diamond_armor", 1, 40, ShopItem.Currenty.IRON));
        items.add(new ShopItem(4, Material.NETHERITE_CHESTPLATE, "netherite_armor", 1, 8, ShopItem.Currenty.GOLD));
        items.add(new ShopItem(4, Material.SHIELD, "", 1, 6, ShopItem.Currenty.IRON));

        items.add(new ShopItem(5, Material.BOW, "bow", 1, 2, ShopItem.Currenty.IRON));
        items.add(new ShopItem(5, Material.BOW, "bow_1", 1, 8, ShopItem.Currenty.IRON, new ItemEnchantment(Enchantment.ARROW_DAMAGE, 1)));
        items.add(new ShopItem(5, Material.BOW, "bow_2", 1, 2, ShopItem.Currenty.GOLD, new ItemEnchantment(Enchantment.ARROW_DAMAGE, 2), new ItemEnchantment(Enchantment.ARROW_KNOCKBACK, 1)));
        items.add(new ShopItem(5, Material.ARROW, "", 8, 16, ShopItem.Currenty.COPPER));

        items.add(new ShopItem(6, Material.GOLDEN_APPLE, "", 1, 4, ShopItem.Currenty.IRON));
        items.add(new ShopItem(6, Material.ENCHANTED_GOLDEN_APPLE, "", 1, 6, ShopItem.Currenty.GOLD));
        items.add(new ShopItem(6, Material.ENDER_PEARL, "", 1, 4, ShopItem.Currenty.GOLD));
        items.add(new ShopItem(6, Material.SNOWBALL, "shield", 4, 2, ShopItem.Currenty.IRON));
    }

    public static void categorize(Screen screen, int category, Player player) {
        int current = 19;
        for (ShopItem item : items) {
            if(item.getCategory() == category) {
                screen.addButton(current, item.getDisplay(), () -> item.buy(player));
                current++;
                if(current == 26 || current == 35 || current == 44) {
                    current+=2;
                }
            }
        }
    }

    public static void getHeader(Screen screen, Player player) {
        screen.addButton(1, StackCreator.createItem(Material.TERRACOTTA, 1, translate("::gui.shop.title.blocks")), () -> {
                    blocksGUI(player);
                })
                .addButton(2, StackCreator.createItem(Material.REDSTONE, 1, translate("::gui.shop.title.redstone")), () -> {
                    redstoneGUI(player);
                })
                .addButton(3, StackCreator.createItem(Material.TNT, 1, translate("::gui.shop.title.tnt")), () -> {
                    tntGUI(player);
                })
                .addButton(4, StackCreator.createItem(Material.IRON_SWORD, 1, translate("::gui.shop.title.sword")), () -> {
                    swordGUI(player);
                })
                .addButton(5, StackCreator.createItem(Material.IRON_CHESTPLATE, 1, translate("::gui.shop.title.armor")), () -> {
                    armorGUI(player);
                })
                .addButton(6, StackCreator.createItem(Material.BOW, 1, translate("::gui.shop.title.ranged")), () -> {
                    rangedGUI(player);
                })
                .addButton(7, StackCreator.createItem(Material.GOLDEN_APPLE, 1, translate("::gui.shop.title.utils")), () -> {
                    utilsGUI(player);
                });
    }

    public static void blocksGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.blocks"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 0, player);
        screen.open(player);

    }

    public static void redstoneGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.redstone"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 1, player);
        screen.open(player);
    }

    public static void tntGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.tnt"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 2, player);
        screen.open(player);
    }

    public static void swordGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.sword"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 3, player);
        screen.open(player);
    }

    public static void armorGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.armor"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 4, player);
        screen.open(player);
    }

    public static void rangedGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.ranged"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 5, player);
        screen.open(player);
    }

    public static void utilsGUI(Player player) {
        Screen screen = new Screen(5, translate("::gui.shop.title.utils"));
        getHeader(screen, player);
        screen.setBackground(Material.BLACK_STAINED_GLASS_PANE);
        categorize(screen, 6, player);
        screen.open(player);
    }

}

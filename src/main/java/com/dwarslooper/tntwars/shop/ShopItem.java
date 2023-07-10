package com.dwarslooper.tntwars.shop;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.utility.StackCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.dwarslooper.tntwars.utility.Translate.translate;

public class ShopItem {

    public enum Currenty {
        COPPER,
        IRON,
        GOLD
    }

    int category;
    Material type;
    String name;
    int amount;
    int cost;
    Currenty currenty;
    ItemEnchantment[] enchantments;

    public ShopItem(int category, Material type, String name, int amount, int cost, Currenty currenty, ItemEnchantment... enchantments) {
        this.category = category;
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.cost = cost;
        this.currenty = currenty;
        this.enchantments = enchantments;
    }

    public String getName() {
        return name.equalsIgnoreCase("") ? new ItemStack(type).getItemMeta().getLocalizedName() : translate("::gui.shop.items." + getRawName());
    }

    public String getRawName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public Currenty getCurrenty() {
        return currenty;
    }

    public int getCategory() {
        return category;
    }

    public Material getType() {
        return type;
    }

    public String getPrice() {
        return translate("::gui.shop.currency." + getCurrenty().name().toLowerCase(Locale.ROOT), ""+getCost());
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getDisplay() {
        return enchant(StackCreator.createItem(getType(), 1, getName(), getPrice() + " §7(" + getAmount() + "x)"));
    }

    public ItemStack getStack() {
        return enchant(StackCreator.createItem(getType(), getAmount(), getName()));
    }

    public ItemEnchantment[] getEnchantments() {
        return enchantments;
    }

    public ItemStack enchant(ItemStack itemStack) {
        for(ItemEnchantment enchantment : enchantments) {
            itemStack.addUnsafeEnchantment(enchantment.getEnchantment(), enchantment.getLevel());
        }
        return itemStack;
    }

    public boolean buy(Player player) {
        boolean ok = false;

        if(getCurrenty() == Currenty.COPPER && player.getInventory().contains(Material.COPPER_INGOT, cost)) {
            ok = true;
            removeItems(player.getInventory(), Material.COPPER_INGOT, cost);
        }
        else if(getCurrenty() == Currenty.IRON && player.getInventory().contains(Material.IRON_INGOT, cost)) {
            ok = true;
            removeItems(player.getInventory(), Material.IRON_INGOT, cost);
        }
        else if(getCurrenty() == Currenty.GOLD && player.getInventory().contains(Material.GOLD_INGOT, cost)) {
            ok = true;
            removeItems(player.getInventory(), Material.GOLD_INGOT, cost);
        }

        if(ok) {
            if(getType() == Material.IRON_CHESTPLATE || getType() == Material.DIAMOND_CHESTPLATE || getType() == Material.NETHERITE_CHESTPLATE) {
                if (getType() == Material.IRON_CHESTPLATE) {
                    player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                } else if (getType() == Material.DIAMOND_CHESTPLATE) {
                    player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                } else if (getType() == Material.NETHERITE_CHESTPLATE) {
                    player.getInventory().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
                }
                player.getInventory().getLeggings().addUnsafeEnchantment(Enchantment.BINDING_CURSE, 10);
                player.getInventory().getBoots().addUnsafeEnchantment(Enchantment.BINDING_CURSE, 10);
                return true;
            }
            player.getInventory().addItem(getStack());
        } else {
            player.sendMessage(Main.PREFIX + translate("::gui.shop.message.not_enough_ressources", getPrice().replaceFirst(""+getCost()+" ", ""), getName()));
        }

        return ok;
    }

    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }
}

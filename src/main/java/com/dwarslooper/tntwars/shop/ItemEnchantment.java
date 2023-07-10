package com.dwarslooper.tntwars.shop;

import org.bukkit.enchantments.Enchantment;

public class ItemEnchantment {

    Enchantment enchantment;
    int level;

    public ItemEnchantment(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public String toString() {
        return "ItemEnchantment{" +
                "enchantment=" + enchantment +
                ", level=" + level +
                '}';
    }
}

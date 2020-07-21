package dev.distressing.spleef.utils;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;

    public ItemBuilder(ItemStack itemStack) {
        this.item = itemStack;
    }

    public ItemBuilder setAmount(int count) {
        item.setAmount(count);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String name, HashMap<String, String> replacements) {
        ItemMeta meta = item.getItemMeta();

        String formatted = name;

        for (String placeholder : replacements.keySet()) {
            formatted = formatted.replaceAll(placeholder, replacements.get(placeholder));
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', formatted));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(ArrayList<String> lores) {
        ItemMeta meta = item.getItemMeta().clone();

        List<String> lore = new ArrayList<>();

        for (String loreline : lores) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(ArrayList<String> lores, HashMap<String, String> replacements) {
        ItemMeta meta = item.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        for (String loreline : lores) {
            String temp = loreline;

            for (String replace : replacements.keySet()) {
                temp = temp.replaceAll(replace, replacements.get(replace));
            }

            lore.add(ChatColor.translateAlternateColorCodes('&', temp));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        ItemMeta meta = item.getItemMeta();
        if (glowing) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.getEnchants().clear();
        }
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setIndistructable(boolean canBreak) {
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(canBreak);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }
}

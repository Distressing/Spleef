package dev.distressing.spleef.utils;

import org.bukkit.ChatColor;

public class Chat {
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}

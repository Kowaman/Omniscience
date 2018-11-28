package net.lordofthecraft.omniscience.util;

import org.bukkit.ChatColor;

public class Formatter {

    public static String getPageHeader(int page, int maxPages) {
        return ChatColor.DARK_AQUA + "======= " + ChatColor.AQUA + "Results (" + ChatColor.GOLD + "Page " + page + ChatColor.AQUA + "/" + maxPages + ")" + ChatColor.DARK_AQUA + " =======";
    }
}

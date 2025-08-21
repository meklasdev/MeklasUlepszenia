package pl.meklas.enhance.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Klasa do zarządzania wiadomościami
 */
public class Messages {
    private final ConfigManager configManager;
    
    public Messages(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Pobiera wiadomość z kolorami
     * @param key klucz wiadomości
     * @return sformatowana wiadomość
     */
    public String get(String key) {
        String message = configManager.getMessage(key);
        return colorize(message);
    }
    
    /**
     * Pobiera wiadomość z podstawieniami
     * @param key klucz wiadomości
     * @param replacements pary klucz-wartość do podstawienia
     * @return sformatowana wiadomość
     */
    public String get(String key, Object... replacements) {
        String message = get(key);
        
        // Podstaw prefix jeśli występuje
        if (message.contains("%prefix%")) {
            message = message.replace("%prefix%", get("prefix"));
        }
        
        // Podstaw inne zmienne
        for (int i = 0; i < replacements.length - 1; i += 2) {
            String placeholder = "%" + replacements[i] + "%";
            String value = String.valueOf(replacements[i + 1]);
            message = message.replace(placeholder, value);
        }
        
        return message;
    }
    
    /**
     * Wysyła wiadomość do gracza
     * @param player gracz
     * @param key klucz wiadomości
     */
    public void send(Player player, String key) {
        player.sendMessage(get(key));
    }
    
    /**
     * Wysyła wiadomość do gracza z podstawieniami
     * @param player gracz
     * @param key klucz wiadomości
     * @param replacements pary klucz-wartość do podstawienia
     */
    public void send(Player player, String key, Object... replacements) {
        player.sendMessage(get(key, replacements));
    }
    
    /**
     * Pobiera prefix wiadomości
     * @return prefix z kolorami
     */
    public String getPrefix() {
        return get("prefix");
    }
    
    /**
     * Koloruje tekst używając kodów kolorów Minecraft
     * @param text tekst do pokolorowania
     * @return pokolorowany tekst
     */
    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    // Często używane wiadomości
    
    public void sendNoItem(Player player) {
        send(player, "noItem");
    }
    
    public void sendNoMaterials(Player player) {
        send(player, "noMaterials");
    }
    
    public void sendNoMoney(Player player) {
        send(player, "noMoney");
    }
    
    public void sendPathLocked(Player player) {
        send(player, "pathLocked");
    }
    
    public void sendMaxLevel(Player player) {
        send(player, "maxLevel");
    }
    
    public void sendBannedItem(Player player) {
        send(player, "bannedItem");
    }
    
    public void sendSuccess(Player player) {
        send(player, "success");
    }
    
    public void sendFail(Player player) {
        send(player, "fail");
    }
    
    public void sendReloaded(Player player) {
        send(player, "reloaded");
    }
    
    public void sendItemsGiven(Player player) {
        send(player, "itemsGiven");
    }
    
    public void sendSimulationResult(Player player, int success, int fail, int total) {
        send(player, "simulationResult", 
             "success", success, 
             "fail", fail, 
             "total", total);
    }
}
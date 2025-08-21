package pl.meklas.enhance.config;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manager konfiguracji pluginu
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration upgrades;
    private FileConfiguration messages;
    private FileConfiguration gui;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }
    
    /**
     * Ładuje wszystkie pliki konfiguracyjne
     */
    public void loadConfigs() {
        // Zapisz domyślne pliki jeśli nie istnieją
        plugin.saveDefaultConfig();
        saveDefaultConfig("upgrades.yml");
        saveDefaultConfig("messages.yml");
        saveDefaultConfig("gui.yml");
        
        // Załaduj konfiguracje
        config = plugin.getConfig();
        upgrades = loadConfig("upgrades.yml");
        messages = loadConfig("messages.yml");
        gui = loadConfig("gui.yml");
    }
    
    /**
     * Przeładowuje wszystkie konfiguracje
     */
    public void reloadConfigs() {
        plugin.reloadConfig();
        loadConfigs();
    }
    
    private void saveDefaultConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
    
    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.getLogger().warning("Plik " + fileName + " nie istnieje!");
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(file);
    }
    
    // Gettery dla podstawowych ustawień
    
    public boolean isVaultEnabled() {
        return config.getBoolean("useVault", true);
    }
    
    public double getMaxDefensePercent() {
        return config.getDouble("maxDefensePercent", 20.0);
    }
    
    public Sound getSuccessSound() {
        try {
            return Sound.valueOf(config.getString("soundSuccess", "ENTITY_PLAYER_LEVELUP"));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Nieprawidłowy dźwięk sukcesu, używam domyślnego");
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }
    
    public Sound getFailSound() {
        try {
            return Sound.valueOf(config.getString("soundFail", "BLOCK_ANVIL_BREAK"));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Nieprawidłowy dźwięk porażki, używam domyślnego");
            return Sound.BLOCK_ANVIL_BREAK;
        }
    }
    
    public List<String> getBannedItems() {
        return config.getStringList("bannedItems");
    }
    
    public Map<String, Object> getMaterialSources() {
        return config.getConfigurationSection("materialSources").getValues(false);
    }
    
    // GUI Configuration
    
    public String getGuiInfoTitle() {
        return config.getString("gui.titleInfo", "&8[&aWzmocnienia&8]");
    }
    
    public String getGuiUpgradeTitle() {
        return config.getString("gui.titleUpgrade", "&8[&eUlepszanie&8]");
    }
    
    public int getGuiSize() {
        return config.getInt("gui.size", 54);
    }
    
    public int getInputItemSlot() {
        return config.getInt("gui.slots.inputItem", 20);
    }
    
    public int getInputMaterialSlot() {
        return config.getInt("gui.slots.inputMaterial", 29);
    }
    
    public List<Integer> getPathButtonSlots() {
        return config.getIntegerList("gui.slots.pathButtons");
    }
    
    public int getPreviewSlot() {
        return config.getInt("gui.slots.preview", 22);
    }
    
    public int getConfirmSlot() {
        return config.getInt("gui.slots.confirm", 26);
    }
    
    // Upgrade Configuration
    
    public int getSuccessChance(String path, int level) {
        String configPath = String.format("paths.%s.%d.successChance", path, level);
        return upgrades.getInt(configPath, 50);
    }
    
    public double getMoneyCost(String path, int level) {
        String configPath = String.format("paths.%s.%d.cost.money", path, level);
        return upgrades.getDouble(configPath, 0.0);
    }
    
    public Map<String, Object> getItemCosts(String path, int level) {
        String configPath = String.format("paths.%s.%d.cost.items", path, level);
        var section = upgrades.getConfigurationSection(configPath);
        return section != null ? section.getValues(false) : Map.of();
    }
    
    public String getOnFailBehavior() {
        return upgrades.getString("onFail.behavior", "DESTROY_ITEM");
    }
    
    // Messages Configuration
    
    public String getMessage(String key) {
        return messages.getString(key, "&cBrak wiadomości: " + key);
    }
    
    public String getPrefix() {
        return messages.getString("prefix", "&8[&aWzmocnienia&8] ");
    }
    
    // GUI Configuration from gui.yml
    
    public FileConfiguration getGuiConfig() {
        return gui;
    }
    
    public String getGuiMessage(String key) {
        return messages.getString("gui." + key, "&cBrak wiadomości: gui." + key);
    }
    
    /**
     * Zapisuje konfigurację do pliku
     */
    public void saveConfig(String fileName, FileConfiguration config) {
        try {
            File file = new File(plugin.getDataFolder(), fileName);
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie można zapisać " + fileName, e);
        }
    }
    
    /**
     * Sprawdza czy materiał jest zbanowany
     */
    public boolean isBannedMaterial(String material) {
        return getBannedItems().contains(material.toUpperCase());
    }
}
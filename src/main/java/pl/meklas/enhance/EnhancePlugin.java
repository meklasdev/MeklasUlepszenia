package pl.meklas.enhance;

import org.bukkit.plugin.java.JavaPlugin;
import pl.meklas.enhance.command.EnhanceCommand;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.config.Messages;
import pl.meklas.enhance.integration.VaultHook;
import pl.meklas.enhance.listener.DamageListener;
import pl.meklas.enhance.listener.UtilityListener;
import pl.meklas.enhance.service.CostService;
import pl.meklas.enhance.service.EnhancementService;
import pl.meklas.enhance.service.LoreService;
import pl.meklas.enhance.util.RandomProvider;

/**
 * Główna klasa pluginu Wzmocnienia Przedmiotów
 */
public class EnhancePlugin extends JavaPlugin {
    
    private ConfigManager configManager;
    private Messages messages;
    private VaultHook vaultHook;
    private EnhancementService enhancementService;
    private CostService costService;
    private LoreService loreService;
    private RandomProvider randomProvider;
    
    @Override
    public void onEnable() {
        getLogger().info("Włączanie pluginu Wzmocnienia Przedmiotów...");
        
        // Inicjalizacja komponentów
        initializeComponents();
        
        // Integracja z Vault
        setupVault();
        
        // Ustaw hook Vault w CostService
        if (vaultHook != null) {
            costService.setVaultHook(vaultHook);
        }
        
        // Rejestracja komend
        registerCommands();
        
        // Rejestracja listenerów
        registerListeners();
        
        getLogger().info("Plugin Wzmocnienia Przedmiotów został włączony!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Wyłączanie pluginu Wzmocnienia Przedmiotów...");
        
        // Cleanup jeśli potrzebny
        if (vaultHook != null) {
            vaultHook.disable();
        }
        
        getLogger().info("Plugin Wzmocnienia Przedmiotów został wyłączony!");
    }
    
    /**
     * Inicjalizuje wszystkie komponenty pluginu
     */
    private void initializeComponents() {
        // Konfiguracja i wiadomości
        configManager = new ConfigManager(this);
        messages = new Messages(configManager);
        
        // Utility
        randomProvider = new RandomProvider();
        
        // Serwisy
        loreService = new LoreService(messages);
        costService = new CostService(this, configManager);
        enhancementService = new EnhancementService(this, configManager, costService, loreService, randomProvider);
    }
    
    /**
     * Rejestruje komendy pluginu
     */
    private void registerCommands() {
        EnhanceCommand enhanceCommand = new EnhanceCommand(this);
        getCommand("enhance").setExecutor(enhanceCommand);
        getCommand("enhance").setTabCompleter(enhanceCommand);
    }
    
    /**
     * Rejestruje listenery eventów
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new UtilityListener(this), this);
    }
    
    /**
     * Konfiguruje integrację z Vault
     */
    private void setupVault() {
        if (configManager.isVaultEnabled() && getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHook = new VaultHook();
            if (vaultHook.setupEconomy()) {
                getLogger().info("Pomyślnie podłączono do Vault!");
            } else {
                getLogger().warning("Nie można podłączyć się do Vault - używanie tylko materiałów");
                vaultHook = null;
            }
        } else {
            getLogger().info("Vault wyłączony lub niedostępny - używanie tylko materiałów");
        }
    }
    
    /**
     * Przeładowuje konfigurację pluginu
     */
    public void reloadPlugin() {
        configManager.reloadConfigs();
        messages = new Messages(configManager);
        
        // Aktualizuj serwisy
        costService.updateConfig(configManager);
        enhancementService.updateConfig(configManager);
        
        // Ponownie skonfiguruj Vault jeśli potrzeba
        if (configManager.isVaultEnabled() && vaultHook == null) {
            setupVault();
        }
        
        getLogger().info("Konfiguracja została przeładowana!");
    }
    
    // Gettery dla innych klas
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public Messages getMessages() {
        return messages;
    }
    
    public VaultHook getVaultHook() {
        return vaultHook;
    }
    
    public EnhancementService getEnhancementService() {
        return enhancementService;
    }
    
    public CostService getCostService() {
        return costService;
    }
    
    public LoreService getLoreService() {
        return loreService;
    }
    
    public RandomProvider getRandomProvider() {
        return randomProvider;
    }
    
    /**
     * Sprawdza czy Vault jest dostępny
     */
    public boolean hasVault() {
        return vaultHook != null && vaultHook.isEnabled();
    }
}
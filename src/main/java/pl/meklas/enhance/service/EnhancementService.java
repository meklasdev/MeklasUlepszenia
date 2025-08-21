package pl.meklas.enhance.service;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.util.ItemNBT;
import pl.meklas.enhance.util.RandomProvider;

import java.util.logging.Level;

/**
 * Główny serwis do zarządzania wzmocnieniami
 */
public class EnhancementService {
    private final JavaPlugin plugin;
    private ConfigManager configManager;
    private final CostService costService;
    private final LoreService loreService;
    private final RandomProvider randomProvider;
    private final NamespacedKey pluginKey;
    
    public EnhancementService(JavaPlugin plugin, ConfigManager configManager, 
                            CostService costService, LoreService loreService, 
                            RandomProvider randomProvider) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.costService = costService;
        this.loreService = loreService;
        this.randomProvider = randomProvider;
        this.pluginKey = new NamespacedKey(plugin, "enhance");
    }
    
    /**
     * Aktualizuje konfigurację
     * @param configManager nowy manager konfiguracji
     */
    public void updateConfig(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Pobiera dane wzmocnienia z przedmiotu
     * @param item przedmiot
     * @return dane wzmocnienia
     */
    public EnhancementData getEnhancementData(ItemStack item) {
        return ItemNBT.loadEnhancementData(item, pluginKey);
    }
    
    /**
     * Sprawdza czy przedmiot może być wzmacniany
     * @param item przedmiot
     * @return true jeśli może być wzmacniany
     */
    public boolean canBeEnhanced(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        // Sprawdź czy materiał jest zbanowany
        if (configManager.isBannedMaterial(item.getType().name())) {
            return false;
        }
        
        return ItemNBT.isEnhanceable(item);
    }
    
    /**
     * Sprawdza czy ścieżka jest dostępna dla przedmiotu
     * @param item przedmiot
     * @param path ścieżka
     * @return true jeśli dostępna
     */
    public boolean isPathAvailable(ItemStack item, PathType path) {
        if (!canBeEnhanced(item)) {
            return false;
        }
        
        EnhancementData currentData = getEnhancementData(item);
        
        // Jeśli przedmiot nie ma wzmocnienia, wszystkie ścieżki są dostępne
        if (currentData.isEmpty()) {
            return true;
        }
        
        // Jeśli ma wzmocnienie, dostępna jest tylko ta sama ścieżka
        return currentData.getPath() == path;
    }
    
    /**
     * Sprawdza czy przedmiot może być ulepszony
     * @param item przedmiot
     * @param path ścieżka
     * @return true jeśli może być ulepszony
     */
    public boolean canUpgrade(ItemStack item, PathType path) {
        if (!isPathAvailable(item, path)) {
            return false;
        }
        
        EnhancementData currentData = getEnhancementData(item);
        
        // Jeśli nie ma wzmocnienia, może być ulepszony do poziomu 1
        if (currentData.isEmpty()) {
            return true;
        }
        
        // Sprawdź czy nie osiągnął maksymalnego poziomu
        return !currentData.isMaxLevel();
    }
    
    /**
     * Pobiera szansę powodzenia dla ulepszenia
     * @param item przedmiot
     * @param path ścieżka
     * @return szansa w procentach
     */
    public int getSuccessChance(ItemStack item, PathType path) {
        EnhancementData currentData = getEnhancementData(item);
        int nextLevel = currentData.isEmpty() ? 1 : currentData.getLevel() + 1;
        
        return configManager.getSuccessChance(path.name(), nextLevel);
    }
    
    /**
     * Sprawdza czy gracz może zapłacić za ulepszenie
     * @param player gracz
     * @param item przedmiot
     * @param path ścieżka
     * @return true jeśli może zapłacić
     */
    public boolean canAffordUpgrade(Player player, ItemStack item, PathType path) {
        EnhancementData currentData = getEnhancementData(item);
        int nextLevel = currentData.isEmpty() ? 1 : currentData.getLevel() + 1;
        
        return costService.canAfford(player, path, nextLevel);
    }
    
    /**
     * Wykonuje ulepszenie przedmiotu
     * @param player gracz wykonujący ulepszenie
     * @param item przedmiot do ulepszenia
     * @param path ścieżka wzmocnienia
     * @return wynik ulepszenia
     */
    public UpgradeResult performUpgrade(Player player, ItemStack item, PathType path) {
        // Sprawdzenia podstawowe
        if (!canUpgrade(item, path)) {
            return UpgradeResult.INVALID_ITEM;
        }
        
        EnhancementData currentData = getEnhancementData(item);
        int nextLevel = currentData.isEmpty() ? 1 : currentData.getLevel() + 1;
        
        // Sprawdź koszty
        if (!costService.canAfford(player, path, nextLevel)) {
            return UpgradeResult.INSUFFICIENT_RESOURCES;
        }
        
        // Pobierz koszty
        if (!costService.takeCosts(player, path, nextLevel)) {
            return UpgradeResult.INSUFFICIENT_RESOURCES;
        }
        
        // Wykonaj losowanie
        int successChance = configManager.getSuccessChance(path.name(), nextLevel);
        boolean success = randomProvider.rollSuccess(successChance);
        
        // Loguj próbę
        plugin.getLogger().info(String.format(
            "Gracz %s próbuje ulepszyć %s na ścieżce %s do poziomu %d (szansa: %d%%) - %s",
            player.getName(),
            item.getType().name(),
            path.name(),
            nextLevel,
            successChance,
            success ? "SUKCES" : "PORAŻKA"
        ));
        
        if (success) {
            // Sukces - zapisz nowe dane wzmocnienia
            EnhancementData newData = new EnhancementData(path, nextLevel, plugin.getDescription().getVersion());
            ItemNBT.saveEnhancementData(item, newData, pluginKey);
            loreService.updateItemLore(item, newData);
            loreService.updateItemLoreWithChance(item, newData, 
                newData.isMaxLevel() ? 0 : configManager.getSuccessChance(path.name(), nextLevel + 1));
            
            return UpgradeResult.SUCCESS;
        } else {
            // Porażka - usuń przedmiot zgodnie z konfiguracją
            String failBehavior = configManager.getOnFailBehavior();
            
            switch (failBehavior) {
                case "DESTROY_ITEM" -> {
                    // Usuń przedmiot (ustaw amount na 0)
                    item.setAmount(0);
                    return UpgradeResult.FAILURE_DESTROYED;
                }
                case "DOWNGRADE" -> {
                    // Na przyszłość - obniż poziom
                    if (!currentData.isEmpty() && currentData.getLevel() > 1) {
                        EnhancementData downgradedData = new EnhancementData(
                            currentData.getPath(), 
                            currentData.getLevel() - 1, 
                            currentData.getVersion()
                        );
                        ItemNBT.saveEnhancementData(item, downgradedData, pluginKey);
                        loreService.updateItemLore(item, downgradedData);
                    } else {
                        // Usuń wzmocnienie całkowicie
                        ItemNBT.removeEnhancement(item, pluginKey);
                        loreService.updateItemLore(item, EnhancementData.empty());
                    }
                    return UpgradeResult.FAILURE_DOWNGRADED;
                }
                case "KEEP" -> {
                    // Zachowaj przedmiot bez zmian
                    return UpgradeResult.FAILURE_KEPT;
                }
                default -> {
                    // Domyślnie zniszcz
                    item.setAmount(0);
                    return UpgradeResult.FAILURE_DESTROYED;
                }
            }
        }
    }
    
    /**
     * Symuluje ulepszenie bez pobierania kosztów
     * @param path ścieżka
     * @param level poziom
     * @param iterations liczba iteracji
     * @return wyniki symulacji
     */
    public SimulationResult simulateUpgrade(PathType path, int level, int iterations) {
        int successChance = configManager.getSuccessChance(path.name(), level);
        int successes = 0;
        int failures = 0;
        
        for (int i = 0; i < iterations; i++) {
            if (randomProvider.rollSuccess(successChance)) {
                successes++;
            } else {
                failures++;
            }
        }
        
        return new SimulationResult(successes, failures, iterations, successChance);
    }
    
    /**
     * Enum wyników ulepszenia
     */
    public enum UpgradeResult {
        SUCCESS,
        FAILURE_DESTROYED,
        FAILURE_DOWNGRADED,
        FAILURE_KEPT,
        INVALID_ITEM,
        INSUFFICIENT_RESOURCES
    }
    
    /**
     * Klasa wyników symulacji
     */
    public static class SimulationResult {
        private final int successes;
        private final int failures;
        private final int total;
        private final int expectedChance;
        
        public SimulationResult(int successes, int failures, int total, int expectedChance) {
            this.successes = successes;
            this.failures = failures;
            this.total = total;
            this.expectedChance = expectedChance;
        }
        
        public int getSuccesses() { return successes; }
        public int getFailures() { return failures; }
        public int getTotal() { return total; }
        public int getExpectedChance() { return expectedChance; }
        
        public double getActualSuccessRate() {
            return total > 0 ? (double) successes / total * 100.0 : 0.0;
        }
        
        public double getDeviation() {
            return getActualSuccessRate() - expectedChance;
        }
    }
}
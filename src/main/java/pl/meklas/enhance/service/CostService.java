package pl.meklas.enhance.service;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.integration.VaultHook;
import pl.meklas.enhance.model.PathType;

import java.util.*;

/**
 * Serwis do zarządzania kosztami ulepszeń
 */
public class CostService {
    private final JavaPlugin plugin;
    private ConfigManager configManager;
    private VaultHook vaultHook;
    
    public CostService(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.vaultHook = null; // Zostanie ustawiony przez EnhancePlugin
    }
    
    /**
     * Ustawia hook do Vault
     * @param vaultHook hook do Vault
     */
    public void setVaultHook(VaultHook vaultHook) {
        this.vaultHook = vaultHook;
    }
    
    /**
     * Aktualizuje konfigurację
     * @param configManager nowy manager konfiguracji
     */
    public void updateConfig(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Sprawdza czy gracz może zapłacić za ulepszenie
     * @param player gracz
     * @param path ścieżka wzmocnienia
     * @param level poziom do osiągnięcia
     * @return true jeśli może zapłacić
     */
    public boolean canAfford(Player player, PathType path, int level) {
        // Sprawdź koszty pieniężne
        if (!canAffordMoney(player, path, level)) {
            return false;
        }
        
        // Sprawdź koszty materiałowe
        return canAffordItems(player, path, level);
    }
    
    /**
     * Sprawdza czy gracz ma wystarczającą ilość pieniędzy
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     * @return true jeśli ma wystarczająco
     */
    public boolean canAffordMoney(Player player, PathType path, int level) {
        double cost = configManager.getMoneyCost(path.name(), level);
        
        if (cost <= 0) {
            return true; // Brak kosztu pieniężnego
        }
        
        if (vaultHook == null || !vaultHook.isEnabled()) {
            return true; // Vault wyłączony
        }
        
        return vaultHook.hasEnoughMoney(player, cost);
    }
    
    /**
     * Sprawdza czy gracz ma wystarczającą ilość materiałów
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     * @return true jeśli ma wystarczająco
     */
    public boolean canAffordItems(Player player, PathType path, int level) {
        Map<String, Object> itemCosts = configManager.getItemCosts(path.name(), level);
        
        if (itemCosts.isEmpty()) {
            return true; // Brak kosztów materiałowych
        }
        
        // Sprawdź każdy wymagany materiał
        for (Map.Entry<String, Object> entry : itemCosts.entrySet()) {
            String materialName = entry.getKey();
            int requiredAmount = ((Number) entry.getValue()).intValue();
            
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Nieprawidłowy materiał w konfiguracji: " + materialName);
                continue;
            }
            
            int playerAmount = countItems(player, material);
            if (playerAmount < requiredAmount) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Pobiera koszty od gracza
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     * @return true jeśli sukces
     */
    public boolean takeCosts(Player player, PathType path, int level) {
        // Najpierw sprawdź czy może zapłacić
        if (!canAfford(player, path, level)) {
            return false;
        }
        
        // Pobierz pieniądze
        if (!takeMoney(player, path, level)) {
            return false;
        }
        
        // Pobierz materiały
        if (!takeItems(player, path, level)) {
            // Zwróć pieniądze jeśli nie udało się pobrać materiałów
            giveMoney(player, path, level);
            return false;
        }
        
        return true;
    }
    
    /**
     * Pobiera pieniądze od gracza
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     * @return true jeśli sukces
     */
    private boolean takeMoney(Player player, PathType path, int level) {
        double cost = configManager.getMoneyCost(path.name(), level);
        
        if (cost <= 0) {
            return true; // Brak kosztu
        }
        
        if (vaultHook == null || !vaultHook.isEnabled()) {
            return true; // Vault wyłączony
        }
        
        return vaultHook.takeMoney(player, cost);
    }
    
    /**
     * Pobiera materiały od gracza
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     * @return true jeśli sukces
     */
    private boolean takeItems(Player player, PathType path, int level) {
        Map<String, Object> itemCosts = configManager.getItemCosts(path.name(), level);
        
        if (itemCosts.isEmpty()) {
            return true; // Brak kosztów materiałowych
        }
        
        // Pobierz każdy wymagany materiał
        for (Map.Entry<String, Object> entry : itemCosts.entrySet()) {
            String materialName = entry.getKey();
            int requiredAmount = ((Number) entry.getValue()).intValue();
            
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Nieprawidłowy materiał w konfiguracji: " + materialName);
                continue;
            }
            
            if (!removeItems(player, material, requiredAmount)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Zwraca koszty graczowi (przy niepowodzeniu)
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     */
    public void refundCosts(Player player, PathType path, int level) {
        giveMoney(player, path, level);
        giveItems(player, path, level);
    }
    
    /**
     * Zwraca pieniądze graczowi
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     */
    private void giveMoney(Player player, PathType path, int level) {
        double cost = configManager.getMoneyCost(path.name(), level);
        
        if (cost <= 0 || vaultHook == null || !vaultHook.isEnabled()) {
            return;
        }
        
        vaultHook.giveMoney(player, cost);
    }
    
    /**
     * Zwraca materiały graczowi
     * @param player gracz
     * @param path ścieżka
     * @param level poziom
     */
    private void giveItems(Player player, PathType path, int level) {
        Map<String, Object> itemCosts = configManager.getItemCosts(path.name(), level);
        
        for (Map.Entry<String, Object> entry : itemCosts.entrySet()) {
            String materialName = entry.getKey();
            int amount = ((Number) entry.getValue()).intValue();
            
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                continue;
            }
            
            ItemStack item = new ItemStack(material, amount);
            player.getInventory().addItem(item);
        }
    }
    
    /**
     * Liczy ilość przedmiotów danego typu w ekwipunku gracza
     * @param player gracz
     * @param material materiał
     * @return ilość przedmiotów
     */
    private int countItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    /**
     * Usuwa przedmioty z ekwipunku gracza
     * @param player gracz
     * @param material materiał
     * @param amount ilość do usunięcia
     * @return true jeśli sukces
     */
    private boolean removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                if (item.getAmount() >= remaining) {
                    item.setAmount(item.getAmount() - remaining);
                    return true;
                } else {
                    remaining -= item.getAmount();
                    item.setAmount(0);
                }
            }
        }
        
        return remaining == 0;
    }
    
    /**
     * Pobiera opis kosztów dla GUI
     * @param path ścieżka
     * @param level poziom
     * @return lista opisów kosztów
     */
    public List<String> getCostDescription(PathType path, int level) {
        List<String> description = new ArrayList<>();
        
        // Koszt pieniężny
        double moneyCost = configManager.getMoneyCost(path.name(), level);
        if (moneyCost > 0 && vaultHook != null && vaultHook.isEnabled()) {
            description.add("§7Koszt: §e" + vaultHook.format(moneyCost));
        }
        
        // Koszty materiałowe
        Map<String, Object> itemCosts = configManager.getItemCosts(path.name(), level);
        if (!itemCosts.isEmpty()) {
            description.add("§7Materiały:");
            for (Map.Entry<String, Object> entry : itemCosts.entrySet()) {
                String materialName = entry.getKey();
                int amount = ((Number) entry.getValue()).intValue();
                description.add("§7• " + amount + "x " + formatMaterialName(materialName));
            }
        }
        
        return description;
    }
    
    /**
     * Formatuje nazwę materiału do wyświetlenia
     * @param materialName nazwa materiału
     * @return sformatowana nazwa
     */
    private String formatMaterialName(String materialName) {
        return materialName.toLowerCase().replace('_', ' ');
    }
}
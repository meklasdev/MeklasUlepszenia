package pl.meklas.enhance.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Hook do integracji z Vault
 */
public class VaultHook {
    private Economy economy;
    private boolean enabled;
    
    public VaultHook() {
        this.enabled = false;
    }
    
    /**
     * Konfiguruje połączenie z Vault Economy
     * @return true jeśli sukces
     */
    public boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        
        if (rsp == null) {
            return false;
        }
        
        economy = rsp.getProvider();
        enabled = economy != null;
        return enabled;
    }
    
    /**
     * Sprawdza czy gracz ma wystarczającą ilość pieniędzy
     * @param player gracz
     * @param amount kwota
     * @return true jeśli ma wystarczająco
     */
    public boolean hasEnoughMoney(Player player, double amount) {
        if (!enabled || amount <= 0) {
            return true;
        }
        return economy.has(player, amount);
    }
    
    /**
     * Pobiera pieniądze od gracza
     * @param player gracz
     * @param amount kwota
     * @return true jeśli sukces
     */
    public boolean takeMoney(Player player, double amount) {
        if (!enabled || amount <= 0) {
            return true;
        }
        
        if (!hasEnoughMoney(player, amount)) {
            return false;
        }
        
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
    
    /**
     * Zwraca pieniądze graczowi
     * @param player gracz
     * @param amount kwota
     * @return true jeśli sukces
     */
    public boolean giveMoney(Player player, double amount) {
        if (!enabled || amount <= 0) {
            return true;
        }
        
        return economy.depositPlayer(player, amount).transactionSuccess();
    }
    
    /**
     * Pobiera saldo gracza
     * @param player gracz
     * @return saldo
     */
    public double getBalance(Player player) {
        if (!enabled) {
            return 0.0;
        }
        return economy.getBalance(player);
    }
    
    /**
     * Formatuje kwotę do wyświetlenia
     * @param amount kwota
     * @return sformatowana kwota
     */
    public String format(double amount) {
        if (!enabled) {
            return String.valueOf(amount);
        }
        return economy.format(amount);
    }
    
    /**
     * Pobiera nazwę waluty
     * @return nazwa waluty
     */
    public String getCurrencyName() {
        if (!enabled) {
            return "monet";
        }
        return economy.currencyNamePlural();
    }
    
    /**
     * Sprawdza czy Vault jest włączony
     * @return true jeśli włączony
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Wyłącza hook
     */
    public void disable() {
        enabled = false;
        economy = null;
    }
}
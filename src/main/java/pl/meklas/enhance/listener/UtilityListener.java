package pl.meklas.enhance.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.meklas.enhance.EnhancePlugin;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.service.EnhancementService;
import pl.meklas.enhance.util.ItemNBT;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener obsługujący efekty ścieżki UTILITY
 */
public class UtilityListener implements Listener {
    private final EnhancePlugin plugin;
    private final EnhancementService enhancementService;
    
    // Mapa do śledzenia graczy z efektami utility
    private final Map<UUID, Boolean> playersWithSpeedBoost = new HashMap<>();
    private final Map<UUID, Boolean> playersWithHasteBoost = new HashMap<>();
    
    public UtilityListener(EnhancePlugin plugin) {
        this.plugin = plugin;
        this.enhancementService = plugin.getEnhancementService();
        
        // Uruchom zadanie sprawdzające efekty co sekundę
        startUtilityEffectTask();
    }
    
    /**
     * Obsługuje zmianę trzymanego przedmiotu
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // Sprawdź efekt Haste po krótkiej chwili (żeby nowy przedmiot był już w ręce)
        new BukkitRunnable() {
            @Override
            public void run() {
                updateHasteEffect(player);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    /**
     * Obsługuje ruch gracza (do sprawdzania pełnego setu zbroi)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Sprawdzaj efekt Speed tylko przy zmianie bloku (optymalizacja)
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            
            updateSpeedEffect(event.getPlayer());
        }
    }
    
    /**
     * Uruchamia zadanie sprawdzające efekty utility co sekundę
     */
    private void startUtilityEffectTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    updateUtilityEffects(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Co sekundę
    }
    
    /**
     * Aktualizuje wszystkie efekty utility dla gracza
     * @param player gracz
     */
    private void updateUtilityEffects(Player player) {
        updateSpeedEffect(player);
        updateHasteEffect(player);
    }
    
    /**
     * Aktualizuje efekt Speed dla gracza
     * @param player gracz
     */
    private void updateSpeedEffect(Player player) {
        boolean shouldHaveSpeed = hasFullUtilityArmorSet(player);
        boolean currentlyHasSpeed = playersWithSpeedBoost.getOrDefault(player.getUniqueId(), false);
        
        if (shouldHaveSpeed && !currentlyHasSpeed) {
            // Dodaj efekt Speed
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                Integer.MAX_VALUE, // Nieskończony czas
                0, // Speed I
                false, // ambient
                false, // particles
                false  // icon
            ));
            playersWithSpeedBoost.put(player.getUniqueId(), true);
            
            plugin.getLogger().fine("Dodano efekt Speed dla gracza " + player.getName());
            
        } else if (!shouldHaveSpeed && currentlyHasSpeed) {
            // Usuń efekt Speed
            player.removePotionEffect(PotionEffectType.SPEED);
            playersWithSpeedBoost.put(player.getUniqueId(), false);
            
            plugin.getLogger().fine("Usunięto efekt Speed dla gracza " + player.getName());
        }
    }
    
    /**
     * Aktualizuje efekt Haste dla gracza
     * @param player gracz
     */
    private void updateHasteEffect(Player player) {
        boolean shouldHaveHaste = hasUtilityToolInHand(player);
        boolean currentlyHasHaste = playersWithHasteBoost.getOrDefault(player.getUniqueId(), false);
        
        if (shouldHaveHaste && !currentlyHasHaste) {
            // Dodaj efekt Haste
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                Integer.MAX_VALUE, // Nieskończony czas
                0, // Haste I
                false, // ambient
                false, // particles
                false  // icon
            ));
            playersWithHasteBoost.put(player.getUniqueId(), true);
            
            plugin.getLogger().fine("Dodano efekt Haste dla gracza " + player.getName());
            
        } else if (!shouldHaveHaste && currentlyHasHaste) {
            // Usuń efekt Haste
            player.removePotionEffect(PotionEffectType.HASTE);
            playersWithHasteBoost.put(player.getUniqueId(), false);
            
            plugin.getLogger().fine("Usunięto efekt Haste dla gracza " + player.getName());
        }
    }
    
    /**
     * Sprawdza czy gracz ma pełny set zbroi z ścieżką UTILITY
     * @param player gracz
     * @return true jeśli ma pełny set
     */
    private boolean hasFullUtilityArmorSet(Player player) {
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        
        // Sprawdź wszystkie 4 części zbroi
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || !ItemNBT.isArmor(armorPiece)) {
                return false; // Brak części zbroi
            }
            
            EnhancementData enhancement = enhancementService.getEnhancementData(armorPiece);
            if (enhancement.isEmpty() || enhancement.getPath() != PathType.UTILITY || enhancement.getLevel() < 1) {
                return false; // Brak wzmocnienia UTILITY lub poziom 0
            }
        }
        
        return true; // Wszystkie 4 części mają UTILITY poziom 1+
    }
    
    /**
     * Sprawdza czy gracz trzyma narzędzie z ścieżką UTILITY
     * @param player gracz
     * @return true jeśli trzyma narzędzie UTILITY
     */
    private boolean hasUtilityToolInHand(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem == null || !ItemNBT.isTool(heldItem)) {
            return false;
        }
        
        EnhancementData enhancement = enhancementService.getEnhancementData(heldItem);
        return !enhancement.isEmpty() && 
               enhancement.getPath() == PathType.UTILITY && 
               enhancement.getLevel() >= 1;
    }
    
    /**
     * Czyści dane gracza przy wylogowaniu
     * @param playerUUID UUID gracza
     */
    public void cleanupPlayer(UUID playerUUID) {
        playersWithSpeedBoost.remove(playerUUID);
        playersWithHasteBoost.remove(playerUUID);
    }
}
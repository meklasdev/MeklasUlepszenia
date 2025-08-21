package pl.meklas.enhance.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.meklas.enhance.EnhancePlugin;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.service.EnhancementService;
import pl.meklas.enhance.util.ItemNBT;
import pl.meklas.enhance.util.RandomProvider;

/**
 * Listener obsługujący efekty ścieżek DEFENSE i OFFENSE
 */
public class DamageListener implements Listener {
    private final EnhancePlugin plugin;
    private final EnhancementService enhancementService;
    private final RandomProvider randomProvider;
    
    public DamageListener(EnhancePlugin plugin) {
        this.plugin = plugin;
        this.enhancementService = plugin.getEnhancementService();
        this.randomProvider = plugin.getRandomProvider();
    }
    
    /**
     * Obsługuje redukcję obrażeń dla ścieżki DEFENSE
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        double totalDefensePercent = calculateTotalDefense(player);
        
        if (totalDefensePercent > 0) {
            double maxDefense = plugin.getConfigManager().getMaxDefensePercent();
            totalDefensePercent = Math.min(totalDefensePercent, maxDefense);
            
            double originalDamage = event.getDamage();
            double reduction = originalDamage * (totalDefensePercent / 100.0);
            double newDamage = originalDamage - reduction;
            
            event.setDamage(Math.max(0, newDamage));
            
            // Debug log
            plugin.getLogger().fine(String.format(
                "Gracz %s otrzymał %f obrażeń (oryginalne: %f, redukcja: %f%%, zmniejszenie: %f)",
                player.getName(), newDamage, originalDamage, totalDefensePercent, reduction
            ));
        }
    }
    
    /**
     * Obsługuje efekty ataku dla ścieżki OFFENSE
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon == null || !ItemNBT.isTool(weapon)) {
            return;
        }
        
        EnhancementData enhancement = enhancementService.getEnhancementData(weapon);
        if (enhancement.isEmpty() || enhancement.getPath() != PathType.OFFENSE) {
            return;
        }
        
        // Sprawdź czy nastąpił proc
        double procChance = enhancement.getPath().getOffenseChance(enhancement.getLevel());
        if (!randomProvider.rollSuccess(procChance)) {
            return;
        }
        
        // Aplikuj efekt Siły
        int strengthLevel = enhancement.getPath().getOffenseStrengthLevel(enhancement.getLevel());
        int duration = enhancement.getPath().getOffenseDuration(enhancement.getLevel()) * 20; // Konwersja na ticki
        
        // Usuń istniejący efekt Siły jeśli jest aktywny (odświeżenie)
        attacker.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        
        // Dodaj nowy efekt
        PotionEffect strengthEffect = new PotionEffect(
            PotionEffectType.INCREASE_DAMAGE,
            duration,
            strengthLevel,
            false, // ambient
            false, // particles
            false  // icon
        );
        
        attacker.addPotionEffect(strengthEffect);
        
        // Debug log
        plugin.getLogger().fine(String.format(
            "Gracz %s aktywował efekt Siły %s na %d sekund (szansa: %f%%)",
            attacker.getName(),
            strengthLevel == 0 ? "I" : "II",
            enhancement.getPath().getOffenseDuration(enhancement.getLevel()),
            procChance
        ));
        
        // Opcjonalnie: efekt dźwiękowy lub wizualny
        attacker.playSound(attacker.getLocation(), plugin.getConfigManager().getSuccessSound(), 0.5f, 1.5f);
    }
    
    /**
     * Oblicza łączną redukcję obrażeń ze wszystkich części zbroi
     * @param player gracz
     * @return procent redukcji (0-100)
     */
    private double calculateTotalDefense(Player player) {
        double totalDefense = 0.0;
        
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || !ItemNBT.isArmor(armorPiece)) {
                continue;
            }
            
            EnhancementData enhancement = enhancementService.getEnhancementData(armorPiece);
            if (enhancement.isEmpty() || enhancement.getPath() != PathType.DEFENSE) {
                continue;
            }
            
            double defensePercent = enhancement.getPath().getDefensePercent(enhancement.getLevel());
            totalDefense += defensePercent;
        }
        
        return totalDefense;
    }
}
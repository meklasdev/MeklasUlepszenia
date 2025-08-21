package pl.meklas.enhance.service;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.meklas.enhance.config.Messages;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;

import java.util.ArrayList;
import java.util.List;

/**
 * Serwis do zarządzania lore przedmiotów
 */
public class LoreService {
    private final Messages messages;
    private static final String ENHANCEMENT_MARKER = "§k§r"; // Niewidoczny marker
    
    public LoreService(Messages messages) {
        this.messages = messages;
    }
    
    /**
     * Aktualizuje lore przedmiotu z danymi wzmocnienia
     * @param item przedmiot do zaktualizowania
     * @param data dane wzmocnienia
     */
    public void updateItemLore(ItemStack item, EnhancementData data) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        
        // Usuń stare linie wzmocnienia
        removeEnhancementLore(lore);
        
        // Dodaj nowe linie wzmocnienia jeśli nie jest puste
        if (!data.isEmpty()) {
            addEnhancementLore(lore, data);
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    /**
     * Usuwa linie wzmocnienia z lore
     * @param lore lista lore do modyfikacji
     */
    private void removeEnhancementLore(List<String> lore) {
        lore.removeIf(line -> line.contains(ENHANCEMENT_MARKER));
    }
    
    /**
     * Dodaje linie wzmocnienia do lore
     * @param lore lista lore do modyfikacji
     * @param data dane wzmocnienia
     */
    private void addEnhancementLore(List<String> lore, EnhancementData data) {
        if (data.isEmpty()) {
            return;
        }
        
        // Dodaj pustą linię jeśli lore nie jest puste
        if (!lore.isEmpty()) {
            lore.add("");
        }
        
        // Linia 1: Ścieżka i poziom
        String pathLine = String.format("§7Ścieżka: %s §7| §e%d%s",
                getPathColor(data.getPath()) + data.getPath().getDisplayName(),
                data.getLevel(),
                ENHANCEMENT_MARKER);
        lore.add(pathLine);
        
        // Linia 2: Efekt
        String effectLine = String.format("§7%s%s",
                data.getEffectDescription(),
                ENHANCEMENT_MARKER);
        lore.add(effectLine);
        
        // Linia 3: Następny poziom lub maksymalny poziom
        if (data.isMaxLevel()) {
            String maxLine = "§7§oMaksymalny poziom osiągnięty" + ENHANCEMENT_MARKER;
            lore.add(maxLine);
        } else {
            // Tu będzie trzeba dodać logikę pobierania szansy z konfiguracji
            String nextLine = "§7Następny poziom: §e??% §7szansy" + ENHANCEMENT_MARKER;
            lore.add(nextLine);
        }
    }
    
    /**
     * Aktualizuje lore z informacją o szansie następnego poziomu
     * @param item przedmiot
     * @param data dane wzmocnienia
     * @param nextLevelChance szansa następnego poziomu
     */
    public void updateItemLoreWithChance(ItemStack item, EnhancementData data, int nextLevelChance) {
        if (item == null || item.getItemMeta() == null || data.isEmpty()) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        
        // Znajdź i zaktualizuj linię z szansą
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.contains(ENHANCEMENT_MARKER) && line.contains("Następny poziom")) {
                if (data.isMaxLevel()) {
                    lore.set(i, "§7§oMaksymalny poziom osiągnięty" + ENHANCEMENT_MARKER);
                } else {
                    lore.set(i, String.format("§7Następny poziom: §e%d%% §7szansy%s", 
                            nextLevelChance, ENHANCEMENT_MARKER));
                }
                break;
            }
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    /**
     * Pobiera kolor dla ścieżki
     * @param path ścieżka
     * @return kod koloru
     */
    private String getPathColor(PathType path) {
        return switch (path) {
            case DEFENSE -> "§c";
            case OFFENSE -> "§4";
            case UTILITY -> "§b";
        };
    }
    
    /**
     * Tworzy lore dla podglądu w GUI
     * @param data dane wzmocnienia
     * @param nextLevelChance szansa następnego poziomu
     * @return lista linii lore
     */
    public List<String> createPreviewLore(EnhancementData data, int nextLevelChance) {
        List<String> lore = new ArrayList<>();
        
        if (data.isEmpty()) {
            lore.add("§7Wybierz ścieżkę wzmocnienia");
            return lore;
        }
        
        lore.add("§7Podgląd po ulepszeniu:");
        lore.add("");
        lore.add(String.format("§7Ścieżka: %s%s §7| §e%d",
                getPathColor(data.getPath()),
                data.getPath().getDisplayName(),
                data.getLevel()));
        lore.add("§7" + data.getEffectDescription());
        
        if (data.isMaxLevel()) {
            lore.add("§7§oMaksymalny poziom");
        } else {
            lore.add(String.format("§7Szansa powodzenia: §e%d%%", nextLevelChance));
        }
        
        return lore;
    }
    
    /**
     * Tworzy lore dla przycisku ścieżki w GUI
     * @param path ścieżka
     * @param currentData obecne dane przedmiotu
     * @param available czy ścieżka jest dostępna
     * @return lista linii lore
     */
    public List<String> createPathButtonLore(PathType path, EnhancementData currentData, boolean available) {
        List<String> lore = new ArrayList<>();
        
        lore.add(getPathColor(path) + "§l" + path.getDisplayName().toUpperCase());
        lore.add("§7" + path.getDescription());
        lore.add("");
        
        // Dodaj opis poziomów
        switch (path) {
            case DEFENSE -> {
                lore.add("§7• Poziom 1: §c+2% redukcji obrażeń");
                lore.add("§7• Poziom 2: §c+4% redukcji obrażeń");
                lore.add("§7• Poziom 3: §c+6% redukcji obrażeń");
            }
            case OFFENSE -> {
                lore.add("§7• Poziom 1: §46% szansy na Siłę I (3s)");
                lore.add("§7• Poziom 2: §48% szansy na Siłę II (3s)");
                lore.add("§7• Poziom 3: §410% szansy na Siłę II (5s)");
            }
            case UTILITY -> {
                lore.add("§7• Na zbroi: §bSpeed I §7przy pełnym secie");
                lore.add("§7• Na narzędziu: §bHaste I §7podczas trzymania");
            }
        }
        
        lore.add("");
        
        // Status dostępności
        if (!available) {
            if (currentData.isEmpty()) {
                lore.add("§7Kliknij aby wybrać tę ścieżkę");
            } else if (currentData.getPath() == path) {
                if (currentData.isMaxLevel()) {
                    lore.add("§c§lMAKSYMALNY POZIOM");
                } else {
                    lore.add("§a§lDOSTĘPNE ULEPSZENIE");
                }
            } else {
                lore.add("§c§lŚCIEŻKA ZABLOKOWANA");
                lore.add("§7Przedmiot ma inną ścieżkę");
            }
        }
        
        return lore;
    }
}
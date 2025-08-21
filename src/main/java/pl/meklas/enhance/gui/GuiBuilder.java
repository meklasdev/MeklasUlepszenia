package pl.meklas.enhance.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.config.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder do tworzenia GUI
 */
public class GuiBuilder {
    private final ConfigManager configManager;
    private final Messages messages;
    
    public GuiBuilder(ConfigManager configManager, Messages messages) {
        this.configManager = configManager;
        this.messages = messages;
    }
    
    /**
     * Tworzy podstawowe GUI z tytułem i rozmiarem
     * @param title tytuł GUI
     * @param size rozmiar GUI
     * @return nowe GUI
     */
    public Inventory createGui(String title, int size) {
        String coloredTitle = ChatColor.translateAlternateColorCodes('&', title);
        return Bukkit.createInventory(null, size, coloredTitle);
    }
    
    /**
     * Tworzy ItemStack z nazwą i lore
     * @param material materiał
     * @param name nazwa (może zawierać kody kolorów)
     * @param lore lore (może zawierać kody kolorów)
     * @return nowy ItemStack
     */
    public ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            
            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(coloredLore);
            }
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Tworzy ItemStack z pojedynczą linią lore
     * @param material materiał
     * @param name nazwa
     * @param loreLine pojedyncza linia lore
     * @return nowy ItemStack
     */
    public ItemStack createItem(Material material, String name, String loreLine) {
        List<String> lore = loreLine != null ? List.of(loreLine) : null;
        return createItem(material, name, lore);
    }
    
    /**
     * Tworzy ItemStack bez lore
     * @param material materiał
     * @param name nazwa
     * @return nowy ItemStack
     */
    public ItemStack createItem(Material material, String name) {
        return createItem(material, name, (List<String>) null);
    }
    
    /**
     * Tworzy szkło dekoracyjne
     * @param color kolor szkła (może być null dla szarego)
     * @return ItemStack szkła
     */
    public ItemStack createGlass(Material glassColor) {
        if (glassColor == null) {
            glassColor = Material.GRAY_STAINED_GLASS_PANE;
        }
        return createItem(glassColor, " ");
    }
    
    /**
     * Wypełnia sloty GUI szkłem dekoracyjnym
     * @param gui GUI do wypełnienia
     * @param slots sloty do wypełnienia
     * @param glassColor kolor szkła
     */
    public void fillWithGlass(Inventory gui, List<Integer> slots, Material glassColor) {
        ItemStack glass = createGlass(glassColor);
        for (int slot : slots) {
            if (slot >= 0 && slot < gui.getSize()) {
                gui.setItem(slot, glass);
            }
        }
    }
    
    /**
     * Wypełnia wszystkie puste sloty szkłem
     * @param gui GUI do wypełnienia
     * @param glassColor kolor szkła
     */
    public void fillEmptyWithGlass(Inventory gui, Material glassColor) {
        ItemStack glass = createGlass(glassColor);
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, glass);
            }
        }
    }
    
    /**
     * Tworzy przycisk potwierdzenia
     * @param confirmText tekst potwierdzenia
     * @return ItemStack przycisku
     */
    public ItemStack createConfirmButton(String confirmText) {
        return createItem(Material.EMERALD, 
                         "&a&lPOTWIERDŹ", 
                         List.of("&7Kliknij aby " + confirmText));
    }
    
    /**
     * Tworzy przycisk anulowania
     * @param cancelText tekst anulowania
     * @return ItemStack przycisku
     */
    public ItemStack createCancelButton(String cancelText) {
        return createItem(Material.REDSTONE, 
                         "&c&lANULUJ", 
                         List.of("&7Kliknij aby " + cancelText));
    }
    
    /**
     * Tworzy placeholder item
     * @param text tekst do wyświetlenia
     * @return ItemStack placeholder
     */
    public ItemStack createPlaceholder(String text) {
        return createItem(Material.BARRIER, "&c" + text);
    }
    
    /**
     * Klonuje ItemStack z nową nazwą
     * @param original oryginalny item
     * @param newName nowa nazwa
     * @return sklonowany item
     */
    public ItemStack cloneWithName(ItemStack original, String newName) {
        if (original == null) return null;
        
        ItemStack clone = original.clone();
        ItemMeta meta = clone.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', newName));
            clone.setItemMeta(meta);
        }
        
        return clone;
    }
    
    /**
     * Klonuje ItemStack z nowym lore
     * @param original oryginalny item
     * @param newLore nowe lore
     * @return sklonowany item
     */
    public ItemStack cloneWithLore(ItemStack original, List<String> newLore) {
        if (original == null) return null;
        
        ItemStack clone = original.clone();
        ItemMeta meta = clone.getItemMeta();
        
        if (meta != null) {
            if (newLore != null) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : newLore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(coloredLore);
            } else {
                meta.setLore(null);
            }
            clone.setItemMeta(meta);
        }
        
        return clone;
    }
    
    /**
     * Sprawdza czy ItemStack jest szkłem dekoracyjnym
     * @param item item do sprawdzenia
     * @return true jeśli jest szkłem
     */
    public boolean isGlass(ItemStack item) {
        if (item == null) return false;
        
        String materialName = item.getType().name();
        return materialName.contains("GLASS_PANE") || materialName.equals("GLASS");
    }
    
    /**
     * Pobiera materiał z konfiguracji lub zwraca domyślny
     * @param configPath ścieżka w konfiguracji
     * @param defaultMaterial domyślny materiał
     * @return materiał
     */
    public Material getMaterialFromConfig(String configPath, Material defaultMaterial) {
        String materialName = configManager.getGuiConfig().getString(configPath);
        if (materialName == null) {
            return defaultMaterial;
        }
        
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultMaterial;
        }
    }
    
    /**
     * Pobiera listę slotów z konfiguracji
     * @param configPath ścieżka w konfiguracji
     * @return lista slotów
     */
    public List<Integer> getSlotsFromConfig(String configPath) {
        return configManager.getGuiConfig().getIntegerList(configPath);
    }
}
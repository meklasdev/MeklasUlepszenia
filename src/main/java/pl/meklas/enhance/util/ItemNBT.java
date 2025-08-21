package pl.meklas.enhance.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;

/**
 * Utility do zarządzania NBT przedmiotów
 */
public class ItemNBT {
    private static final String NAMESPACE = "enhance";
    private static final String PATH_KEY = "path";
    private static final String LEVEL_KEY = "level";
    private static final String VERSION_KEY = "version";
    
    /**
     * Zapisuje dane wzmocnienia do przedmiotu
     * @param item przedmiot
     * @param data dane wzmocnienia
     * @param pluginKey klucz pluginu
     */
    public static void saveEnhancementData(ItemStack item, EnhancementData data, NamespacedKey pluginKey) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        NamespacedKey pathKey = new NamespacedKey(pluginKey.getNamespace(), PATH_KEY);
        NamespacedKey levelKey = new NamespacedKey(pluginKey.getNamespace(), LEVEL_KEY);
        NamespacedKey versionKey = new NamespacedKey(pluginKey.getNamespace(), VERSION_KEY);
        
        if (data.isEmpty()) {
            // Usuń dane wzmocnienia
            container.remove(pathKey);
            container.remove(levelKey);
            container.remove(versionKey);
        } else {
            // Zapisz dane wzmocnienia
            container.set(pathKey, PersistentDataType.STRING, data.getPath().name());
            container.set(levelKey, PersistentDataType.INTEGER, data.getLevel());
            container.set(versionKey, PersistentDataType.STRING, data.getVersion());
        }
        
        item.setItemMeta(meta);
    }
    
    /**
     * Odczytuje dane wzmocnienia z przedmiotu
     * @param item przedmiot
     * @param pluginKey klucz pluginu
     * @return dane wzmocnienia lub EnhancementData.empty() jeśli brak
     */
    public static EnhancementData loadEnhancementData(ItemStack item, NamespacedKey pluginKey) {
        if (item == null || item.getItemMeta() == null) {
            return EnhancementData.empty();
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        NamespacedKey pathKey = new NamespacedKey(pluginKey.getNamespace(), PATH_KEY);
        NamespacedKey levelKey = new NamespacedKey(pluginKey.getNamespace(), LEVEL_KEY);
        NamespacedKey versionKey = new NamespacedKey(pluginKey.getNamespace(), VERSION_KEY);
        
        if (!container.has(pathKey, PersistentDataType.STRING)) {
            return EnhancementData.empty();
        }
        
        try {
            String pathName = container.get(pathKey, PersistentDataType.STRING);
            Integer level = container.get(levelKey, PersistentDataType.INTEGER);
            String version = container.get(versionKey, PersistentDataType.STRING);
            
            if (pathName == null || level == null) {
                return EnhancementData.empty();
            }
            
            PathType path = PathType.valueOf(pathName);
            return new EnhancementData(path, level, version != null ? version : "1.0.0");
            
        } catch (IllegalArgumentException e) {
            // Nieprawidłowe dane - zwróć pusty
            return EnhancementData.empty();
        }
    }
    
    /**
     * Sprawdza czy przedmiot ma dane wzmocnienia
     * @param item przedmiot
     * @param pluginKey klucz pluginu
     * @return true jeśli ma wzmocnienie
     */
    public static boolean hasEnhancement(ItemStack item, NamespacedKey pluginKey) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey pathKey = new NamespacedKey(pluginKey.getNamespace(), PATH_KEY);
        
        return container.has(pathKey, PersistentDataType.STRING);
    }
    
    /**
     * Usuwa wszystkie dane wzmocnienia z przedmiotu
     * @param item przedmiot
     * @param pluginKey klucz pluginu
     */
    public static void removeEnhancement(ItemStack item, NamespacedKey pluginKey) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        NamespacedKey pathKey = new NamespacedKey(pluginKey.getNamespace(), PATH_KEY);
        NamespacedKey levelKey = new NamespacedKey(pluginKey.getNamespace(), LEVEL_KEY);
        NamespacedKey versionKey = new NamespacedKey(pluginKey.getNamespace(), VERSION_KEY);
        
        container.remove(pathKey);
        container.remove(levelKey);
        container.remove(versionKey);
        
        item.setItemMeta(meta);
    }
    
    /**
     * Sprawdza czy przedmiot może być wzmacniany
     * @param item przedmiot
     * @return true jeśli może być wzmacniany
     */
    public static boolean isEnhanceable(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        String materialName = item.getType().name();
        
        // Sprawdź zbroje
        if (materialName.contains("HELMET") || materialName.contains("CHESTPLATE") || 
            materialName.contains("LEGGINGS") || materialName.contains("BOOTS")) {
            return materialName.contains("LEATHER") || materialName.contains("IRON") || 
                   materialName.contains("DIAMOND") || materialName.contains("NETHERITE");
        }
        
        // Sprawdź narzędzia
        if (materialName.contains("SWORD") || materialName.contains("AXE") || 
            materialName.contains("PICKAXE") || materialName.contains("SHOVEL") || 
            materialName.contains("HOE")) {
            return materialName.contains("IRON") || materialName.contains("DIAMOND") || 
                   materialName.contains("NETHERITE");
        }
        
        return false;
    }
    
    /**
     * Sprawdza czy przedmiot jest zbroją
     * @param item przedmiot
     * @return true jeśli jest zbroją
     */
    public static boolean isArmor(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        String materialName = item.getType().name();
        return materialName.contains("HELMET") || materialName.contains("CHESTPLATE") || 
               materialName.contains("LEGGINGS") || materialName.contains("BOOTS");
    }
    
    /**
     * Sprawdza czy przedmiot jest narzędziem
     * @param item przedmiot
     * @return true jeśli jest narzędziem
     */
    public static boolean isTool(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        String materialName = item.getType().name();
        return materialName.contains("SWORD") || materialName.contains("AXE") || 
               materialName.contains("PICKAXE") || materialName.contains("SHOVEL") || 
               materialName.contains("HOE");
    }
}
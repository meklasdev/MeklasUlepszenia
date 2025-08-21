package pl.meklas.enhance.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.meklas.enhance.EnhancePlugin;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.config.Messages;

import java.util.List;

/**
 * GUI informacyjne o systemie wzmocnień
 */
public class InfoGui implements Listener {
    private final EnhancePlugin plugin;
    private final ConfigManager configManager;
    private final Messages messages;
    private final GuiBuilder guiBuilder;
    
    private static final String GUI_TITLE = "§8[§aWzmocnienia§8]";
    
    public InfoGui(EnhancePlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messages = plugin.getMessages();
        this.guiBuilder = new GuiBuilder(configManager, messages);
        
        // Rejestruj listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Otwiera GUI informacyjne dla gracza
     * @param player gracz
     */
    public void openGui(Player player) {
        Inventory gui = createInfoGui();
        player.openInventory(gui);
    }
    
    /**
     * Tworzy GUI informacyjne
     * @return GUI
     */
    private Inventory createInfoGui() {
        String title = configManager.getGuiConfig().getString("info.title", GUI_TITLE);
        int size = configManager.getGuiConfig().getInt("info.size", 27);
        
        Inventory gui = guiBuilder.createGui(title, size);
        
        // Ikona systemu wzmocnień
        int systemSlot = configManager.getGuiConfig().getInt("info.items.system.slot", 11);
        Material systemMaterial = guiBuilder.getMaterialFromConfig("info.items.system.material", Material.ENCHANTED_BOOK);
        
        List<String> systemLore = List.of(
            "&7Wzmacniaj swoje zbroje i narzędzia",
            "&7wybierając jedną z trzech ścieżek:",
            "&e• &cObrona &7- redukcja obrażeń",
            "&e• &4Atak &7- efekty bojowe", 
            "&e• &bUtility &7- efekty użytkowe",
            "",
            "&6Kliknij aby otworzyć ulepszanie"
        );
        
        ItemStack systemItem = guiBuilder.createItem(systemMaterial, "&a&lSystem Wzmocnień", systemLore);
        gui.setItem(systemSlot, systemItem);
        
        // Ikona ścieżek
        int pathsSlot = configManager.getGuiConfig().getInt("info.items.paths.slot", 13);
        Material pathsMaterial = guiBuilder.getMaterialFromConfig("info.items.paths.material", Material.DIAMOND_SWORD);
        
        List<String> pathsLore = List.of(
            "&c&lOBRONA:",
            "&7• Poziom 1: &c+2% redukcji obrażeń",
            "&7• Poziom 2: &c+4% redukcji obrażeń", 
            "&7• Poziom 3: &c+6% redukcji obrażeń",
            "",
            "&4&lATAK:",
            "&7• Poziom 1: &46% szansy na Siłę I (3s)",
            "&7• Poziom 2: &48% szansy na Siłę II (3s)",
            "&7• Poziom 3: &410% szansy na Siłę II (5s)",
            "",
            "&b&lUTILITY:",
            "&7• Na zbroi: &bSpeed I &7przy pełnym secie",
            "&7• Na narzędziu: &bHaste I &7podczas trzymania"
        );
        
        ItemStack pathsItem = guiBuilder.createItem(pathsMaterial, "&e&lŚcieżki Wzmocnień", pathsLore);
        gui.setItem(pathsSlot, pathsItem);
        
        // Ikona materiałów
        int materialsSlot = configManager.getGuiConfig().getInt("info.items.materials.slot", 15);
        Material materialsMaterial = guiBuilder.getMaterialFromConfig("info.items.materials.material", Material.CHEST);
        
        List<String> materialsLore = List.of(
            "&7Materiały można zdobyć:",
            "&e• Kopiąc w jaskiniach",
            "&e• Handlując z mieszkańcami", 
            "&e• Walcząc z mobami",
            "&e• Eksplorując struktury",
            "",
            "&7Szczegóły w konfiguracji serwera"
        );
        
        ItemStack materialsItem = guiBuilder.createItem(materialsMaterial, "&6&lMateriały", materialsLore);
        gui.setItem(materialsSlot, materialsItem);
        
        // Wypełnij dekoracyjnym szkłem
        List<Integer> decorationSlots = guiBuilder.getSlotsFromConfig("info.items.decoration.slots");
        Material decorationMaterial = guiBuilder.getMaterialFromConfig("info.items.decoration.material", Material.GRAY_STAINED_GLASS_PANE);
        
        if (!decorationSlots.isEmpty()) {
            guiBuilder.fillWithGlass(gui, decorationSlots, decorationMaterial);
        } else {
            // Domyślne sloty dekoracyjne dla rozmiaru 27
            List<Integer> defaultSlots = List.of(0,1,2,3,4,5,6,7,8,9,10,12,14,16,17,18,19,20,21,22,23,24,25,26);
            guiBuilder.fillWithGlass(gui, defaultSlots, decorationMaterial);
        }
        
        return gui;
    }
    
    /**
     * Obsługuje kliknięcia w GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!isInfoGui(event.getView().getTitle())) {
            return;
        }
        
        event.setCancelled(true); // Blokuj przenoszenie przedmiotów
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || guiBuilder.isGlass(clickedItem)) {
            return;
        }
        
        int slot = event.getSlot();
        
        // System wzmocnień - otwórz GUI ulepszania
        int systemSlot = configManager.getGuiConfig().getInt("info.items.system.slot", 11);
        if (slot == systemSlot) {
            player.closeInventory();
            
            // Otwórz GUI ulepszania
            UpgradeGui upgradeGui = new UpgradeGui(plugin);
            upgradeGui.openGui(player);
        }
        
        // Inne ikony są tylko informacyjne
    }
    
    /**
     * Sprawdza czy GUI to GUI informacyjne
     * @param title tytuł GUI
     * @return true jeśli to GUI informacyjne
     */
    private boolean isInfoGui(String title) {
        String configTitle = configManager.getGuiConfig().getString("info.title", GUI_TITLE);
        String coloredTitle = messages.get("gui.info.title");
        
        return title.equals(configTitle) || title.equals(coloredTitle) || title.contains("Wzmocnienia");
    }
}
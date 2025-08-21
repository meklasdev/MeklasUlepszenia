package pl.meklas.enhance.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.meklas.enhance.EnhancePlugin;
import pl.meklas.enhance.config.ConfigManager;
import pl.meklas.enhance.config.Messages;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.service.EnhancementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * GUI do ulepszania przedmiotów
 */
public class UpgradeGui implements Listener {
    private final EnhancePlugin plugin;
    private final ConfigManager configManager;
    private final Messages messages;
    private final GuiBuilder guiBuilder;
    private final EnhancementService enhancementService;
    
    // Mapa otwartych GUI dla graczy
    private final Map<UUID, Inventory> openGuis = new HashMap<>();
    
    private static final String GUI_TITLE = "§8[§eUlepszanie§8]";
    
    public UpgradeGui(EnhancePlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messages = plugin.getMessages();
        this.guiBuilder = new GuiBuilder(configManager, messages);
        this.enhancementService = plugin.getEnhancementService();
        
        // Rejestruj listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Otwiera GUI ulepszania dla gracza
     * @param player gracz
     */
    public void openGui(Player player) {
        Inventory gui = createUpgradeGui(player);
        openGuis.put(player.getUniqueId(), gui);
        player.openInventory(gui);
    }
    
    /**
     * Tworzy GUI ulepszania
     * @param player gracz
     * @return GUI
     */
    private Inventory createUpgradeGui(Player player) {
        String title = configManager.getGuiConfig().getString("upgrade.title", GUI_TITLE);
        int size = configManager.getGuiConfig().getInt("upgrade.size", 54);
        
        Inventory gui = guiBuilder.createGui(title, size);
        
        // Slot na przedmiot do ulepszenia
        int inputItemSlot = configManager.getGuiConfig().getInt("upgrade.items.inputItem.slot", 20);
        ItemStack inputPlaceholder = guiBuilder.createItem(Material.BARRIER, 
                                                          "&a&lWłóż przedmiot",
                                                          List.of("&7Umieść tutaj przedmiot", "&7który chcesz ulepszyć"));
        gui.setItem(inputItemSlot, inputPlaceholder);
        
        // Slot na materiały
        int inputMaterialSlot = configManager.getGuiConfig().getInt("upgrade.items.inputMaterial.slot", 29);
        ItemStack materialPlaceholder = guiBuilder.createItem(Material.BARRIER,
                                                            "&e&lMateriały", 
                                                            List.of("&7Umieść wymagane materiały", "&7lub zostaw puste"));
        gui.setItem(inputMaterialSlot, materialPlaceholder);
        
        // Przyciski ścieżek
        createPathButtons(gui);
        
        // Slot podglądu
        int previewSlot = configManager.getGuiConfig().getInt("upgrade.items.preview.slot", 22);
        ItemStack previewItem = guiBuilder.createItem(Material.PAPER,
                                                    "&6&lPodgląd",
                                                    List.of("&7Tutaj zobaczysz efekt", "&7po ulepszeniu"));
        gui.setItem(previewSlot, previewItem);
        
        // Przycisk potwierdzenia
        int confirmSlot = configManager.getGuiConfig().getInt("upgrade.items.confirm.slot", 26);
        ItemStack confirmItem = guiBuilder.createItem(Material.EMERALD,
                                                    "&a&lULEPSZ",
                                                    List.of("&7Kliknij aby rozpocząć", "&7proces ulepszania"));
        gui.setItem(confirmSlot, confirmItem);
        
        // Wypełnij dekoracyjnym szkłem
        fillDecorationSlots(gui);
        
        return gui;
    }
    
    /**
     * Tworzy przyciski ścieżek
     * @param gui GUI
     */
    private void createPathButtons(Inventory gui) {
        List<Integer> pathSlots = configManager.getGuiConfig().getIntegerList("upgrade.items.pathButtons");
        if (pathSlots.size() < 3) {
            pathSlots = List.of(24, 33, 42); // Domyślne sloty
        }
        
        PathType[] paths = {PathType.DEFENSE, PathType.OFFENSE, PathType.UTILITY};
        Material[] materials = {Material.SHIELD, Material.DIAMOND_SWORD, Material.REDSTONE};
        
        for (int i = 0; i < 3; i++) {
            PathType path = paths[i];
            Material material = materials[i];
            int slot = pathSlots.get(i);
            
            List<String> lore = createPathButtonLore(path);
            ItemStack pathButton = guiBuilder.createItem(material, 
                                                       getPathColor(path) + "&l" + path.getDisplayName().toUpperCase(),
                                                       lore);
            gui.setItem(slot, pathButton);
        }
    }
    
    /**
     * Tworzy lore dla przycisku ścieżki
     * @param path ścieżka
     * @return lore
     */
    private List<String> createPathButtonLore(PathType path) {
        return switch (path) {
            case DEFENSE -> List.of(
                "&7Redukuje otrzymywane obrażenia",
                "&7Poziom 1: &c+2%",
                "&7Poziom 2: &c+4%", 
                "&7Poziom 3: &c+6%"
            );
            case OFFENSE -> List.of(
                "&7Szansa na efekt przy ataku",
                "&7Poziom 1: &46% - Siła I (3s)",
                "&7Poziom 2: &48% - Siła II (3s)",
                "&7Poziom 3: &410% - Siła II (5s)"
            );
            case UTILITY -> List.of(
                "&7Efekty użytkowe",
                "&7Zbroja: Speed I przy pełnym secie",
                "&7Narzędzie: Haste I podczas trzymania"
            );
        };
    }
    
    /**
     * Wypełnia sloty dekoracyjne
     * @param gui GUI
     */
    private void fillDecorationSlots(Inventory gui) {
        List<Integer> decorationSlots = guiBuilder.getSlotsFromConfig("upgrade.items.decoration.slots");
        Material decorationMaterial = guiBuilder.getMaterialFromConfig("upgrade.items.decoration.material", Material.GRAY_STAINED_GLASS_PANE);
        
        if (!decorationSlots.isEmpty()) {
            guiBuilder.fillWithGlass(gui, decorationSlots, decorationMaterial);
        } else {
            // Wypełnij wszystkie puste sloty
            guiBuilder.fillEmptyWithGlass(gui, decorationMaterial);
        }
    }
    
    /**
     * Aktualizuje GUI na podstawie włożonego przedmiotu
     * @param gui GUI
     * @param player gracz
     */
    private void updateGui(Inventory gui, Player player) {
        int inputItemSlot = configManager.getGuiConfig().getInt("upgrade.items.inputItem.slot", 20);
        ItemStack inputItem = gui.getItem(inputItemSlot);
        
        // Aktualizuj przyciski ścieżek
        updatePathButtons(gui, inputItem);
        
        // Aktualizuj podgląd
        updatePreview(gui, inputItem);
    }
    
    /**
     * Aktualizuje przyciski ścieżek na podstawie przedmiotu
     * @param gui GUI
     * @param inputItem przedmiot wejściowy
     */
    private void updatePathButtons(Inventory gui, ItemStack inputItem) {
        List<Integer> pathSlots = configManager.getGuiConfig().getIntegerList("upgrade.items.pathButtons");
        if (pathSlots.size() < 3) {
            pathSlots = List.of(24, 33, 42);
        }
        
        PathType[] paths = {PathType.DEFENSE, PathType.OFFENSE, PathType.UTILITY};
        
        for (int i = 0; i < 3; i++) {
            PathType path = paths[i];
            int slot = pathSlots.get(i);
            
            ItemStack button = createPathButton(path, inputItem);
            gui.setItem(slot, button);
        }
    }
    
    /**
     * Tworzy przycisk ścieżki na podstawie przedmiotu
     * @param path ścieżka
     * @param inputItem przedmiot
     * @return przycisk
     */
    private ItemStack createPathButton(PathType path, ItemStack inputItem) {
        Material buttonMaterial = getPathMaterial(path);
        String name = getPathColor(path) + "&l" + path.getDisplayName().toUpperCase();
        
        List<String> lore = createPathButtonLore(path);
        
        // Dodaj status dostępności
        if (inputItem != null && enhancementService.canBeEnhanced(inputItem)) {
            EnhancementData currentData = enhancementService.getEnhancementData(inputItem);
            
            if (currentData.isEmpty()) {
                lore.add("");
                lore.add("&a&lKLIKNIJ ABY WYBRAĆ");
            } else if (currentData.getPath() == path) {
                if (currentData.isMaxLevel()) {
                    lore.add("");
                    lore.add("&c&lMAKSYMALNY POZIOM");
                } else {
                    lore.add("");
                    lore.add("&a&lDOSTĘPNE ULEPSZENIE");
                    int chance = enhancementService.getSuccessChance(inputItem, path);
                    lore.add("&7Szansa: &e" + chance + "%");
                }
            } else {
                lore.add("");
                lore.add("&c&lŚCIEŻKA ZABLOKOWANA");
                lore.add("&7Przedmiot ma inną ścieżkę");
            }
        } else {
            lore.add("");
            lore.add("&7&oWłóż poprawny przedmiot");
        }
        
        return guiBuilder.createItem(buttonMaterial, name, lore);
    }
    
    /**
     * Aktualizuje podgląd
     * @param gui GUI
     * @param inputItem przedmiot
     */
    private void updatePreview(Inventory gui, ItemStack inputItem) {
        int previewSlot = configManager.getGuiConfig().getInt("upgrade.items.preview.slot", 22);
        
        if (inputItem == null || !enhancementService.canBeEnhanced(inputItem)) {
            ItemStack previewItem = guiBuilder.createItem(Material.PAPER,
                                                        "&6&lPodgląd",
                                                        List.of("&7Włóż przedmiot aby", "&7zobaczyć podgląd"));
            gui.setItem(previewSlot, previewItem);
            return;
        }
        
        EnhancementData currentData = enhancementService.getEnhancementData(inputItem);
        List<String> previewLore;
        
        if (currentData.isEmpty()) {
            previewLore = List.of("&7Wybierz ścieżkę wzmocnienia");
        } else {
            previewLore = List.of(
                "&7Obecne wzmocnienie:",
                "&7Ścieżka: " + getPathColor(currentData.getPath()) + currentData.getPath().getDisplayName(),
                "&7Poziom: &e" + currentData.getLevel(),
                "&7Efekt: &f" + currentData.getEffectDescription()
            );
        }
        
        ItemStack previewItem = guiBuilder.createItem(Material.PAPER, "&6&lPodgląd", previewLore);
        gui.setItem(previewSlot, previewItem);
    }
    
    /**
     * Obsługuje kliknięcia w GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!isUpgradeGui(event.getView().getTitle())) {
            return;
        }
        
        int slot = event.getSlot();
        int inputItemSlot = configManager.getGuiConfig().getInt("upgrade.items.inputItem.slot", 20);
        int inputMaterialSlot = configManager.getGuiConfig().getInt("upgrade.items.inputMaterial.slot", 29);
        
        // Pozwól na wkładanie/wyjmowanie przedmiotów w slotach input
        if (slot == inputItemSlot || slot == inputMaterialSlot) {
            // Aktualizuj GUI po zmianie
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (openGuis.containsKey(player.getUniqueId())) {
                    updateGui(event.getInventory(), player);
                }
            }, 1L);
            return;
        }
        
        event.setCancelled(true); // Blokuj inne operacje
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || guiBuilder.isGlass(clickedItem)) {
            return;
        }
        
        // Obsłuż kliknięcia w przyciski
        handleButtonClick(player, event.getInventory(), slot);
    }
    
    /**
     * Obsługuje kliknięcia w przyciski
     * @param player gracz
     * @param gui GUI
     * @param slot slot
     */
    private void handleButtonClick(Player player, Inventory gui, int slot) {
        List<Integer> pathSlots = configManager.getGuiConfig().getIntegerList("upgrade.items.pathButtons");
        if (pathSlots.size() < 3) {
            pathSlots = List.of(24, 33, 42);
        }
        
        int confirmSlot = configManager.getGuiConfig().getInt("upgrade.items.confirm.slot", 26);
        
        // Kliknięcie w ścieżkę
        if (pathSlots.contains(slot)) {
            int pathIndex = pathSlots.indexOf(slot);
            PathType path = PathType.values()[pathIndex];
            handlePathClick(player, gui, path);
        }
        // Kliknięcie w potwierdzenie
        else if (slot == confirmSlot) {
            handleConfirmClick(player, gui);
        }
    }
    
    /**
     * Obsługuje kliknięcie w ścieżkę
     * @param player gracz
     * @param gui GUI
     * @param path ścieżka
     */
    private void handlePathClick(Player player, Inventory gui, PathType path) {
        int inputItemSlot = configManager.getGuiConfig().getInt("upgrade.items.inputItem.slot", 20);
        ItemStack inputItem = gui.getItem(inputItemSlot);
        
        if (inputItem == null || !enhancementService.canBeEnhanced(inputItem)) {
            messages.sendNoItem(player);
            return;
        }
        
        if (!enhancementService.isPathAvailable(inputItem, path)) {
            messages.sendPathLocked(player);
            return;
        }
        
        if (!enhancementService.canUpgrade(inputItem, path)) {
            messages.sendMaxLevel(player);
            return;
        }
        
        // Sprawdź koszty
        if (!enhancementService.canAffordUpgrade(player, inputItem, path)) {
            messages.sendNoMaterials(player); // Lub bardziej szczegółowa wiadomość
            return;
        }
        
        // Otwórz potwierdzenie
        openConfirmationGui(player, inputItem, path);
    }
    
    /**
     * Obsługuje kliknięcie w potwierdzenie
     * @param player gracz
     * @param gui GUI
     */
    private void handleConfirmClick(Player player, Inventory gui) {
        messages.send(player, "noItem"); // Placeholder - implementacja w przyszłości
    }
    
    /**
     * Otwiera GUI potwierdzenia
     * @param player gracz
     * @param item przedmiot
     * @param path ścieżka
     */
    private void openConfirmationGui(Player player, ItemStack item, PathType path) {
        // Implementacja GUI potwierdzenia - uproszczona wersja
        player.closeInventory();
        
        // Wykonaj ulepszenie bezpośrednio (w pełnej wersji byłoby GUI potwierdzenia)
        EnhancementService.UpgradeResult result = enhancementService.performUpgrade(player, item, path);
        
        switch (result) {
            case SUCCESS -> {
                messages.sendSuccess(player);
                player.playSound(player.getLocation(), configManager.getSuccessSound(), 1.0f, 1.0f);
            }
            case FAILURE_DESTROYED -> {
                messages.sendFail(player);
                player.playSound(player.getLocation(), configManager.getFailSound(), 1.0f, 1.0f);
            }
            case INSUFFICIENT_RESOURCES -> {
                messages.sendNoMaterials(player);
            }
            case INVALID_ITEM -> {
                messages.sendNoItem(player);
            }
        }
    }
    
    /**
     * Obsługuje zamknięcie GUI
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        if (isUpgradeGui(event.getView().getTitle())) {
            openGuis.remove(player.getUniqueId());
        }
    }
    
    /**
     * Sprawdza czy GUI to GUI ulepszania
     * @param title tytuł GUI
     * @return true jeśli to GUI ulepszania
     */
    private boolean isUpgradeGui(String title) {
        return title.contains("Ulepszanie") || title.contains("Upgrade");
    }
    
    /**
     * Pobiera kolor ścieżki
     * @param path ścieżka
     * @return kod koloru
     */
    private String getPathColor(PathType path) {
        return switch (path) {
            case DEFENSE -> "&c";
            case OFFENSE -> "&4";
            case UTILITY -> "&b";
        };
    }
    
    /**
     * Pobiera materiał dla ścieżki
     * @param path ścieżka
     * @return materiał
     */
    private Material getPathMaterial(PathType path) {
        return switch (path) {
            case DEFENSE -> Material.SHIELD;
            case OFFENSE -> Material.DIAMOND_SWORD;
            case UTILITY -> Material.REDSTONE;
        };
    }
}
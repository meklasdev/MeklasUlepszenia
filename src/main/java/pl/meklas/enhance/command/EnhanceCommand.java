package pl.meklas.enhance.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.meklas.enhance.EnhancePlugin;
import pl.meklas.enhance.config.Messages;
import pl.meklas.enhance.gui.InfoGui;
import pl.meklas.enhance.gui.UpgradeGui;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.service.EnhancementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Główna komenda pluginu /enhance
 */
public class EnhanceCommand implements CommandExecutor, TabCompleter {
    private final EnhancePlugin plugin;
    private final Messages messages;
    private final InfoGui infoGui;
    private final UpgradeGui upgradeGui;
    
    public EnhanceCommand(EnhancePlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.infoGui = new InfoGui(plugin);
        this.upgradeGui = new UpgradeGui(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Brak argumentów - otwórz GUI info dla gracza
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (!player.hasPermission("enhance.open")) {
                    player.sendMessage(messages.get("noPermission", "permission", "enhance.open"));
                    return true;
                }
                infoGui.openGui(player);
            } else {
                sender.sendMessage(messages.get("prefix") + "§cTa komenda jest dostępna tylko dla graczy.");
            }
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "open" -> handleOpenCommand(sender, args);
            case "upgrade" -> handleUpgradeCommand(sender, args);
            case "simulate" -> handleSimulateCommand(sender, args);
            case "reload" -> handleReloadCommand(sender);
            case "giveitem", "give" -> handleGiveItemCommand(sender, args);
            case "help" -> handleHelpCommand(sender);
            default -> {
                sender.sendMessage(messages.get("prefix") + "§cNieznana podkomenda. Użyj /enhance help");
                return true;
            }
        }
        
        return true;
    }
    
    /**
     * Obsługuje podkomendę open
     */
    private void handleOpenCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // /enhance open - otwórz dla siebie
            if (sender instanceof Player player) {
                if (!player.hasPermission("enhance.open")) {
                    player.sendMessage(messages.get("noPermission", "permission", "enhance.open"));
                    return;
                }
                infoGui.openGui(player);
            } else {
                sender.sendMessage(messages.get("prefix") + "§cPodaj nazwę gracza.");
            }
        } else if (args.length == 2) {
            // /enhance open <gracz> - otwórz dla innego gracza
            if (!sender.hasPermission("enhance.admin")) {
                sender.sendMessage(messages.get("noPermission", "permission", "enhance.admin"));
                return;
            }
            
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(messages.get("prefix") + "§cGracz nie jest online.");
                return;
            }
            
            infoGui.openGui(target);
            sender.sendMessage(messages.get("prefix") + "§aOtwarto GUI dla gracza " + target.getName());
        }
    }
    
    /**
     * Obsługuje podkomendę upgrade
     */
    private void handleUpgradeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("prefix") + "§cTa komenda jest dostępna tylko dla graczy.");
            return;
        }
        
        if (!player.hasPermission("enhance.upgrade")) {
            player.sendMessage(messages.get("noPermission", "permission", "enhance.upgrade"));
            return;
        }
        
        upgradeGui.openGui(player);
    }
    
    /**
     * Obsługuje podkomendę simulate
     */
    private void handleSimulateCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("enhance.admin.simulate")) {
            sender.sendMessage(messages.get("noPermission", "permission", "enhance.admin.simulate"));
            return;
        }
        
        if (args.length < 3) {
            sender.sendMessage(messages.get("prefix") + "§cUżycie: /enhance simulate <ścieżka> <poziom> [iteracje]");
            sender.sendMessage("§7Ścieżki: DEFENSE, OFFENSE, UTILITY");
            sender.sendMessage("§7Poziomy: 1, 2, 3");
            return;
        }
        
        // Parsuj ścieżkę
        PathType path;
        try {
            path = PathType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(messages.get("prefix") + "§cNieprawidłowa ścieżka. Dostępne: DEFENSE, OFFENSE, UTILITY");
            return;
        }
        
        // Parsuj poziom
        int level;
        try {
            level = Integer.parseInt(args[2]);
            if (level < 1 || level > 3) {
                sender.sendMessage(messages.get("prefix") + "§cPoziom musi być między 1 a 3.");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.get("prefix") + "§cNieprawidłowy poziom.");
            return;
        }
        
        // Parsuj liczbę iteracji (domyślnie 1000)
        int iterations = 1000;
        if (args.length >= 4) {
            try {
                iterations = Integer.parseInt(args[3]);
                if (iterations < 1 || iterations > 10000) {
                    sender.sendMessage(messages.get("prefix") + "§cLiczba iteracji musi być między 1 a 10000.");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.get("prefix") + "§cNieprawidłowa liczba iteracji.");
                return;
            }
        }
        
        // Wykonaj symulację
        sender.sendMessage(messages.get("prefix") + "§7Wykonywanie symulacji...");
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            EnhancementService.SimulationResult result = plugin.getEnhancementService()
                    .simulateUpgrade(path, level, iterations);
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                sender.sendMessage(messages.get("prefix") + "§eWyniki symulacji:");
                sender.sendMessage("§7Ścieżka: §f" + path.getDisplayName() + " §7Poziom: §f" + level);
                sender.sendMessage("§7Iteracje: §f" + iterations);
                sender.sendMessage("§7Oczekiwana szansa: §f" + result.getExpectedChance() + "%");
                sender.sendMessage("§aSuccesses: §f" + result.getSuccesses());
                sender.sendMessage("§cFailures: §f" + result.getFailures());
                sender.sendMessage("§7Rzeczywista szansa: §f" + String.format("%.2f%%", result.getActualSuccessRate()));
                sender.sendMessage("§7Odchylenie: §f" + String.format("%.2f%%", result.getDeviation()));
            });
        });
    }
    
    /**
     * Obsługuje podkomendę reload
     */
    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("enhance.admin.reload")) {
            sender.sendMessage(messages.get("noPermission", "permission", "enhance.admin.reload"));
            return;
        }
        
        try {
            plugin.reloadPlugin();
            sender.sendMessage(messages.get("prefix") + "§aKonfiguracja została przeładowana!");
        } catch (Exception e) {
            sender.sendMessage(messages.get("prefix") + "§cBłąd podczas przeładowywania: " + e.getMessage());
            plugin.getLogger().severe("Błąd podczas przeładowywania konfiguracji: " + e.getMessage());
        }
    }
    
    /**
     * Obsługuje podkomendę giveitem
     */
    private void handleGiveItemCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("enhance.admin.give")) {
            sender.sendMessage(messages.get("noPermission", "permission", "enhance.admin.give"));
            return;
        }
        
        Player target;
        if (args.length >= 2) {
            target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(messages.get("prefix") + "§cGracz nie jest online.");
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(messages.get("prefix") + "§cPodaj nazwę gracza.");
            return;
        }
        
        // Daj przykładowe materiały testowe
        giveTestMaterials(target);
        
        sender.sendMessage(messages.get("prefix") + "§aDano materiały testowe dla gracza " + target.getName());
        if (target != sender) {
            target.sendMessage(messages.get("prefix") + "§aOtrzymałeś materiały testowe!");
        }
    }
    
    /**
     * Obsługuje podkomendę help
     */
    private void handleHelpCommand(CommandSender sender) {
        sender.sendMessage("§8§m----§r §aWzmocnienia Przedmiotów §8§m----");
        sender.sendMessage("§e/enhance §7- Otwiera GUI informacyjne");
        sender.sendMessage("§e/enhance open [gracz] §7- Otwiera GUI dla gracza");
        sender.sendMessage("§e/enhance upgrade §7- Otwiera GUI ulepszania");
        
        if (sender.hasPermission("enhance.admin")) {
            sender.sendMessage("§c§lKomendy administratora:");
            sender.sendMessage("§e/enhance simulate <ścieżka> <poziom> [iteracje] §7- Symuluje ulepszenia");
            sender.sendMessage("§e/enhance reload §7- Przeładowuje konfigurację");
            sender.sendMessage("§e/enhance giveitem [gracz] §7- Daje materiały testowe");
        }
        
        sender.sendMessage("§8§m------------------------");
    }
    
    /**
     * Daje graczowi materiały testowe
     */
    private void giveTestMaterials(Player player) {
        // Materiały dla każdej ścieżki
        ItemStack[] testItems = {
                // DEFENSE
                new ItemStack(Material.DIAMOND, 16),
                new ItemStack(Material.NETHERITE_SCRAP, 8),
                new ItemStack(Material.NETHERITE_INGOT, 2),
                
                // OFFENSE
                new ItemStack(Material.BLAZE_ROD, 32),
                new ItemStack(Material.ECHO_SHARD, 16),
                new ItemStack(Material.NETHER_STAR, 2),
                
                // UTILITY
                new ItemStack(Material.REDSTONE, 64),
                new ItemStack(Material.AMETHYST_SHARD, 32),
                new ItemStack(Material.SHULKER_SHELL, 8),
                
                // Przykładowe przedmioty do wzmacniania
                new ItemStack(Material.DIAMOND_SWORD),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_BOOTS)
        };
        
        for (ItemStack item : testItems) {
            player.getInventory().addItem(item);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Pierwsza podkomenda
            List<String> subCommands = Arrays.asList("open", "upgrade", "help");
            
            if (sender.hasPermission("enhance.admin")) {
                subCommands = Arrays.asList("open", "upgrade", "simulate", "reload", "giveitem", "help");
            }
            
            String input = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if ("open".equals(subCommand) || "giveitem".equals(subCommand) || "give".equals(subCommand)) {
                // Nazwy graczy online
                String input = args[1].toLowerCase();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            } else if ("simulate".equals(subCommand)) {
                // Ścieżki
                String input = args[1].toLowerCase();
                for (PathType path : PathType.values()) {
                    if (path.name().toLowerCase().startsWith(input)) {
                        completions.add(path.name());
                    }
                }
            }
        } else if (args.length == 3 && "simulate".equals(args[0].toLowerCase())) {
            // Poziomy dla simulate
            completions.addAll(Arrays.asList("1", "2", "3"));
        } else if (args.length == 4 && "simulate".equals(args[0].toLowerCase())) {
            // Liczba iteracji dla simulate
            completions.addAll(Arrays.asList("100", "1000", "5000"));
        }
        
        return completions;
    }
}
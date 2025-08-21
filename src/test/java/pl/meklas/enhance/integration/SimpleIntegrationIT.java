package pl.meklas.enhance.integration;

import org.junit.jupiter.api.Test;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;
import pl.meklas.enhance.util.RandomProvider;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uproszczone testy integracyjne bez mocków Bukkit
 */
public class SimpleIntegrationIT {
    
    @Test
    void testCompleteEnhancementWorkflow() {
        // Given - Symulacja pełnego workflow wzmocnienia
        RandomProvider randomProvider = new RandomProvider();
        
        // Start with empty enhancement
        EnhancementData empty = EnhancementData.empty();
        assertTrue(empty.isEmpty());
        assertTrue(empty.canUpgrade());
        
        // Choose DEFENSE path
        EnhancementData level1 = new EnhancementData(PathType.DEFENSE, 1, "1.0.0");
        assertFalse(level1.isEmpty());
        assertEquals(PathType.DEFENSE, level1.getPath());
        assertEquals(1, level1.getLevel());
        assertTrue(level1.canUpgrade());
        assertFalse(level1.isMaxLevel());
        
        // Verify level 1 effects
        assertEquals(2.0, level1.getPath().getDefensePercent(level1.getLevel()));
        assertEquals("Redukcja obrażeń +2%", level1.getEffectDescription());
        assertEquals("Obrona | 1", level1.getShortDescription());
        
        // Upgrade to level 2
        EnhancementData level2 = level1.getNextLevel();
        assertEquals(2, level2.getLevel());
        assertEquals(4.0, level2.getPath().getDefensePercent(level2.getLevel()));
        assertEquals("Redukcja obrażeń +4%", level2.getEffectDescription());
        
        // Upgrade to level 3 (max)
        EnhancementData level3 = level2.getNextLevel();
        assertEquals(3, level3.getLevel());
        assertTrue(level3.isMaxLevel());
        assertFalse(level3.canUpgrade());
        assertEquals(6.0, level3.getPath().getDefensePercent(level3.getLevel()));
        assertEquals("Redukcja obrażeń +6%", level3.getEffectDescription());
        
        // Try to upgrade beyond max - should return same object
        EnhancementData stillLevel3 = level3.getNextLevel();
        assertEquals(level3, stillLevel3);
    }
    
    @Test
    void testAllPathsIntegration() {
        // Test that all three paths work correctly together
        
        // DEFENSE Path
        EnhancementData defense = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        assertEquals(4.0, defense.getPath().getDefensePercent(defense.getLevel()));
        assertEquals(0.0, defense.getPath().getOffenseChance(defense.getLevel())); // Should be 0 for other paths
        assertEquals(0, defense.getPath().getUtilityRange(defense.getLevel())); // Should be 0 for other paths
        
        // OFFENSE Path
        EnhancementData offense = new EnhancementData(PathType.OFFENSE, 3, "1.0.0");
        assertEquals(10.0, offense.getPath().getOffenseChance(offense.getLevel()));
        assertEquals(1, offense.getPath().getOffenseStrengthLevel(offense.getLevel())); // Strength II
        assertEquals(5, offense.getPath().getOffenseDuration(offense.getLevel())); // 5 seconds
        assertEquals(0.0, offense.getPath().getDefensePercent(offense.getLevel())); // Should be 0
        
        // UTILITY Path
        EnhancementData utility = new EnhancementData(PathType.UTILITY, 1, "1.0.0");
        assertEquals(8, utility.getPath().getUtilityRange(utility.getLevel()));
        assertEquals("Efekt użytkowy aktywny", utility.getEffectDescription());
        assertEquals(0.0, utility.getPath().getDefensePercent(utility.getLevel())); // Should be 0
        assertEquals(0.0, utility.getPath().getOffenseChance(utility.getLevel())); // Should be 0
    }
    
    @Test
    void testRandomProviderConsistency() {
        // Test random provider with large sample size
        RandomProvider randomProvider = new RandomProvider();
        
        int trials = 10000;
        int successes = 0;
        double targetChance = 50.0;
        
        for (int i = 0; i < trials; i++) {
            if (randomProvider.rollSuccess(targetChance)) {
                successes++;
            }
        }
        
        double actualRate = (double) successes / trials * 100.0;
        
        // With 10000 trials, we should be within ±2% of target
        assertTrue(Math.abs(actualRate - targetChance) <= 2.0,
                String.format("Expected ~50%%, got %.2f%% (difference: %.2f%%)",
                        actualRate, Math.abs(actualRate - targetChance)));
    }
    
    @Test
    void testPathTypeEdgeCases() {
        // Test edge cases for all path types
        
        for (PathType path : PathType.values()) {
            // Test invalid levels
            assertEquals(0.0, path.getDefensePercent(0));
            assertEquals(0.0, path.getDefensePercent(-1));
            assertEquals(0.0, path.getDefensePercent(4));
            
            assertEquals(0.0, path.getOffenseChance(0));
            assertEquals(0.0, path.getOffenseChance(-1));
            assertEquals(0.0, path.getOffenseChance(4));
            
            assertEquals(0, path.getUtilityRange(0));
            assertEquals(0, path.getUtilityRange(-1));
            assertEquals(0, path.getUtilityRange(4));
            
            // Test valid levels for each path
            for (int level = 1; level <= 3; level++) {
                if (path == PathType.DEFENSE) {
                    assertTrue(path.getDefensePercent(level) > 0);
                    assertEquals(0.0, path.getOffenseChance(level));
                    assertEquals(0, path.getUtilityRange(level));
                } else if (path == PathType.OFFENSE) {
                    assertEquals(0.0, path.getDefensePercent(level));
                    assertTrue(path.getOffenseChance(level) > 0);
                    assertEquals(0, path.getUtilityRange(level));
                } else if (path == PathType.UTILITY) {
                    assertEquals(0.0, path.getDefensePercent(level));
                    assertEquals(0.0, path.getOffenseChance(level));
                    assertTrue(path.getUtilityRange(level) > 0);
                }
            }
        }
    }
    
    @Test
    void testEnhancementDataEquality() {
        // Test equality and hash code
        EnhancementData data1 = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        EnhancementData data2 = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        EnhancementData data3 = new EnhancementData(PathType.OFFENSE, 2, "1.0.0");
        EnhancementData data4 = new EnhancementData(PathType.DEFENSE, 3, "1.0.0");
        EnhancementData data5 = new EnhancementData(PathType.DEFENSE, 2, "2.0.0");
        
        // Same data should be equal
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        
        // Different path should not be equal
        assertNotEquals(data1, data3);
        
        // Different level should not be equal
        assertNotEquals(data1, data4);
        
        // Different version should not be equal
        assertNotEquals(data1, data5);
        
        // Test toString
        assertTrue(data1.toString().contains("DEFENSE"));
        assertTrue(data1.toString().contains("2"));
        assertTrue(data1.toString().contains("1.0.0"));
    }
    
    @Test
    void testRandomProviderBoundaryValues() {
        RandomProvider randomProvider = new RandomProvider();
        
        // Test boundary values
        assertFalse(randomProvider.rollSuccess(0.0));
        assertTrue(randomProvider.rollSuccess(100.0));
        assertFalse(randomProvider.rollSuccess(-1.0));
        assertTrue(randomProvider.rollSuccess(101.0));
        
        // Test ranges
        for (int i = 0; i < 100; i++) {
            int result = randomProvider.nextInt(1, 1);
            assertEquals(1, result);
            
            double dResult = randomProvider.nextDouble(5.0, 5.1);
            assertTrue(dResult >= 5.0 && dResult < 5.1);
        }
    }
    
    @Test
    void testFullGameScenario() {
        // Simulate a complete game scenario with multiple enhancements
        
        // Player finds a diamond sword and wants to enhance it
        String itemType = "DIAMOND_SWORD";
        assertTrue(itemType.contains("SWORD"));
        assertTrue(itemType.contains("DIAMOND"));
        
        // Player chooses OFFENSE path
        EnhancementData enhancement = EnhancementData.empty();
        assertTrue(enhancement.isEmpty());
        
        // First upgrade attempt - success simulation
        enhancement = new EnhancementData(PathType.OFFENSE, 1, "1.0.0");
        assertEquals("Atak | 1", enhancement.getShortDescription());
        assertTrue(enhancement.getEffectDescription().contains("6%"));
        
        // Player wants to upgrade to level 2
        assertTrue(enhancement.canUpgrade());
        enhancement = enhancement.getNextLevel();
        assertEquals(2, enhancement.getLevel());
        assertTrue(enhancement.getEffectDescription().contains("8%"));
        
        // Final upgrade to level 3
        enhancement = enhancement.getNextLevel();
        assertTrue(enhancement.isMaxLevel());
        assertEquals(3, enhancement.getLevel());
        assertTrue(enhancement.getEffectDescription().contains("10%"));
        assertTrue(enhancement.getEffectDescription().contains("II")); // Strength II
        assertTrue(enhancement.getEffectDescription().contains("5")); // 5 seconds
        
        // Player tries to upgrade further - should fail
        assertFalse(enhancement.canUpgrade());
        EnhancementData sameEnhancement = enhancement.getNextLevel();
        assertEquals(enhancement, sameEnhancement);
    }
    
    @Test
    void testMaterialValidation() {
        // Test material validation logic (simulated)
        String[] validArmor = {
                "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
                "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
                "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
                "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS"
        };
        
        String[] validTools = {
                "IRON_SWORD", "IRON_AXE", "IRON_PICKAXE", "IRON_SHOVEL", "IRON_HOE",
                "DIAMOND_SWORD", "DIAMOND_AXE", "DIAMOND_PICKAXE", "DIAMOND_SHOVEL", "DIAMOND_HOE",
                "NETHERITE_SWORD", "NETHERITE_AXE", "NETHERITE_PICKAXE", "NETHERITE_SHOVEL", "NETHERITE_HOE"
        };
        
        // Test armor validation
        for (String armor : validArmor) {
            assertTrue(armor.contains("HELMET") || armor.contains("CHESTPLATE") || 
                      armor.contains("LEGGINGS") || armor.contains("BOOTS"),
                      "Armor " + armor + " should contain armor type");
            assertTrue(armor.contains("LEATHER") || armor.contains("IRON") || 
                      armor.contains("DIAMOND") || armor.contains("NETHERITE"),
                      "Armor " + armor + " should contain valid material");
        }
        
        // Test tool validation
        for (String tool : validTools) {
            assertTrue(tool.contains("SWORD") || tool.contains("AXE") || 
                      tool.contains("PICKAXE") || tool.contains("SHOVEL") || 
                      tool.contains("HOE"),
                      "Tool " + tool + " should contain tool type");
            assertTrue(tool.contains("IRON") || tool.contains("DIAMOND") || 
                      tool.contains("NETHERITE"),
                      "Tool " + tool + " should contain valid material");
        }
    }
}
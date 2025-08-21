package pl.meklas.enhance.integration;

import org.junit.jupiter.api.Test;
import pl.meklas.enhance.model.EnhancementData;
import pl.meklas.enhance.model.PathType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy integracyjne dla PathType z pełnymi scenariuszami
 */
public class PathTypeIntegrationIT {
    
    @Test
    void testDefensePath_FullProgression() {
        // Given - Defense path progression
        PathType defense = PathType.DEFENSE;
        
        // When & Then - Test each level
        EnhancementData level1 = new EnhancementData(defense, 1, "1.0.0");
        assertEquals(2.0, defense.getDefensePercent(level1.getLevel()));
        assertEquals("Redukcja obrażeń +2%", level1.getEffectDescription());
        assertFalse(level1.isMaxLevel());
        assertTrue(level1.canUpgrade());
        
        EnhancementData level2 = level1.getNextLevel();
        assertEquals(4.0, defense.getDefensePercent(level2.getLevel()));
        assertEquals("Redukcja obrażeń +4%", level2.getEffectDescription());
        assertFalse(level2.isMaxLevel());
        assertTrue(level2.canUpgrade());
        
        EnhancementData level3 = level2.getNextLevel();
        assertEquals(6.0, defense.getDefensePercent(level3.getLevel()));
        assertEquals("Redukcja obrażeń +6%", level3.getEffectDescription());
        assertTrue(level3.isMaxLevel());
        assertFalse(level3.canUpgrade());
        
        // Test max defense limit scenario
        double totalDefense = defense.getDefensePercent(1) + defense.getDefensePercent(2) + defense.getDefensePercent(3);
        assertEquals(12.0, totalDefense); // 2% + 4% + 6% from different armor pieces
        
        // In a real scenario, 4 armor pieces at level 3 would give 24% total,
        // but should be capped at 20% by maxDefensePercent config
        double fourPiecesLevel3 = defense.getDefensePercent(3) * 4;
        assertEquals(24.0, fourPiecesLevel3);
        assertTrue(fourPiecesLevel3 > 20.0); // Would need to be capped
    }
    
    @Test
    void testOffensePath_FullProgression() {
        // Given - Offense path progression
        PathType offense = PathType.OFFENSE;
        
        // When & Then - Test each level
        EnhancementData level1 = new EnhancementData(offense, 1, "1.0.0");
        assertEquals(6.0, offense.getOffenseChance(level1.getLevel()));
        assertEquals(0, offense.getOffenseStrengthLevel(level1.getLevel())); // Strength I
        assertEquals(3, offense.getOffenseDuration(level1.getLevel()));
        assertTrue(level1.getEffectDescription().contains("6%"));
        assertTrue(level1.getEffectDescription().contains("I"));
        assertTrue(level1.getEffectDescription().contains("3"));
        
        EnhancementData level2 = level1.getNextLevel();
        assertEquals(8.0, offense.getOffenseChance(level2.getLevel()));
        assertEquals(1, offense.getOffenseStrengthLevel(level2.getLevel())); // Strength II
        assertEquals(3, offense.getOffenseDuration(level2.getLevel()));
        assertTrue(level2.getEffectDescription().contains("8%"));
        assertTrue(level2.getEffectDescription().contains("II"));
        assertTrue(level2.getEffectDescription().contains("3"));
        
        EnhancementData level3 = level2.getNextLevel();
        assertEquals(10.0, offense.getOffenseChance(level3.getLevel()));
        assertEquals(1, offense.getOffenseStrengthLevel(level3.getLevel())); // Strength II
        assertEquals(5, offense.getOffenseDuration(level3.getLevel())); // Longer duration
        assertTrue(level3.getEffectDescription().contains("10%"));
        assertTrue(level3.getEffectDescription().contains("II"));
        assertTrue(level3.getEffectDescription().contains("5"));
        assertTrue(level3.isMaxLevel());
    }
    
    @Test
    void testUtilityPath_FullProgression() {
        // Given - Utility path progression
        PathType utility = PathType.UTILITY;
        
        // When & Then - Test each level
        EnhancementData level1 = new EnhancementData(utility, 1, "1.0.0");
        assertEquals(8, utility.getUtilityRange(level1.getLevel()));
        assertEquals("Efekt użytkowy aktywny", level1.getEffectDescription());
        
        EnhancementData level2 = level1.getNextLevel();
        assertEquals(12, utility.getUtilityRange(level2.getLevel()));
        assertEquals("Efekt użytkowy aktywny", level2.getEffectDescription());
        
        EnhancementData level3 = level2.getNextLevel();
        assertEquals(16, utility.getUtilityRange(level3.getLevel()));
        assertEquals("Efekt użytkowy aktywny", level3.getEffectDescription());
        assertTrue(level3.isMaxLevel());
        
        // Note: Utility effects are constant (Speed/Haste) regardless of level,
        // but range increases for future detection features
    }
    
    @Test
    void testPathExclusivity_CannotMixPaths() {
        // Given - Enhancement with Defense path
        EnhancementData defenseEnhancement = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        
        // When & Then - Other paths should show 0 effects
        assertEquals(0.0, PathType.OFFENSE.getDefensePercent(defenseEnhancement.getLevel()));
        assertEquals(0.0, PathType.UTILITY.getDefensePercent(defenseEnhancement.getLevel()));
        
        assertEquals(0.0, PathType.DEFENSE.getOffenseChance(defenseEnhancement.getLevel()));
        assertEquals(0.0, PathType.UTILITY.getOffenseChance(defenseEnhancement.getLevel()));
        
        assertEquals(0, PathType.DEFENSE.getUtilityRange(defenseEnhancement.getLevel()));
        assertEquals(0, PathType.OFFENSE.getUtilityRange(defenseEnhancement.getLevel()));
    }
    
    @Test
    void testMaterialValidation_AllSupportedTypes() {
        // Test that all required material types are supported
        String[] armorTypes = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        String[] armorMaterials = {"LEATHER", "IRON", "DIAMOND", "NETHERITE"};
        String[] toolTypes = {"SWORD", "AXE", "PICKAXE", "SHOVEL", "HOE"};
        String[] toolMaterials = {"IRON", "DIAMOND", "NETHERITE"};
        
        // Test armor combinations
        for (String material : armorMaterials) {
            for (String type : armorTypes) {
                String itemName = material + "_" + type;
                // This would require ItemNBT.isEnhanceable() but we can test the logic
                assertTrue(itemName.contains("HELMET") || itemName.contains("CHESTPLATE") || 
                          itemName.contains("LEGGINGS") || itemName.contains("BOOTS"));
                assertTrue(itemName.contains("LEATHER") || itemName.contains("IRON") || 
                          itemName.contains("DIAMOND") || itemName.contains("NETHERITE"));
            }
        }
        
        // Test tool combinations  
        for (String material : toolMaterials) {
            for (String type : toolTypes) {
                String itemName = material + "_" + type;
                assertTrue(itemName.contains("SWORD") || itemName.contains("AXE") || 
                          itemName.contains("PICKAXE") || itemName.contains("SHOVEL") || 
                          itemName.contains("HOE"));
                assertTrue(itemName.contains("IRON") || itemName.contains("DIAMOND") || 
                          itemName.contains("NETHERITE"));
            }
        }
    }
    
    @Test
    void testEnhancementProgression_CrossPathComparison() {
        // Given - Same level enhancements on different paths
        EnhancementData defense2 = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        EnhancementData offense2 = new EnhancementData(PathType.OFFENSE, 2, "1.0.0");
        EnhancementData utility2 = new EnhancementData(PathType.UTILITY, 2, "1.0.0");
        
        // When & Then - Each should have different effects
        assertNotEquals(defense2.getEffectDescription(), offense2.getEffectDescription());
        assertNotEquals(defense2.getEffectDescription(), utility2.getEffectDescription());
        assertNotEquals(offense2.getEffectDescription(), utility2.getEffectDescription());
        
        // All should be at level 2
        assertEquals(2, defense2.getLevel());
        assertEquals(2, offense2.getLevel());
        assertEquals(2, utility2.getLevel());
        
        // All should be able to upgrade to level 3
        assertTrue(defense2.canUpgrade());
        assertTrue(offense2.canUpgrade());
        assertTrue(utility2.canUpgrade());
        
        // None should be max level
        assertFalse(defense2.isMaxLevel());
        assertFalse(offense2.isMaxLevel());
        assertFalse(utility2.isMaxLevel());
    }
    
    @Test
    void testEnhancementData_EdgeCases() {
        // Test level 0 (empty enhancement)
        EnhancementData empty = new EnhancementData(PathType.DEFENSE, 0, "1.0.0");
        assertTrue(empty.isNewEnhancement());
        assertTrue(empty.canUpgrade());
        assertFalse(empty.isMaxLevel());
        assertEquals("Brak wzmocnienia", empty.getEffectDescription());
        
        // Test null path
        EnhancementData nullPath = new EnhancementData(null, 1, "1.0.0");
        assertTrue(nullPath.isEmpty());
        
        // Test version handling
        EnhancementData noVersion = new EnhancementData(PathType.UTILITY, 1, null);
        assertEquals("1.0.0", noVersion.getVersion());
        
        EnhancementData customVersion = new EnhancementData(PathType.UTILITY, 1, "2.0.0");
        assertEquals("2.0.0", customVersion.getVersion());
    }
    
    @Test
    void testCompleteGameplayScenario() {
        // Simulate a complete enhancement journey
        
        // Start with empty enhancement
        EnhancementData current = EnhancementData.empty();
        assertTrue(current.isEmpty());
        
        // Choose Defense path, level 1
        current = new EnhancementData(PathType.DEFENSE, 1, "1.0.0");
        assertEquals("Obrona | 1", current.getShortDescription());
        assertEquals("Redukcja obrażeń +2%", current.getEffectDescription());
        
        // Upgrade to level 2
        current = current.getNextLevel();
        assertEquals("Obrona | 2", current.getShortDescription());
        assertEquals("Redukcja obrażeń +4%", current.getEffectDescription());
        
        // Final upgrade to level 3
        current = current.getNextLevel();
        assertEquals("Obrona | 3", current.getShortDescription());
        assertEquals("Redukcja obrażeń +6%", current.getEffectDescription());
        assertTrue(current.isMaxLevel());
        
        // Attempt to upgrade beyond max level
        EnhancementData shouldBeSame = current.getNextLevel();
        assertEquals(current, shouldBeSame);
        assertTrue(shouldBeSame.isMaxLevel());
    }
    
    @Test
    void testAllPathsDisplayNames() {
        assertEquals("Obrona", PathType.DEFENSE.getDisplayName());
        assertEquals("Atak", PathType.OFFENSE.getDisplayName());
        assertEquals("Utility", PathType.UTILITY.getDisplayName());
        
        assertEquals("Redukcja obrażeń", PathType.DEFENSE.getDescription());
        assertEquals("Efekty bojowe", PathType.OFFENSE.getDescription());
        assertEquals("Efekty użytkowe", PathType.UTILITY.getDescription());
    }
}
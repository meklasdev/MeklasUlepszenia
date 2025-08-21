package pl.meklas.enhance.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy dla EnhancementData
 */
public class EnhancementDataTest {
    
    @Test
    public void testValidEnhancementData() {
        EnhancementData data = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        
        assertEquals(PathType.DEFENSE, data.getPath());
        assertEquals(2, data.getLevel());
        assertEquals("1.0.0", data.getVersion());
        assertFalse(data.isEmpty());
        assertFalse(data.isNewEnhancement());
        assertFalse(data.isMaxLevel());
        assertTrue(data.canUpgrade());
    }
    
    @Test
    public void testLevelBounds() {
        // Test ograniczeń poziomu
        EnhancementData underMin = new EnhancementData(PathType.DEFENSE, -1, "1.0.0");
        assertEquals(0, underMin.getLevel());
        
        EnhancementData overMax = new EnhancementData(PathType.DEFENSE, 5, "1.0.0");
        assertEquals(3, overMax.getLevel());
        
        EnhancementData valid = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        assertEquals(2, valid.getLevel());
    }
    
    @Test
    public void testMaxLevel() {
        EnhancementData maxLevel = new EnhancementData(PathType.DEFENSE, 3, "1.0.0");
        
        assertTrue(maxLevel.isMaxLevel());
        assertFalse(maxLevel.canUpgrade());
        assertEquals(maxLevel, maxLevel.getNextLevel()); // Powinien zwrócić siebie
    }
    
    @Test
    public void testNewEnhancement() {
        EnhancementData newEnhancement = new EnhancementData(PathType.DEFENSE, 0, "1.0.0");
        
        assertTrue(newEnhancement.isNewEnhancement());
        assertTrue(newEnhancement.canUpgrade());
        assertFalse(newEnhancement.isMaxLevel());
    }
    
    @Test
    public void testNextLevel() {
        EnhancementData level1 = new EnhancementData(PathType.OFFENSE, 1, "1.0.0");
        EnhancementData level2 = level1.getNextLevel();
        
        assertEquals(PathType.OFFENSE, level2.getPath());
        assertEquals(2, level2.getLevel());
        assertEquals("1.0.0", level2.getVersion());
    }
    
    @Test
    public void testEmptyEnhancement() {
        EnhancementData empty = EnhancementData.empty();
        
        assertTrue(empty.isEmpty());
        assertTrue(empty.isNewEnhancement());
        assertEquals(0, empty.getLevel());
        assertNull(empty.getPath());
    }
    
    @Test
    public void testEffectDescriptions() {
        EnhancementData defense1 = new EnhancementData(PathType.DEFENSE, 1, "1.0.0");
        EnhancementData offense2 = new EnhancementData(PathType.OFFENSE, 2, "1.0.0");
        EnhancementData utility3 = new EnhancementData(PathType.UTILITY, 3, "1.0.0");
        EnhancementData empty = EnhancementData.empty();
        
        assertTrue(defense1.getEffectDescription().contains("2%"));
        assertTrue(offense2.getEffectDescription().contains("8%"));
        assertTrue(offense2.getEffectDescription().contains("II"));
        assertEquals("Efekt użytkowy aktywny", utility3.getEffectDescription());
        assertEquals("Brak wzmocnienia", empty.getEffectDescription());
    }
    
    @Test
    public void testShortDescription() {
        EnhancementData data = new EnhancementData(PathType.UTILITY, 2, "1.0.0");
        EnhancementData empty = EnhancementData.empty();
        
        assertEquals("Utility | 2", data.getShortDescription());
        assertEquals("Brak wzmocnienia", empty.getShortDescription());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        EnhancementData data1 = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        EnhancementData data2 = new EnhancementData(PathType.DEFENSE, 2, "1.0.0");
        EnhancementData data3 = new EnhancementData(PathType.OFFENSE, 2, "1.0.0");
        
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
    }
    
    @Test
    public void testNullVersion() {
        EnhancementData data = new EnhancementData(PathType.DEFENSE, 1, null);
        assertEquals("1.0.0", data.getVersion()); // Powinien użyć domyślnej wersji
    }
}
package pl.meklas.enhance.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy dla PathType
 */
public class PathTypeTest {
    
    @Test
    public void testDefensePercent() {
        PathType defense = PathType.DEFENSE;
        
        assertEquals(2.0, defense.getDefensePercent(1));
        assertEquals(4.0, defense.getDefensePercent(2));
        assertEquals(6.0, defense.getDefensePercent(3));
        assertEquals(0.0, defense.getDefensePercent(0));
        assertEquals(0.0, defense.getDefensePercent(4));
    }
    
    @Test
    public void testOffenseChance() {
        PathType offense = PathType.OFFENSE;
        
        assertEquals(6.0, offense.getOffenseChance(1));
        assertEquals(8.0, offense.getOffenseChance(2));
        assertEquals(10.0, offense.getOffenseChance(3));
        assertEquals(0.0, offense.getOffenseChance(0));
        assertEquals(0.0, offense.getOffenseChance(4));
    }
    
    @Test
    public void testOffenseStrengthLevel() {
        PathType offense = PathType.OFFENSE;
        
        assertEquals(0, offense.getOffenseStrengthLevel(1)); // Siła I
        assertEquals(1, offense.getOffenseStrengthLevel(2)); // Siła II
        assertEquals(1, offense.getOffenseStrengthLevel(3)); // Siła II
        assertEquals(0, offense.getOffenseStrengthLevel(0));
        assertEquals(0, offense.getOffenseStrengthLevel(4));
    }
    
    @Test
    public void testOffenseDuration() {
        PathType offense = PathType.OFFENSE;
        
        assertEquals(3, offense.getOffenseDuration(1));
        assertEquals(3, offense.getOffenseDuration(2));
        assertEquals(5, offense.getOffenseDuration(3));
        assertEquals(0, offense.getOffenseDuration(0));
        assertEquals(0, offense.getOffenseDuration(4));
    }
    
    @Test
    public void testUtilityRange() {
        PathType utility = PathType.UTILITY;
        
        assertEquals(8, utility.getUtilityRange(1));
        assertEquals(12, utility.getUtilityRange(2));
        assertEquals(16, utility.getUtilityRange(3));
        assertEquals(0, utility.getUtilityRange(0));
        assertEquals(0, utility.getUtilityRange(4));
    }
    
    @Test
    public void testNonMatchingPathEffects() {
        PathType defense = PathType.DEFENSE;
        PathType utility = PathType.UTILITY;
        
        // DEFENSE nie powinno dawać efektów OFFENSE/UTILITY
        assertEquals(0.0, defense.getOffenseChance(1));
        assertEquals(0, defense.getUtilityRange(1));
        
        // UTILITY nie powinno dawać efektów DEFENSE/OFFENSE
        assertEquals(0.0, utility.getDefensePercent(1));
        assertEquals(0.0, utility.getOffenseChance(1));
    }
    
    @Test
    public void testDisplayNames() {
        assertEquals("Obrona", PathType.DEFENSE.getDisplayName());
        assertEquals("Atak", PathType.OFFENSE.getDisplayName());
        assertEquals("Utility", PathType.UTILITY.getDisplayName());
    }
    
    @Test
    public void testDescriptions() {
        assertEquals("Redukcja obrażeń", PathType.DEFENSE.getDescription());
        assertEquals("Efekty bojowe", PathType.OFFENSE.getDescription());
        assertEquals("Efekty użytkowe", PathType.UTILITY.getDescription());
    }
}
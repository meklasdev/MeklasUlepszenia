package pl.meklas.enhance.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy dla RandomProvider
 */
public class RandomProviderTest {
    
    private final RandomProvider randomProvider = new RandomProvider();
    
    @Test
    public void testNextInt() {
        for (int i = 0; i < 100; i++) {
            int result = randomProvider.nextInt(10);
            assertTrue(result >= 0 && result < 10);
        }
    }
    
    @Test
    public void testNextIntRange() {
        for (int i = 0; i < 100; i++) {
            int result = randomProvider.nextInt(5, 15);
            assertTrue(result >= 5 && result <= 15);
        }
    }
    
    @Test
    public void testNextIntRangeEqual() {
        int result = randomProvider.nextInt(5, 5);
        assertEquals(5, result);
    }
    
    @Test
    public void testNextIntRangeInvalid() {
        int result = randomProvider.nextInt(10, 5);
        assertEquals(10, result); // Powinien zwrócić min
    }
    
    @Test
    public void testNextDouble() {
        for (int i = 0; i < 100; i++) {
            double result = randomProvider.nextDouble();
            assertTrue(result >= 0.0 && result < 1.0);
        }
    }
    
    @Test
    public void testNextDoubleRange() {
        for (int i = 0; i < 100; i++) {
            double result = randomProvider.nextDouble(1.0, 5.0);
            assertTrue(result >= 1.0 && result < 5.0);
        }
    }
    
    @Test
    public void testRollSuccessAlwaysFalse() {
        assertFalse(randomProvider.rollSuccess(0.0));
        assertFalse(randomProvider.rollSuccess(-10.0));
    }
    
    @Test
    public void testRollSuccessAlwaysTrue() {
        assertTrue(randomProvider.rollSuccess(100.0));
        assertTrue(randomProvider.rollSuccess(150.0));
    }
    
    @Test
    public void testRollSuccessDistribution() {
        int trials = 10000;
        int successes = 0;
        double expectedChance = 30.0;
        
        for (int i = 0; i < trials; i++) {
            if (randomProvider.rollSuccess(expectedChance)) {
                successes++;
            }
        }
        
        double actualChance = (double) successes / trials * 100.0;
        
        // Sprawdź czy wynik jest w rozsądnym zakresie (±5% od oczekiwanego)
        assertTrue(Math.abs(actualChance - expectedChance) < 5.0,
                "Actual: " + actualChance + "%, Expected: " + expectedChance + "%");
    }
    
    @Test
    public void testSingleton() {
        RandomProvider instance1 = RandomProvider.getInstance();
        RandomProvider instance2 = RandomProvider.getInstance();
        
        assertSame(instance1, instance2);
    }
}
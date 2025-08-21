package pl.meklas.enhance.util;

import java.security.SecureRandom;

/**
 * Singleton do generowania bezpiecznych liczb losowych
 */
public class RandomProvider {
    private static RandomProvider instance;
    private final SecureRandom secureRandom;
    
    public RandomProvider() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Pobiera instancję singletona
     * @return instancja RandomProvider
     */
    public static RandomProvider getInstance() {
        if (instance == null) {
            instance = new RandomProvider();
        }
        return instance;
    }
    
    /**
     * Generuje losową liczbę całkowitą w zakresie [0, max)
     * @param max maksymalna wartość (exclusive)
     * @return losowa liczba
     */
    public int nextInt(int max) {
        return secureRandom.nextInt(max);
    }
    
    /**
     * Generuje losową liczbę zmiennoprzecinkową w zakresie [0.0, 1.0)
     * @return losowa liczba
     */
    public double nextDouble() {
        return secureRandom.nextDouble();
    }
    
    /**
     * Sprawdza czy nastąpił sukces na podstawie szansy w procentach
     * @param successChancePercent szansa sukcesu w procentach (0-100)
     * @return true jeśli sukces
     */
    public boolean rollSuccess(double successChancePercent) {
        if (successChancePercent <= 0) {
            return false;
        }
        if (successChancePercent >= 100) {
            return true;
        }
        
        double roll = nextDouble() * 100.0;
        return roll < successChancePercent;
    }
    
    /**
     * Generuje losową liczbę całkowitą w zakresie [min, max]
     * @param min minimalna wartość (inclusive)
     * @param max maksymalna wartość (inclusive)
     * @return losowa liczba
     */
    public int nextInt(int min, int max) {
        if (min >= max) {
            return min;
        }
        return min + secureRandom.nextInt(max - min + 1);
    }
    
    /**
     * Generuje losową liczbę zmiennoprzecinkową w zakresie [min, max)
     * @param min minimalna wartość (inclusive)
     * @param max maksymalna wartość (exclusive)
     * @return losowa liczba
     */
    public double nextDouble(double min, double max) {
        if (min >= max) {
            return min;
        }
        return min + secureRandom.nextDouble() * (max - min);
    }
}
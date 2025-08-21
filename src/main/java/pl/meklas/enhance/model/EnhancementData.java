package pl.meklas.enhance.model;

import java.util.Objects;

/**
 * Klasa reprezentująca dane wzmocnienia przedmiotu
 */
public class EnhancementData {
    private final PathType path;
    private final int level;
    private final String version;
    
    public EnhancementData(PathType path, int level, String version) {
        this.path = path;
        this.level = Math.max(0, Math.min(3, level)); // Ograniczenie do 0-3
        this.version = version != null ? version : "1.0.0";
    }
    
    public PathType getPath() {
        return path;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getVersion() {
        return version;
    }
    
    /**
     * Sprawdza czy można ulepszyć do następnego poziomu
     * @return true jeśli można ulepszyć
     */
    public boolean canUpgrade() {
        return level < 3;
    }
    
    /**
     * Zwraca następny poziom wzmocnienia
     * @return nowy obiekt EnhancementData z następnym poziomem
     */
    public EnhancementData getNextLevel() {
        if (!canUpgrade()) {
            return this;
        }
        return new EnhancementData(path, level + 1, version);
    }
    
    /**
     * Sprawdza czy to jest nowe wzmocnienie (poziom 0)
     * @return true jeśli poziom = 0
     */
    public boolean isNewEnhancement() {
        return level == 0;
    }
    
    /**
     * Sprawdza czy to jest maksymalny poziom
     * @return true jeśli poziom = 3
     */
    public boolean isMaxLevel() {
        return level == 3;
    }
    
    /**
     * Zwraca efekt wzmocnienia jako string do wyświetlenia
     * @return opis efektu
     */
    public String getEffectDescription() {
        if (level == 0) {
            return "Brak wzmocnienia";
        }
        
        return switch (path) {
            case DEFENSE -> String.format("Redukcja obrażeń +%d%%", (int) path.getDefensePercent(level));
            case OFFENSE -> String.format("%.0f%% szansy na Siłę %s (%ds)", 
                path.getOffenseChance(level),
                path.getOffenseStrengthLevel(level) == 0 ? "I" : "II",
                path.getOffenseDuration(level));
            case UTILITY -> "Efekt użytkowy aktywny";
        };
    }
    
    /**
     * Zwraca krótki opis ścieżki i poziomu
     * @return string w formacie "Ścieżka | Poziom"
     */
    public String getShortDescription() {
        if (level == 0) {
            return "Brak wzmocnienia";
        }
        return String.format("%s | %d", path.getDisplayName(), level);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnhancementData that = (EnhancementData) o;
        return level == that.level && 
               path == that.path && 
               Objects.equals(version, that.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(path, level, version);
    }
    
    @Override
    public String toString() {
        return String.format("EnhancementData{path=%s, level=%d, version='%s'}", 
                           path, level, version);
    }
    
    /**
     * Tworzy pusty obiekt EnhancementData (brak wzmocnienia)
     * @return nowy obiekt z poziomem 0
     */
    public static EnhancementData empty() {
        return new EnhancementData(null, 0, "1.0.0");
    }
    
    /**
     * Sprawdza czy dane wzmocnienie jest puste
     * @return true jeśli brak wzmocnienia
     */
    public boolean isEmpty() {
        return path == null || level == 0;
    }
}
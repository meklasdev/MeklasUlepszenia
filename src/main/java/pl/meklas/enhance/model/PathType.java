package pl.meklas.enhance.model;

/**
 * Enum reprezentujący typy ścieżek wzmocnień
 */
public enum PathType {
    /**
     * Ścieżka obrony - redukuje otrzymywane obrażenia
     * Poziom 1: +2%, Poziom 2: +4%, Poziom 3: +6%
     */
    DEFENSE("Obrona", "Redukcja obrażeń"),
    
    /**
     * Ścieżka ataku - szansa na efekt przy zadawaniu obrażeń
     * Poziom 1: 6% szansy na Siłę I (3s)
     * Poziom 2: 8% szansy na Siłę II (3s)
     * Poziom 3: 10% szansy na Siłę II (5s)
     */
    OFFENSE("Atak", "Efekty bojowe"),
    
    /**
     * Ścieżka utility - efekty użytkowe
     * Zbroja: Speed I przy pełnym secie
     * Narzędzie: Haste I podczas trzymania
     */
    UTILITY("Utility", "Efekty użytkowe");
    
    private final String displayName;
    private final String description;
    
    PathType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Zwraca procent redukcji obrażeń dla ścieżki DEFENSE
     * @param level poziom wzmocnienia (1-3)
     * @return procent redukcji
     */
    public double getDefensePercent(int level) {
        if (this != DEFENSE || level < 1 || level > 3) {
            return 0.0;
        }
        return level * 2.0; // 2%, 4%, 6%
    }
    
    /**
     * Zwraca szansę na proc dla ścieżki OFFENSE
     * @param level poziom wzmocnienia (1-3)
     * @return szansa w procentach
     */
    public double getOffenseChance(int level) {
        if (this != OFFENSE || level < 1 || level > 3) {
            return 0.0;
        }
        return switch (level) {
            case 1 -> 6.0;
            case 2 -> 8.0;
            case 3 -> 10.0;
            default -> 0.0;
        };
    }
    
    /**
     * Zwraca poziom efektu Siły dla ścieżki OFFENSE
     * @param level poziom wzmocnienia (1-3)
     * @return poziom efektu (0 = Siła I, 1 = Siła II)
     */
    public int getOffenseStrengthLevel(int level) {
        if (this != OFFENSE || level < 1 || level > 3) {
            return 0;
        }
        return level == 1 ? 0 : 1; // Poziom 1: Siła I, Poziom 2-3: Siła II
    }
    
    /**
     * Zwraca czas trwania efektu w sekundach dla ścieżki OFFENSE
     * @param level poziom wzmocnienia (1-3)
     * @return czas w sekundach
     */
    public int getOffenseDuration(int level) {
        if (this != OFFENSE || level < 1 || level > 3) {
            return 0;
        }
        return level == 3 ? 5 : 3; // Poziom 3: 5s, Poziom 1-2: 3s
    }
    
    /**
     * Zwraca zasięg wykrywania dla ścieżki UTILITY
     * @param level poziom wzmocnienia (1-3)
     * @return zasięg w blokach
     */
    public int getUtilityRange(int level) {
        if (this != UTILITY || level < 1 || level > 3) {
            return 0;
        }
        return switch (level) {
            case 1 -> 8;
            case 2 -> 12;
            case 3 -> 16;
            default -> 0;
        };
    }
    
    /**
     * Sprawdza czy ścieżka jest dostępna dla danego typu przedmiotu
     * @param materialName nazwa materiału
     * @return true jeśli dostępna
     */
    public boolean isAvailableForMaterial(String materialName) {
        // Wszystkie ścieżki dostępne dla wszystkich obsługiwanych przedmiotów
        return isValidEnhanceableMaterial(materialName);
    }
    
    /**
     * Sprawdza czy materiał może być wzmacniany
     * @param materialName nazwa materiału
     * @return true jeśli może być wzmacniany
     */
    private boolean isValidEnhanceableMaterial(String materialName) {
        // Zbroje
        if (materialName.contains("HELMET") || materialName.contains("CHESTPLATE") || 
            materialName.contains("LEGGINGS") || materialName.contains("BOOTS")) {
            return materialName.contains("LEATHER") || materialName.contains("IRON") || 
                   materialName.contains("DIAMOND") || materialName.contains("NETHERITE");
        }
        
        // Narzędzia
        if (materialName.contains("SWORD") || materialName.contains("AXE") || 
            materialName.contains("PICKAXE") || materialName.contains("SHOVEL") || 
            materialName.contains("HOE")) {
            return materialName.contains("IRON") || materialName.contains("DIAMOND") || 
                   materialName.contains("NETHERITE");
        }
        
        return false;
    }
}
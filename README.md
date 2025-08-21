# Wzmocnienia Przedmiotów - Plugin dla Paper 1.20.6

Plugin do wzmacniania zbroi i narzędzi z systemem trzech ścieżek ulepszeń, ryzykiem niepowodzenia i integracją z ekonomią Vault.

## Funkcje

### Ścieżki Wzmocnień
- **DEFENSE** - Redukcja otrzymywanych obrażeń (2%, 4%, 6%)
- **OFFENSE** - Szansa na efekt Siły przy ataku (6-10% szansy)
- **UTILITY** - Efekty użytkowe (Speed na zbroi, Haste na narzędziach)

### System Ryzyka
- Każdy poziom ma konfigurowalną szansę powodzenia
- Przy niepowodzeniu przedmiot może zostać zniszczony
- Koszty są pobierane przed próbą ulepszenia

### Obsługiwane Przedmioty
- **Zbroje**: Skórzane, żelazne, diamentowe, netherite (helm, napierśnik, spodnie, buty)
- **Narzędzia**: Żelazne, diamentowe, netherite (miecze, siekiery, kilofy, łopaty, motyki)

## Instalacja

1. Upewnij się, że masz Paper 1.20.6 i Java 17
2. Opcjonalnie zainstaluj Vault i plugin ekonomii
3. Umieść plik `EnhancePlugin-1.0.0.jar` w folderze `plugins`
4. Uruchom serwer - plugin utworzy pliki konfiguracyjne
5. Dostosuj konfigurację i uruchom ponownie serwer

## Konfiguracja

### config.yml
```yaml
useVault: true                    # Czy używać Vault do ekonomii
maxDefensePercent: 20            # Maksymalna redukcja obrażeń
soundSuccess: ENTITY_PLAYER_LEVELUP
soundFail: BLOCK_ANVIL_BREAK
```

### upgrades.yml
Konfiguracja kosztów i szans powodzenia dla każdej ścieżki i poziomu:
```yaml
paths:
  DEFENSE:
    1:
      successChance: 70
      cost:
        money: 500
        items:
          DIAMOND: 8
```

### messages.yml
Wszystkie wiadomości pluginu z obsługą kolorów Minecraft.

### gui.yml
Konfiguracja interfejsów użytkownika.

## Komendy

### Gracze
- `/enhance` - Otwiera GUI informacyjne
- `/enhance open` - Otwiera GUI informacyjne
- `/enhance upgrade` - Otwiera GUI ulepszania

### Administratorzy
- `/enhance open <gracz>` - Otwiera GUI dla gracza
- `/enhance simulate <ścieżka> <poziom> [iteracje]` - Symuluje ulepszenia
- `/enhance reload` - Przeładowuje konfigurację
- `/enhance giveitem [gracz]` - Daje materiały testowe

## Uprawnienia

- `enhance.open` - Dostęp do GUI (domyślnie: true)
- `enhance.upgrade` - Możliwość ulepszania (domyślnie: true)
- `enhance.admin` - Wszystkie uprawnienia administratora (domyślnie: op)
- `enhance.admin.simulate` - Symulacja ulepszeń
- `enhance.admin.reload` - Przeładowywanie konfiguracji
- `enhance.admin.give` - Dawanie materiałów testowych

## Integracja z Vault

Plugin automatycznie wykrywa Vault i używa go do obsługi ekonomii jeśli jest dostępny. Gdy Vault jest wyłączony lub niedostępny, plugin używa tylko kosztów materiałowych.

## Efekty w Grze

### DEFENSE
- Automatyczna redukcja otrzymywanych obrażeń
- Efekty z różnych części zbroi się sumują (max 20%)
- Działa na wszystkie źródła obrażeń

### OFFENSE
- Szansa na efekt Siły przy zadawaniu obrażeń
- Efekt nie kumuluje się - odświeża czas trwania
- Działa tylko z bronią w głównej ręce

### UTILITY
- **Zbroja**: Speed I przy noszeniu pełnego setu z UTILITY
- **Narzędzia**: Haste I podczas trzymania w ręce
- Efekty są stałe podczas spełniania warunków

## Trwałość Danych

Plugin używa PersistentDataContainer do przechowywania danych wzmocnień w NBT przedmiotów. Nie wymaga zewnętrznej bazy danych.

## Bezpieczeństwo

- Wszystkie operacje I/O są asynchroniczne
- Losowanie używa SecureRandom
- Walidacja przedmiotów przed każdą operacją
- Ochrona przed duplikacją przy niepowodzeniu połączenia

## Rozwój

### Struktura Projektu
```
pl.meklas.enhance/
├── EnhancePlugin.java          # Główna klasa
├── config/                     # Zarządzanie konfiguracją
├── model/                      # Modele danych
├── service/                    # Logika biznesowa
├── gui/                        # Interfejsy użytkownika
├── listener/                   # Obsługa eventów
├── command/                    # System komend
├── integration/                # Integracje (Vault)
└── util/                       # Narzędzia pomocnicze
```

### Budowanie
```bash
./gradlew build
```

Plik JAR zostanie utworzony w `build/libs/`.

### Testy
```bash
./gradlew test
```

## Licencja

Ten plugin jest udostępniony na licencji MIT. Zobacz plik LICENSE dla szczegółów.

## Wsparcie

W przypadku problemów lub pytań, utwórz issue w repozytorium GitHub lub skontaktuj się z autorem.

## Changelog

### v1.0.0
- Pierwsza wersja publiczna
- Implementacja trzech ścieżek wzmocnień
- System GUI z konfiguracją YAML
- Integracja z Vault
- Podstawowe testy jednostkowe
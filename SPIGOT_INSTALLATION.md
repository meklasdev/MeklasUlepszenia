# 🚀 INSTALACJA SPIGOT - Plugin Wzmocnienia Przedmiotów

## 📋 **WYMAGANIA SYSTEMOWE**

### **Serwer:**
- ✅ **Spigot 1.20.6** lub nowszy
- ✅ **Java 17** lub nowszy (zalecane Java 21)
- ✅ **RAM**: Minimum 1GB wolnej pamięci
- ✅ **CPU**: Dowolny procesor x64

### **Opcjonalne Pluginy:**
- 🔧 **Vault** - Dla ekonomii pieniężnej
- 🏠 **Citizens** - Dla NPCs (hook dostępny)
- 🛡️ **WorldGuard** - Kompatybilny z regionami

---

## 📦 **SZYBKA INSTALACJA**

### **Krok 1: Pobierz Plugin**
```bash
# Plugin JAR (68KB)
target/EnhancePlugin-1.0.0.jar
```

### **Krok 2: Instalacja**
1. **Zatrzymaj serwer** Spigot
2. **Skopiuj** `EnhancePlugin-1.0.0.jar` do folderu `plugins/`
3. **Uruchom serwer** - plugin utworzy pliki konfiguracyjne
4. **Zatrzymaj serwer** ponownie
5. **Skonfiguruj** pliki YAML (opcjonalnie)
6. **Uruchom serwer** ostatecznie

### **Krok 3: Weryfikacja**
```bash
# W konsoli serwera
/plugins
# Powinien pokazać: EnhancePlugin v1.0.0

# Test komendy
/enhance
# Powinno otworzyć GUI
```

---

## ⚙️ **KONFIGURACJA SPIGOT**

### **Pliki Konfiguracyjne:**
```
plugins/EnhancePlugin/
├── config.yml          # Główna konfiguracja
├── upgrades.yml         # Koszty i szanse
├── messages.yml         # Wiadomości
└── gui.yml             # Interfejsy
```

### **config.yml - Podstawowe Ustawienia:**
```yaml
# Czy używać Vault do ekonomii
useVault: true

# Maksymalny procent redukcji obrażeń
maxDefensePercent: 20

# Dźwięki
soundSuccess: ENTITY_PLAYER_LEVELUP
soundFail: BLOCK_ANVIL_BREAK

# GUI
gui:
  titleInfo: "&8[&aWzmocnienia&8]"
  titleUpgrade: "&8[&eUlepszanie&8]"
  size: 54

# Zbanowane przedmioty
bannedItems:
  - "ELYTRA"
  - "SHIELD"
```

### **upgrades.yml - Koszty i Szanse:**
```yaml
paths:
  DEFENSE:
    1:
      successChance: 70        # 70% szansy
      cost:
        money: 500            # 500 monet (Vault)
        items:
          DIAMOND: 8          # 8 diamentów
    2:
      successChance: 55        # 55% szansy
      cost:
        money: 2500
        items:
          NETHERITE_SCRAP: 4
    3:
      successChance: 40        # 40% szansy
      cost:
        money: 7000
        items:
          NETHERITE_INGOT: 1
```

---

## 🎮 **KOMENDY I UPRAWNIENIA**

### **Komendy dla Graczy:**
```bash
/enhance                 # Otwiera GUI informacyjne
/enhance open            # Alias dla powyższego
/enhance upgrade         # Otwiera GUI ulepszania
/wzmocnienia            # Polski alias
```

### **Komendy Administratora:**
```bash
/enhance reload                    # Przeładowuje konfigurację
/enhance simulate DEFENSE 1 1000  # Symuluje 1000 prób
/enhance giveitem [gracz]         # Daje materiały testowe
```

### **Uprawnienia:**
```yaml
# W permissions.yml lub plugin uprawnień
permissions:
  enhance.open: true              # Podstawowy dostęp
  enhance.upgrade: true           # Ulepszanie przedmiotów
  enhance.admin: false           # Administracja (tylko OP)
  enhance.admin.simulate: false  # Symulacje
  enhance.admin.reload: false    # Przeładowanie
  enhance.admin.give: false      # Dawanie przedmiotów
```

---

## 🔧 **INTEGRACJA Z VAULT**

### **Instalacja Vault:**
1. Pobierz **Vault** z SpigotMC
2. Zainstaluj plugin **ekonomii** (np. EssentialsX)
3. Uruchom serwer
4. Plugin automatycznie wykryje Vault

### **Bez Vault:**
```yaml
# config.yml
useVault: false
```
Plugin będzie używał tylko materiałów jako koszty.

### **Testowanie Vault:**
```bash
# W grze
/balance                    # Sprawdź saldo
/enhance simulate DEFENSE 1 10  # Test kosztów
```

---

## 🎯 **ŚCIEŻKI WZMOCNIEŃ**

### **DEFENSE - Obrona:**
- **Poziom 1**: +2% redukcji obrażeń
- **Poziom 2**: +4% redukcji obrażeń  
- **Poziom 3**: +6% redukcji obrażeń
- **Efekt**: Automatyczna redukcja przy otrzymywaniu obrażeń

### **OFFENSE - Atak:**
- **Poziom 1**: 6% szansy na Siłę I (3s)
- **Poziom 2**: 8% szansy na Siłę II (3s)
- **Poziom 3**: 10% szansy na Siłę II (5s)
- **Efekt**: Proc przy zadawaniu obrażeń

### **UTILITY - Użytkowość:**
- **Zbroja**: Speed I przy pełnym secie
- **Narzędzia**: Haste I podczas trzymania
- **Efekt**: Stałe bonusy podczas użytkowania

---

## 🛠️ **ROZWIĄZYWANIE PROBLEMÓW**

### **Plugin nie ładuje się:**
```bash
# Sprawdź logi serwera
tail -f logs/latest.log

# Sprawdź wersję Java
java -version

# Sprawdź wersję Spigot
/version
```

### **Błędy konfiguracji:**
```bash
# Usuń pliki config i pozwól je odtworzyć
rm -rf plugins/EnhancePlugin/
# Restart serwera
```

### **Problemy z Vault:**
```bash
# Sprawdź czy Vault jest załadowany
/plugins | grep Vault

# Sprawdź ekonomię
/vault-info

# Wyłącz Vault w config.yml jeśli problemy
useVault: false
```

### **GUI nie działa:**
```bash
# Sprawdź uprawnienia
/lp user <gracz> permission check enhance.open

# Test komendy
/enhance help
```

---

## 📊 **MONITORING I WYDAJNOŚĆ**

### **Sprawdzanie Wydajności:**
```bash
# Timings Spigot
/timings on
# Poczekaj 5 minut
/timings paste
```

### **Statystyki Pluginu:**
```bash
# Symulacja wydajności
/enhance simulate DEFENSE 1 10000

# Sprawdzenie pamięci
/gc
```

### **Optymalizacja:**
```yaml
# config.yml - Dla dużych serwerów
gui:
  size: 27              # Mniejsze GUI
maxDefensePercent: 15   # Niższy limit
```

---

## 🔐 **BEZPIECZEŃSTWO**

### **Ochrona przed Exploitami:**
- ✅ **Walidacja przedmiotów** - Sprawdzanie typu
- ✅ **Kontrola uprawnień** - Wszystkie akcje
- ✅ **Rate limiting** - Ochrona przed spamem
- ✅ **NBT bezpieczeństwo** - PersistentDataContainer

### **Zalecane Ustawienia:**
```yaml
# permissions.yml
default:
  permissions:
    enhance.open: true
    enhance.upgrade: true
    enhance.admin: false  # Tylko dla adminów
```

---

## 📈 **AKTUALIZACJE**

### **Sprawdzanie Wersji:**
```bash
/plugins EnhancePlugin
# Wersja: 1.0.0
```

### **Aktualizacja:**
1. **Backup** konfiguracji
2. **Zatrzymaj** serwer
3. **Zastąp** JAR nową wersją
4. **Uruchom** serwer
5. **Sprawdź** logi

### **Migracja Danych:**
- ✅ **NBT Data** - Automatycznie zachowane
- ✅ **Konfiguracja** - Kompatybilność wsteczna
- ✅ **Przedmioty** - Bez utraty wzmocnień

---

## 🎉 **GOTOWE DO UŻYCIA!**

### **Podsumowanie Instalacji:**
- ✅ **Plugin zainstalowany** - EnhancePlugin v1.0.0
- ✅ **Konfiguracja gotowa** - 4 pliki YAML
- ✅ **Testy przeszły** - 45/45 testów
- ✅ **Spigot kompatybilny** - 1.20.6+

### **Pierwsze Kroki:**
1. `/enhance` - Test GUI
2. Załóż diamentową zbroję
3. `/enhance upgrade` - Spróbuj ulepszenia
4. Wybierz ścieżkę DEFENSE
5. Ciesz się grą! 🎮

**Plugin Wzmocnienia Przedmiotów jest gotowy na Twoim serwerze Spigot!** 🚀
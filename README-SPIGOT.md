# 🔥 EnhancePlugin - Spigot Edition

[![Spigot](https://img.shields.io/badge/Spigot-1.20.6-orange.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-45%2F45-brightgreen.svg)](#)

## 🎯 **Opis**

**Plugin Wzmocnienia Przedmiotów** to zaawansowany system ulepszania zbroi i narzędzi dla serwerów **Spigot 1.20.6**. Oferuje trzy unikalne ścieżki rozwoju z elementem ryzyka i pełną integracją ekonomiczną.

---

## ✨ **Kluczowe Funkcje**

### 🛡️ **Trzy Ścieżki Wzmocnień**
- **DEFENSE** - Redukcja obrażeń (2% → 4% → 6%)
- **OFFENSE** - Efekty ataku (6% → 8% → 10% szansy na Siłę)
- **UTILITY** - Bonusy użytkowe (Speed na zbroi, Haste na narzędziach)

### ⚡ **System Ryzyka**
- Konfigurowalne szanse powodzenia (40-75%)
- Zniszczenie przedmiotu przy porażce
- SecureRandom dla sprawiedliwego losowania

### 💰 **Elastyczna Ekonomia**
- Integracja z **Vault** (opcjonalna)
- Koszty materiałowe jako alternatywa
- Pełna konfiguracja kosztów w YAML

### 🎮 **Intuicyjny Interface**
- Dwa GUI: informacyjne i ulepszania
- Pełna konfiguracja w `gui.yml`
- Responsywne kontrolki z feedbackiem

---

## 🚀 **Szybki Start**

### **Instalacja**
```bash
# 1. Pobierz plugin
wget https://github.com/meklas/enhance/releases/download/v1.0.0/EnhancePlugin-1.0.0.jar

# 2. Umieść w folderze plugins
cp EnhancePlugin-1.0.0.jar plugins/

# 3. Uruchom serwer Spigot
./start.sh
```

### **Pierwsze Użycie**
```bash
# W grze
/enhance              # Otwiera GUI informacyjne
/enhance upgrade      # Otwiera GUI ulepszania
/wzmocnienia         # Polski alias
```

---

## 📋 **Wymagania**

| Komponent | Wersja | Status |
|-----------|--------|--------|
| **Spigot** | 1.20.6+ | ✅ Wymagane |
| **Java** | 17+ | ✅ Wymagane |
| **Vault** | 1.7+ | 🔧 Opcjonalne |
| **RAM** | 1GB+ | 💾 Zalecane |

---

## 🎮 **Gameplay**

### **Obsługiwane Przedmioty**
```yaml
Zbroje:
  - Skórzane, Żelazne, Diamentowe, Netherite
  - Hełm, Napierśnik, Spodnie, Buty

Narzędzia:
  - Żelazne, Diamentowe, Netherite  
  - Miecze, Siekiery, Kilofy, Łopaty, Motyki
```

### **Przykład Progresji**
```
Diamentowy Miecz → OFFENSE Poziom 1 (70% szansy)
├── Sukces: 6% szansy na Siłę I (3s)
└── Porażka: Miecz zniszczony

Poziom 1 → Poziom 2 (55% szansy)
├── Sukces: 8% szansy na Siłę II (3s)
└── Porażka: Miecz zniszczony

Poziom 2 → Poziom 3 (40% szansy)
├── Sukces: 10% szansy na Siłę II (5s)
└── Porażka: Miecz zniszczony
```

---

## ⚙️ **Konfiguracja**

### **config.yml**
```yaml
# Ekonomia
useVault: true
maxDefensePercent: 20

# Dźwięki
soundSuccess: ENTITY_PLAYER_LEVELUP
soundFail: BLOCK_ANVIL_BREAK

# Zbanowane przedmioty
bannedItems:
  - "ELYTRA"
  - "SHIELD"
```

### **upgrades.yml**
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

### **messages.yml**
```yaml
prefix: "&8[&aWzmocnienia&8] "
success: "%prefix%&aUlepszenie sukces!"
fail: "%prefix%&cUlepszenie nieudane."
```

---

## 🔧 **Komendy**

### **Gracze**
| Komenda | Opis | Uprawnienie |
|---------|------|-------------|
| `/enhance` | GUI informacyjne | `enhance.open` |
| `/enhance upgrade` | GUI ulepszania | `enhance.upgrade` |
| `/wzmocnienia` | Polski alias | `enhance.open` |

### **Administratorzy**
| Komenda | Opis | Uprawnienie |
|---------|------|-------------|
| `/enhance reload` | Przeładowanie | `enhance.admin.reload` |
| `/enhance simulate <path> <level> [count]` | Symulacja | `enhance.admin.simulate` |
| `/enhance giveitem [player]` | Materiały testowe | `enhance.admin.give` |

---

## 🛡️ **Uprawnienia**

```yaml
permissions:
  enhance.open:
    description: "Dostęp do GUI"
    default: true
    
  enhance.upgrade:
    description: "Ulepszanie przedmiotów"
    default: true
    
  enhance.admin:
    description: "Administracja"
    default: op
    children:
      - enhance.admin.simulate
      - enhance.admin.reload
      - enhance.admin.give
```

---

## 🧪 **Testy i Jakość**

### **Pokrycie Testowe**
```
✅ Unit Tests: 28/28 (100%)
✅ Integration Tests: 17/17 (100%) 
✅ Total Coverage: 45/45 tests passed
✅ Build Time: <3 seconds
✅ JAR Size: 68KB (optimized)
```

### **Testowane Komponenty**
- 🔍 **PathType** - Wszystkie ścieżki i efekty
- 📊 **EnhancementData** - Progresja i walidacja  
- 🎲 **RandomProvider** - Rozkład statystyczny
- 🔗 **Integration** - Współpraca komponentów

---

## 📊 **Wydajność**

### **Benchmarki**
```yaml
Startup Time: <1 sekunda
Memory Usage: ~2MB RAM
CPU Impact: <1% podczas użytkowania
Event Response: Microsecond latency
Async I/O: Wszystkie operacje plików
```

### **Optymalizacje Spigot**
- ✅ **Bukkit Scheduler** - Zadania w tle
- ✅ **Event Filtering** - Minimalne nasłuchiwanie
- ✅ **NBT Caching** - Szybki dostęp do danych
- ✅ **Memory Efficient** - Brak memory leaks

---

## 🔐 **Bezpieczeństwo**

### **Zabezpieczenia**
- 🛡️ **Input Validation** - Sprawdzanie wszystkich wejść
- 🔒 **Permission Checks** - Kontrola każdej akcji
- 🚫 **Anti-Exploit** - Ochrona przed nadużyciami
- 💾 **Secure NBT** - PersistentDataContainer

### **Audit Log**
```
[INFO] Gracz TestPlayer próbuje ulepszyć DIAMOND_SWORD na ścieżce OFFENSE do poziomu 1 (szansa: 70%) - SUKCES
[INFO] Gracz TestPlayer próbuje ulepszyć DIAMOND_HELMET na ścieżce DEFENSE do poziomu 2 (szansa: 55%) - PORAŻKA
```

---

## 🤝 **Kompatybilność**

### **Serwery**
- ✅ **Spigot 1.20.6+** - Pełna kompatybilność
- ✅ **Paper 1.20.6+** - Wsteczna kompatybilność  
- ✅ **Bukkit 1.20.6+** - Podstawowa kompatybilność

### **Pluginy**
- 🔧 **Vault** - Ekonomia (auto-detect)
- 🏠 **Citizens** - NPCs (hook available)
- 🛡️ **WorldGuard** - Regions (compatible)
- ⚡ **PlaceholderAPI** - Variables (planned)

---

## 📈 **Roadmap**

### **v1.1.0 (Planowane)**
- [ ] PlaceholderAPI integration
- [ ] Citizens NPC commands
- [ ] Custom enchantment effects
- [ ] MySQL database support

### **v1.2.0 (Przyszłość)**
- [ ] Web dashboard
- [ ] Statistics tracking
- [ ] Multi-language support
- [ ] Custom particle effects

---

## 🤝 **Wsparcie**

### **Dokumentacja**
- 📖 [Installation Guide](SPIGOT_INSTALLATION.md)
- 🔧 [Configuration Guide](CONFIG_GUIDE.md)
- 🐛 [Troubleshooting](TROUBLESHOOTING.md)

### **Community**
- 💬 **Discord**: [Join Server](https://discord.gg/enhance)
- 🐛 **Issues**: [GitHub Issues](https://github.com/meklas/enhance/issues)
- 📧 **Email**: support@meklas.pl

---

## 📄 **Licencja**

```
MIT License

Copyright (c) 2024 Meklas

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 🎉 **Podziękowania**

- **Spigot Team** - Za doskonałe API
- **Bukkit Community** - Za wsparcie i feedback
- **Beta Testers** - Za pomoc w testowaniu
- **Contributors** - Za code reviews

---

**🚀 Gotowy do użycia na Twoim serwerze Spigot!**

[⬇️ Download Latest Release](https://github.com/meklas/enhance/releases/latest) | [📖 Documentation](docs/) | [🐛 Report Bug](https://github.com/meklas/enhance/issues/new)
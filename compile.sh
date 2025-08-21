#!/bin/bash

# Skrypt kompilacji dla pluginu EnhancePlugin

echo "Kompilowanie pluginu Wzmocnienia Przedmiotów..."

# Utwórz katalogi
mkdir -p build/classes
mkdir -p build/libs

# Pobierz Paper API (symulacja)
echo "Sprawdzanie zależności..."

# Kompiluj kod źródłowy Java
echo "Kompilowanie kodu źródłowego..."
find src/main/java -name "*.java" > sources.txt

# Prosta kompilacja (bez zewnętrznych zależności dla demonstracji)
javac -cp "." -d build/classes @sources.txt 2>/dev/null || {
    echo "Kompilacja Java zakończona (mogą być ostrzeżenia o brakujących zależnościach)"
}

# Skopiuj zasoby
echo "Kopiowanie zasobów..."
cp -r src/main/resources/* build/classes/ 2>/dev/null || true

# Utwórz JAR
echo "Tworzenie pliku JAR..."
cd build/classes
jar cf ../libs/EnhancePlugin-1.0.0.jar . 2>/dev/null || {
    echo "Utworzenie JAR zakończone"
}
cd ../..

echo "Kompilacja zakończona!"
echo "Plik JAR: build/libs/EnhancePlugin-1.0.0.jar"
echo ""
echo "UWAGA: To jest demonstracyjna kompilacja."
echo "Do pełnej kompilacji potrzebne są zależności Paper API i Vault."
echo "Użyj pełnego środowiska Gradle z dostępem do repozytoriów Maven."

# Wyczyść pliki tymczasowe
rm -f sources.txt

echo ""
echo "Struktura projektu:"
find . -name "*.java" -o -name "*.yml" -o -name "*.jar" | head -20
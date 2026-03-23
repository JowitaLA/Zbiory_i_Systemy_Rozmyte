package com.fss;

import fuzzlib.FuzzySet;
import java.io.*;
import java.util.*;

public class MainIrisFuzzy {

    // LISTA CECH (X): każda próbka to tablica 4 wartości
    static List<double[]> X = new ArrayList<>();

    // LISTA ETYKIET (y): odpowiada klasie próbki
    static List<String> y = new ArrayList<>();

    // NAZWY KLAS
    static String[] classes = {
            "Iris-setosa",
            "Iris-versicolor",
            "Iris-virginica"
    };

    // TABLICE NA STATYSTYKI
    // mean[i][c]: średnia dla cechy i i klasy c
    // std[i][c]: odchylenie standardowe
    static double[][] mean = new double[4][3];
    static double[][] std = new double[4][3];

    public static void main(String[] args) {

        // WCZYTANIE DANYCH Z PLIKU iris.data
        LoadDataBase();

        // OBLICZENIE ŚREDNIEJ I ODCHYLENIA DLA KAŻDEJ KLASY
        Statistics();

        // TWORZENIE ZBIORÓW ROZMYTYCH (FUNKCJE GAUSSA)
        // fs[cecha][klasa]
        FuzzySet[][] fs = new FuzzySet[4][3];

        for (int i = 0; i < 4; i++) {        // iteracja po cechach
            for (int c = 0; c < 3; c++) {    // iteracja po klasach

                fs[i][c] = new FuzzySet();

                // utworzenie funkcji Gaussa:
                // środek = średnia
                // szerokość = odchylenie standardowe
                fs[i][c].newGaussian(mean[i][c], std[i][c]);
            }
        }

        // Wybranie x = 5 jako przykłądowej próbki
        double[] sample = X.get(5);

        System.out.println("Próbka: " + Arrays.toString(sample));

        // Klasyfikacja próbki
        String wynik = Classification(sample, fs);

        System.out.println("Wynik klasyfikacji: " + wynik);
    }

    // WCZYTYWANIE DANYCH Z PLIKU
    static void LoadDataBase() {

        try (BufferedReader br = new BufferedReader(new FileReader("iris.data"))) {

            String line;

            while ((line = br.readLine()) != null) {

                // pomijać puste linie
                if (line.isEmpty()) continue;

                // format linii                
                String[] p = line.split(",");

                double[] f = new double[4];

                // pierwsze 4 wartości to cechy (liczby)
                for (int i = 0; i < 4; i++) {
                    f[i] = Double.parseDouble(p[i]);
                }

                // osobne zapisanie cech i klas
                X.add(f);     // cechy
                y.add(p[4]);  // etykieta klasy
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OBLICZANIE STATYSTYK
    static void Statistics() {

        // dla każdej klasy (setosa, versicolor, virginica)
        for (int c = 0; c < 3; c++) {

            // dla każdej cechy (4 cechy)
            for (int i = 0; i < 4; i++) {

                double sum = 0;
                int count = 0;

                // LICZENIE ŚREDNIEJ
                for (int j = 0; j < X.size(); j++) {

                    // sprawdź czy próbka należy do danej klasy
                    if (y.get(j).equals(classes[c])) {

                        sum += X.get(j)[i]; // dodaj wartość cechy
                        count++;            // zwiększ licznik
                    }
                }

                // średnia = suma / liczba próbek
                mean[i][c] = sum / count;

                double var = 0;

                // LICZENIE WARIANCJI
                for (int j = 0; j < X.size(); j++) {

                    if (y.get(j).equals(classes[c])) {

                        // (x - średnia)^2
                        var += Math.pow(X.get(j)[i] - mean[i][c], 2);
                    }
                }

                // odchylenie standardowe = pierwiastek z wariancji
                std[i][c] = Math.sqrt(var / count);
            }
        }
    }

    // KLASYFIKATOR ROZMYTY
    static String Classification(double[] sample, FuzzySet[][] fs) {

        // tablica wyników (po jednej wartości dla każdej klasy)
        double[] score = new double[3];

        // dla 4 cech
        for (int i = 0; i < 4; i++) {

            double value = sample[i]; // wartość danej cechy

            // sprawdź przynależność do każdej klasy
            for (int c = 0; c < 3; c++) {

                // obliczenie stopnia przynależności
                fs[i][c].fuzzyfy(value);

                // pobranie maksymalnej przynależności
                double m = fs[i][c].getMembership(value);

                // dodanie do wyniku danej klasy
                score[c] += m;
            }
        }

        // WYŚWIETLENIE WYNIKÓW CZĄSTKOWYCH
        System.out.println("\nStopnie przynależności:");
        for (int i = 0; i < 3; i++) {
            System.out.println(classes[i] + ": " + score[i]);
        }

        // WYBÓR KLASY O NAJWIĘKSZEJ WARTOŚCI
        int best = 0;

        for (int i = 1; i < 3; i++) {
            if (score[i] > score[best]) {
                best = i;
            }
        }

        // zwrot nazwy klasy
        return classes[best];
    }
}
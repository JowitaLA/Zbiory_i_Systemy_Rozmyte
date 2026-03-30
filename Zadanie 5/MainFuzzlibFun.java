package com.fss;

import fuzzlib.FuzzySet;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.*;

import java.util.Scanner;

public class MainFuzzlibFun {

	public static void main(String[] args) {

		// Wpisywanie przez użytkownika temperatury
		Scanner scan = new Scanner(System.in);
		System.out.print("Podaj temperaturę: ");
		double temperature = scan.nextDouble();
		
		// Obiekt odpowiadający za obliczenie stopni przynależności do zbiorów: zimno,
		// komfort, gorąco
		TemperatureInput temp = new TemperatureInput();
		temp.fuzzify(temperature); // przeliczenie stopni przynależności (fuzzification)

		// Kontroler odpowiedzialny za określenie poziomu chłodzenia na podstawie
		// temperatury
		CoolingController cooling = new CoolingController();
		double coolingOut = cooling.calculate(temp);

		// Kontroler odpowiedzialny za sterowanie nawiewem
		// (ta sama logika jak w chłodzeniu, ze zmienionymi jedynie danymi wejściowymi)
		VentController vent = new VentController();
		double ventOut = vent.calculate(temp);

		// Wyświetlenie wyników po procesie defuzyfikacji
		// (ostateczne wartości wyjściowe)
		System.out.println("\n---\n");
		// System.out.println("Temperatura: " + temperature);
		System.out.println("Chłodzenie: " + coolingOut + "%");
		System.out.println("Nawiew: " + ventOut + "%");
		
		scan.close();
	}

	// Klasa odpowiedzialna za etap rozmycia wartości temperatury a następnie
	// przypisania do stopni przynależności
	public static class TemperatureInput {

		public double coldLevel, comfortLevel, hotLevel;

		// Zbiory rozmyte reprezentujące różne rodzaje temperatury
		FuzzySet cold = new FuzzySet();
		FuzzySet comfort = new FuzzySet();
		FuzzySet hot = new FuzzySet();

		public TemperatureInput() {

			// Utworzenie gaussowskich funkcji przynależności
			// (pierwszy argument: środek, drugi: odchylenie standardowe funkcji)

			cold.newGaussian(0, 8); // zbiór reprezentujący zimno
			comfort.newGaussian(22, 5); // zbiór reprezentujący komfortową temperaturę
			hot.newGaussian(32, 5); // zbiór reprezentujący gorącą
		}

		public void fuzzify(double t) {

			// Obliczenie stopni przynależności temperatury do każdego zbioru rozmytego
			coldLevel = cold.getMembership(t);
			comfortLevel = comfort.getMembership(t);
			hotLevel = hot.getMembership(t);

			// Wypisanie wartości stopni przynależności
			System.out.println("\nStopień przynależności do 'cold': " + coldLevel);
			System.out.println("Stopień przynależności do 'comfort': " + comfortLevel);
			System.out.println("Stopień przynależności do 'hot': " + hotLevel);
		}
	}

	// Klasa odpowiedzialna za obliczenie siły chłodzenia

	// Schemat Mamdaniego:
	// - rozmycie wejścia (fuzzification)
	// - przycięcie zbiorów wyjściowych tNormą
	// - agregacja sNormą
	// - defuzyfikacja
	public static class CoolingController {

		public double calculate(TemperatureInput temp) {

			// TNorm określa operator logiczny AND (minimum)
			TNorm min = OperationCreator.newTNorm(TNorm.TN_MINIMUM);

			// SNorm określa operator OR do łączenia zbiorów (maksimum)
			SNorm max = OperationCreator.newSNorm(SNorm.SN_MAXIMUM);

			// Trzy poziomy intensywności chłodzenia
			FuzzySet low = new FuzzySet();
			low.newGaussian(20, 5); // niskie chłodzenie

			FuzzySet mid = new FuzzySet();
			mid.newGaussian(50, 5); // średnie chłodzenie

			FuzzySet high = new FuzzySet();
			high.newGaussian(80, 5); // mocne chłodzenie

			// Przycinanie zbioru 'high' na podstawie przynależności do 'hot'
			FuzzySet hHigh = new FuzzySet();
			if (temp.hotLevel > 0) {
				high.processSetAndMembershipWithNorm(temp.hotLevel, min);
				hHigh = high;
			}

			// Przycinanie zbioru 'mid' na podstawie przynależności do 'comfort'
			FuzzySet hMid = new FuzzySet();
			if (temp.comfortLevel > 0) {
				mid.processSetAndMembershipWithNorm(temp.comfortLevel, min);
				hMid = mid;
			}

			// Przycinanie zbioru 'low' na podstawie przynależności do 'cold'
			FuzzySet hLow = new FuzzySet();
			if (temp.coldLevel > 0) {
				low.processSetAndMembershipWithNorm(temp.coldLevel, min);
				hLow = low;
			}

			// Łączenie powyższych zbiorów w jedną funkcję (agregacja)
			FuzzySet out = new FuzzySet();
			FuzzySet.processSetsWithNorm(out, hLow, hMid, max);
			FuzzySet.processSetsWithNorm(out, out, hHigh, max);

			// Wyostrzanie (defuzyfikacja)
			return out.DeFuzzyfy();
		}
	}

	// Klasa sterująca nawiewem: to samo co w CoolingController
	public static class VentController {

		public double calculate(TemperatureInput temp) {

			// TNorm określa operator logiczny AND (minimum)
			TNorm min = OperationCreator.newTNorm(TNorm.TN_MINIMUM);

			// SNorm określa operator OR do łączenia zbiorów (maksimum)
			SNorm max = OperationCreator.newSNorm(SNorm.SN_MAXIMUM);

			// Trzy poziomy intensywności nawiewu
			FuzzySet low = new FuzzySet();
			low.newGaussian(10, 5); // niski nawiew

			FuzzySet mid = new FuzzySet();
			mid.newGaussian(30, 5); // średni nawiew

			FuzzySet high = new FuzzySet();
			high.newGaussian(60, 5); // mocny nawiew

			// Przycinanie zbioru 'high' na podstawie przynależności do 'hot'
			FuzzySet hHigh = new FuzzySet();
			if (temp.hotLevel > 0) {
				high.processSetAndMembershipWithNorm(temp.hotLevel, min);
				hHigh = high;
			}

			// Przycinanie zbioru 'mid' na podstawie przynależności do 'comfort'
			FuzzySet hMid = new FuzzySet();
			if (temp.comfortLevel > 0) {
				mid.processSetAndMembershipWithNorm(temp.comfortLevel, min);
				hMid = mid;
			}

			// Przycinanie zbioru 'low' na podstawie przynależności do 'cold'
			FuzzySet hLow = new FuzzySet();
			if (temp.coldLevel > 0) {
				low.processSetAndMembershipWithNorm(temp.coldLevel, min);
				hLow = low;
			}

			// Łączenie powyższych zbiorów w jedną funkcję (agregacja)
			FuzzySet out = new FuzzySet();
			FuzzySet.processSetsWithNorm(out, hLow, hMid, max);
			FuzzySet.processSetsWithNorm(out, out, hHigh, max);

			// Wyostrzanie (defuzyfikacja)
			return out.DeFuzzyfy();
		}
	}
}
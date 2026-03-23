package com.fss;

import fuzzlib.FuzzySet;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.*;

public class MainFuzzlibTest {
	

	public static void main(String[] args) {
		FuzzySet fs = new FuzzySet();
		FuzzySet fs2 = new FuzzySet();
		FuzzySet fs3 = new FuzzySet();
		FuzzySet fs4 = new FuzzySet();
		
		fs.addPoint(-2.1,0.1);
		fs.addPoint(0.0,4.0);
		fs.addPoint(2.0,0.0);


		fs2.newGaussian(0.0,0.2);
		fs3.newTrapezium(0.0, 3.0, 2.0);
		
		System.out.println(fs3);
		fs3.fuzzyfy(-1);
		System.out.println(fs3);

		FuzzySet.processSetsWithNorm(fs2, fs3, fs4, null);
		System.out.println(fs3 + "\n");

		
		FuzzySet fs5 = new FuzzySet();
		FuzzySet fs6 = new FuzzySet();

		fs5.newTriangle(0.0, 1.0);
		fs6.newTrapezium(0.0, 3.0, 1.0);

		fs5.fuzzyfy(-1);
		
		// ===
		
		
		System.out.println(fs5);
		System.out.println(fs6);
		
		Norm op = OperationCreator.newNorm(TNorm.TN_MINIMUM);
		// TNMin op1 = new TNMin(); //to samo co Norm op
		
		FuzzySet.processSetsWithNorm(fs4, fs5, fs6, op);

		System.out.println("\n" + fs4);
		
		fs4.PackFlatSections();
		System.out.println(fs4);

		
		// ===
		
		
		fs.newTriangle(0, 1.0);
		fs2.newGaussian(0, 2.0);
		
		
		fs.fuzzyfy(-5.0);
		
		Norm op2 = OperationCreator.newNorm(SNorm.SN_MAXIMUM);

		System.out.println(fs);
		System.out.println(fs2);
		FuzzySet.processSetsWithNorm(fs4, fs, fs2, op2);
		System.out.println(fs4);
		System.out.println(fs4.getMax_membership().x);

		System.out.print("\nKoniec programu.");
		
		// Powrót do zad. 2 => Jako ćwiczenie do użycia tej biblio wykorzystać ją w 1 zadaniu
		// 1. Zrobić prostą definicje zbiorów
		// 2. Prosty klasyfikator i budowa zbiorów ze średnimi
		// 3. To co w tamtym tyg
	}

}

package com.fss;

import fuzzlib.*;
import fuzzlib.norms.*;
import fuzzlib.reasoning.*;

import java.util.Scanner;

public class MainReasoningSystemFun {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.print("Podaj temperaturę: ");
        double temp = scan.nextDouble();

        // =====================
        // KONFIGURACJA SYSTEMU
        // =====================
        SystemConfig config = new SystemConfig();
        config.setInputWidth(1);
        config.setOutputWidth(2); // cooling + vent
        config.setNumberOfPremiseSets(3);
        config.setNumberOfConclusionSets(6); // 3 cooling + 3 vent

        config.setAndOperationType(TNorm.TN_MINIMUM);
        config.setOrOperationType(SNorm.SN_MAXIMUM);
        config.setImplicationType(TNorm.TN_MINIMUM);
        config.setConclusionAgregationType(SNorm.SN_MAXIMUM);

        config.setDefuzzyfication(DefuzMethod.DF_COG);
        config.setAutoDefuzzyfication(true);

        ReasoningSystem rs = new ReasoningSystem(config);

        // =====================
        // ZMIENNE
        // =====================
        rs.getInputVar(0).id = "temp";

        rs.getOutputVar(0).id = "cooling";
        rs.getOutputVar(1).id = "vent";

        // =====================
        // ZBIORY WEJŚCIA
        // =====================
        FuzzySet cold = new FuzzySet("cold", "");
        cold.newGaussian(0, 8);

        FuzzySet comfort = new FuzzySet("comfort", "");
        comfort.newGaussian(22, 5);

        FuzzySet hot = new FuzzySet("hot", "");
        hot.newGaussian(32, 5);

        rs.addPremiseSet(cold);
        rs.addPremiseSet(comfort);
        rs.addPremiseSet(hot);

        // =====================
        // ZBIORY WYJŚCIA - CHŁODZENIE
        // =====================
        FuzzySet lowC = new FuzzySet("lowC", "");
        lowC.newGaussian(20, 5);

        FuzzySet midC = new FuzzySet("midC", "");
        midC.newGaussian(50, 5);

        FuzzySet highC = new FuzzySet("highC", "");
        highC.newGaussian(80, 5);

        // =====================
        // ZBIORY WYJŚCIA - NAWIEW
        // =====================
        FuzzySet lowV = new FuzzySet("lowV", "");
        lowV.newGaussian(10, 5);

        FuzzySet midV = new FuzzySet("midV", "");
        midV.newGaussian(30, 5);

        FuzzySet highV = new FuzzySet("highV", "");
        highV.newGaussian(60, 5);

        // dodajemy wszystkie zbiory wyjścia
        rs.addConclusionSet(lowC);
        rs.addConclusionSet(midC);
        rs.addConclusionSet(highC);
        rs.addConclusionSet(lowV);
        rs.addConclusionSet(midV);
        rs.addConclusionSet(highV);

        // =====================
        // REGUŁY (jak w ReasoningSystemTest)
        // =====================
        try {

            // ===== CHŁODZENIE =====
            rs.addRule(1, 1);
            rs.addRuleItem("temp", "cold");
            rs.addRuleConclusion("cooling", "lowC");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "comfort");
            rs.addRuleConclusion("cooling", "midC");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "hot");
            rs.addRuleConclusion("cooling", "highC");

            // ===== NAWIEW =====
            rs.addRule(1, 1);
            rs.addRuleItem("temp", "cold");
            rs.addRuleConclusion("vent", "lowV");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "comfort");
            rs.addRuleConclusion("vent", "midV");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "hot");
            rs.addRuleConclusion("vent", "highV");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // =====================
        // OBLICZENIA
        // =====================
        rs.setInput(0, temp);
        rs.Process();

        double cooling = rs.getOutputVar(0).outset.DeFuzzyfy();
        double vent = rs.getOutputVar(1).outset.DeFuzzyfy();

        // =====================
        // WYNIKI
        // =====================
        System.out.println("\n--- WYNIKI ---");
        System.out.println("Chłodzenie: " + cooling + "%");
        System.out.println("Nawiew: " + vent + "%");

        scan.close();
    }
}
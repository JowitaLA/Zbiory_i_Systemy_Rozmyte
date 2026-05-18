package com.fss;

import fuzzlib.*;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.*;
import fuzzlib.reasoning.*;

import java.util.Scanner;

public class MainReasoningSystem {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.print("Podaj temperaturę: ");
        double temp = scan.nextDouble();

        // =====================
        // KONFIGURACJA SYSTEMU
        // =====================
        SystemConfig config = new SystemConfig();
        config.setInputWidth(1);          // 1 wejście (temperatura)
        config.setOutputWidth(2);         // 2 wyjścia: chłodzenie + nawiew
        config.setNumberOfPremiseSets(3); // cold, comfort, hot
        config.setNumberOfConclusionSets(3);

        config.setAndOperationType(TNorm.TN_MINIMUM);
        config.setOrOperationType(SNorm.SN_MAXIMUM);
        config.setImplicationType(TNorm.TN_MINIMUM);
        config.setConclusionAgregationType(SNorm.SN_MAXIMUM);

        config.setDefuzzyfication(DefuzMethod.DF_COG);
        config.setAutoDefuzzyfication(true);

        ReasoningSystem rs = new ReasoningSystem(config);

        // =====================
        // WEJŚCIE
        // =====================
        rs.getInputVar(0).id = "temp";

        // =====================
        // WYJŚCIA
        // =====================
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
        FuzzySet lowCool = new FuzzySet("lowC", "");
        lowCool.newGaussian(20, 5);

        FuzzySet midCool = new FuzzySet("midC", "");
        midCool.newGaussian(50, 5);

        FuzzySet highCool = new FuzzySet("highC", "");
        highCool.newGaussian(80, 5);

        rs.addConclusionSet(lowCool);
        rs.addConclusionSet(midCool);
        rs.addConclusionSet(highCool);

        // =====================
        // REGUŁY DLA CHŁODZENIA
        // =====================
        try {
            rs.addRule(1, 1);
            rs.addRuleItem("temp", "cold");
            rs.addRuleConclusion("cooling", "lowC");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "comfort");
            rs.addRuleConclusion("cooling", "midC");

            rs.addRule(1, 1);
            rs.addRuleItem("temp", "hot");
            rs.addRuleConclusion("cooling", "highC");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // =====================
        // DRUGI SYSTEM - NAWIEW
        // =====================
        ReasoningSystem ventSystem = new ReasoningSystem(config);

        ventSystem.getInputVar(0).id = "temp";
        ventSystem.getOutputVar(0).id = "vent";

        // Zbiory wyjścia dla nawiewu
        FuzzySet lowVent = new FuzzySet("lowV", "");
        lowVent.newGaussian(10, 5);

        FuzzySet midVent = new FuzzySet("midV", "");
        midVent.newGaussian(30, 5);

        FuzzySet highVent = new FuzzySet("highV", "");
        highVent.newGaussian(60, 5);

        ventSystem.addPremiseSet(cold);
        ventSystem.addPremiseSet(comfort);
        ventSystem.addPremiseSet(hot);

        ventSystem.addConclusionSet(lowVent);
        ventSystem.addConclusionSet(midVent);
        ventSystem.addConclusionSet(highVent);

        try {
            ventSystem.addRule(1, 1);
            ventSystem.addRuleItem("temp", "cold");
            ventSystem.addRuleConclusion("vent", "lowV");

            ventSystem.addRule(1, 1);
            ventSystem.addRuleItem("temp", "comfort");
            ventSystem.addRuleConclusion("vent", "midV");

            ventSystem.addRule(1, 1);
            ventSystem.addRuleItem("temp", "hot");
            ventSystem.addRuleConclusion("vent", "highV");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // =====================
        // OBLICZENIA
        // =====================
        rs.setInput(0, temp);
        rs.Process();

        ventSystem.setInput(0, temp);
        ventSystem.Process();

        double cooling = rs.getOutputVar(0).outset.DeFuzzyfy();
        double vent = ventSystem.getOutputVar(0).outset.DeFuzzyfy();

        // =====================
        // WYNIKI
        // =====================
        System.out.println("\n--- WYNIKI ---");
        System.out.println("Chłodzenie: " + cooling + "%");
        System.out.println("Nawiew: " + vent + "%");

        scan.close();
    }
}
package project;

import java.util.Scanner;

import project.classes.CongruenceClosureAlgorithm;
import project.preprocessing.FormulaReader;
import project.preprocessing.TermsParser;

public class HelloWorld {

    public static void main(String[] args) {
        FormulaReader FR = new FormulaReader(false);
        //Scanner S = new Scanner(System.in);
        //System.out.println("Please, enter the file name in input folder in order to analyze:");
        //S.close();
        //String fileName = S.nextLine();
        String fileName = "prova.txt";
        String formula = FR.readFormulaFromFile(fileName);
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,false);
        if (CCA.compute()){
            System.out.print("END");
            System.out.println(CCA.nodes);
            System.out.println("SAT");
        } else {
            System.out.print("END");
            System.out.println(CCA.nodes);
            System.out.println("UNSAT");
        }
    }
}
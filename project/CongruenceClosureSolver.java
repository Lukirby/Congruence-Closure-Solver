package project;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;

import project.classes.CongruenceClosureAlgorithm;
import project.classes.Options;
import project.preprocessing.FormulaReader;
import project.preprocessing.LogicParser;
import project.preprocessing.Regex;
import project.preprocessing.TermsParser;



public class CongruenceClosureSolver {

    public static Path totalPath;

    public static boolean sat;

    public static String openFile(String input){
        String fileName = input.trim().split(Regex.optionRegex)[0];
        System.out.println("file Name: "+fileName);
        FormulaReader FR = new FormulaReader(true);
        String formula = FR.readFormulaFromFile(fileName);
        totalPath = FR.totalPath;
        return formula;
    }

    public static Options parseOptions(String input){
        Options O = new Options();

        String[] args = input.split(Regex.optionRegex);

        for (int i=0; i<args.length; i++){
            switch (args[i]) {
                case "v":
                    O.setVerbose(true);
                    break;
                case "f":
                    O.setForbiddenSet(true);
                    break;
                case "e":
                    O.setEuristicUnion(true);
                    break;
                case "r":
                    O.setRecursiveFind(true);
                    break;
                default:
                    break;
            }
        }

        return O;
    }

    public static void writeOutput(){
        String result;
        if (sat){
            result = "SAT";
        } else {
            result = "UNSAT";
        }
        
        try {
            // Create a FileWriter object to write to the file
            FileWriter fw = new FileWriter(totalPath.toString().replace("input", "output"));
            
            // Write the string content to the file
            fw.write(result);
            
            // Close the FileWriter to release system resources
            fw.close();
        } catch (IOException e) {
            // Handle exceptions that might occur during file operations
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static boolean solve(String formula, Options opt){
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,opt,Level.SEVERE);
        sat = CCA.compute();
        return sat;
    }

    // solve F = F1 or F2 .... or Fn
    // return true if exist i s.t. Fi is SAT.
    public static boolean solveInput(String input){
        String formulaInput = openFile(input);

        Options opt = parseOptions(input);

        LogicParser LP = new LogicParser(formulaInput, Level.SEVERE);

        for (String formula: LP.formulaList){
            solve(formula, opt);
            if (sat){
                System.out.print(formula);
                break;
            }
        }
        return sat;
    }

    public static void main(String[] args) {
        
        System.out.println("Please, enter the file name in input folder in order to analyze:");
        Scanner S = new Scanner(System.in);
        String input = S.nextLine();
        S.close();

        solveInput(input);

        writeOutput();

    }
}
package project;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;

import project.classes.CongruenceClosureAlgorithm;
import project.classes.Options;
import project.preprocessing.FormulaReader;
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

    public static void solve(String formula, Options opt){
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,opt,Level.SEVERE);
        String result;
        sat = CCA.compute();
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

    public static boolean solveInput(String input){
        String formula = openFile(input);

        Options opt = parseOptions(input);

        solve(formula, opt);

        return sat;
    }

    public static void main(String[] args) {
        
        System.out.println("Please, enter the file name in input folder in order to analyze:");
        Scanner S = new Scanner(System.in);
        String input = S.nextLine();
        S.close();

        solveInput(input);

    }
}
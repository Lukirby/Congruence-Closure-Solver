package project;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;

import project.classes.CongruenceClosureAlgorithm;
import project.classes.Options;
import project.preprocessing.FormulaReader;
import project.preprocessing.Generator;
import project.preprocessing.LogicParser;
import project.preprocessing.Regex;
import project.preprocessing.SMTLIBParser;
import project.preprocessing.TermsParser;



public class CongruenceClosureSolver {

    public Path totalPath;

    public boolean sat;

    public String output = "";

    public long time;

    public CongruenceClosureAlgorithm CCA;

    public String openFile(String input){
        String fileName = input.trim().split(Regex.optionRegex)[0];
        System.out.println("file Name: "+fileName);
        if (fileName.endsWith(".smt2")){
            SMTLIBParser SP = new SMTLIBParser();
            String content = SP.readContentFromFile(fileName);
            SP.writeInputFile(content);
            fileName = fileName.replace(".smt2", ".txt");
        } else 
        if (fileName.endsWith(".properties")){
            Generator G = new Generator();
            G.readFileGenerator(fileName);
            G.generateFile();
            fileName = fileName.replace(".properties", G.getNewExtention());
        }
        FormulaReader FR = new FormulaReader(true);
        String formula = FR.readFormulaFromFile(fileName);
        totalPath = FR.totalPath;
        return formula;
    }

    public Options parseOptions(String input){
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

    public void writeOutput(){
        String result;
        if (sat){
            result = "SAT";
        } else {
            result = "UNSAT";
        }

        this.output += result;

        this.output += "\n\nElapsed Time: " + ((double)time)/1000 + " seconds";

        try {
            // Create a FileWriter object to write to the file
            FileWriter fw = new FileWriter(totalPath.toString().replace("input", "output"));
            
            // Write the string content to the file
            fw.write(output);
            
            // Close the FileWriter to release system resources
            fw.close();
        } catch (IOException e) {
            // Handle exceptions that might occur during file operations
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public boolean solve(String formula, Options opt){
        TermsParser TP = new TermsParser(formula, false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,opt,Level.SEVERE);
        sat = CCA.compute();
        this.output += CCA.DAG.output;
        return sat;
    }

    // solve F = F1 or F2 .... or Fn
    // return true if exist i s.t. Fi is SAT.
    public boolean solveInput(String input){
        this.output = "";

        input = TermsParser.cleanFormula(input);

        String formulaInput = openFile(input);

        output+="Formula: \n"+formulaInput+"\n";

        Options opt = parseOptions(input);

        LogicParser LP = new LogicParser(formulaInput, Level.SEVERE);

        output+=LP.getDNF();

        output+="\n";

        output+=LP.getListOfCubes();

        int i = 1;


        long startTime = System.currentTimeMillis();
        for (String formula: LP.formulaList){
            output+="\nSolving: \n"+i+") "+formula+"\n\n";
            solve(formula, opt);
            if (sat){
                break;
            }
            i++;
        }
        long endTime = System.currentTimeMillis();
        time = endTime - startTime;
        return sat;
    }

    public CongruenceClosureSolver(){}

    public static void main(String[] args) {
        
        System.out.println("Please, enter the file name in input folder in order to analyze:");
        Scanner S = new Scanner(System.in);
        String input = S.nextLine();
        S.close();

        CongruenceClosureSolver CCS = new CongruenceClosureSolver();

        CCS.solveInput(input);

    }
}
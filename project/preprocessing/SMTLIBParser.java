package project.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import project.debug.Debug;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SMTLIBParser {

    public Debug logger;

    public String fileContent;

    private Path absPath;

    public Path totalPath;

    private String declareFun = "(declare-fun";

    private String declareAssert = "(assert";

    private String declareSource = "(set-info :source ";

    private String declareEndSource = "|)";

    private String declareStatus = "(set-info :status ";

    public String readDeclareFun(String line){
        Pattern pattern = Pattern.compile(Regex.declareFunRegex);
        Matcher matcher = pattern.matcher(line);
        String equality = "";
        if (!matcher.find()) {
            logger.severe("No function declarations found in the input file.");
        } else {
            String functionName = matcher.group(1);
            String arguments = matcher.group(2).trim();
            String returnType = matcher.group(3);
            if (arguments.isEmpty()) {
                // No arguments: Format as `f1 = S1`
                equality = functionName + " = " + returnType;
            } else {
                // Arguments exist: Format as `f3(S2,S3,S4,S5,S6) = S1`
                String formattedArguments = arguments.replace(" ", ",");
                equality = functionName + "(" + formattedArguments + ") = " + returnType;
            }
        }
        return equality;
    }
    
    public static String readAssert(String formula) {
        formula = formula.trim();

        if (formula.startsWith("(and")) {
            // Process 'and'
            String[] subformulas = splitTopLevel(formula.substring(4, formula.length() - 1).trim());
            List<String> transformedSubformulas = new ArrayList<>();
            for (String subformula : subformulas) {
                transformedSubformulas.add(readAssert(subformula));
            }
            return "[" + String.join(" "+PropLogic.conjuction+" ", transformedSubformulas) + "]";
        } else if (formula.startsWith("(or")) {
            // Process 'or'
            String[] subformulas = splitTopLevel(formula.substring(3, formula.length() - 1).trim());
            List<String> transformedSubformulas = new ArrayList<>();
            for (String subformula : subformulas) {
                transformedSubformulas.add(readAssert(subformula));
            }
            return "[" + String.join(" "+PropLogic.disjunction+" ", transformedSubformulas) + "]";
        } else if (formula.startsWith("(not")) {
            // Process 'not'
            String subformula = formula.substring(4, formula.length() - 1).trim();
            return PropLogic.negation+"[" + readAssert(subformula) + "]";
        } else if (formula.startsWith("(=")) {
            // Process '='
            String[] operands = formula.substring(2, formula.length() - 1).trim().split("\\s+");
            return operands[0] + " "+EqSignature.equality+" " + operands[1];
        } else {
            throw new IllegalArgumentException("Unsupported formula: " + formula);
        }
    }

    // Utility method to split top-level sub-expressions
    public static String[] splitTopLevel(String input) {
        List<String> parts = new ArrayList<>();
        int openParens = 0;
        StringBuilder currentPart = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == '(') openParens++;
            if (c == ')') openParens--;
            if (openParens == 0 && c == ' ') {
                parts.add(currentPart.toString());
                currentPart.setLength(0);
            } else {
                currentPart.append(c);
            }
        }
        if (currentPart.length() > 0) {
            parts.add(currentPart.toString());
        }
        return parts.toArray(new String[0]);
    }

    public String readContentFromFile(String fileName){
        logger.fine("File Path Received: "+fileName);
        String absString = absPath.toString();
        logger.fine("Absolute Path"+absString);
        this.totalPath = Paths.get(absString,fileName.trim());
        boolean sourceMode = false;
        try (BufferedReader br = new BufferedReader(new FileReader(totalPath.toString()))) {
            logger.fine("Total Path: "+totalPath.toString());
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(declareFun)){
                    //content.append(readDeclareFun(line)).append("\n");
                    continue;
                } else 
                if (line.startsWith(declareSource)){
                    sourceMode = true;
                } else
                if (line.endsWith(declareEndSource) && sourceMode){
                    line = line.replace(declareEndSource, "").trim();
                    content.append("_ "+line).append("\n");
                    sourceMode = false;
                } else
                if (line.startsWith(declareStatus)){
                    line = line.replace(declareStatus, "").replace(")", "").trim().toUpperCase();
                    content.append("_ result "+line).append("\n\n");
                } else
                if (line.startsWith(declareAssert)){
                    line = line.replace(declareAssert, "");
                    line = line.substring(0, line.length()-1).trim();
                    content.append(readAssert(line)).append("\n");
                } else
                if (sourceMode){
                    content.append("_ "+line).append("\n");
                }
            }
            this.fileContent = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.fine("File Content obtained: "+this.fileContent);
        return this.fileContent;
    }

    public void writeInputFile(String content){
        try {
            // Create a FileWriter object to write to the file
            FileWriter fw = new FileWriter(totalPath.toString().replace("smtlib_input", "input").replace(".smt2",".txt"));
            
            // Write the string content to the file
            fw.write(content);
            
            // Close the FileWriter to release system resources
            fw.close();
        } catch (IOException e) {
            // Handle exceptions that might occur during file operations
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public SMTLIBParser(Boolean log){
        if (log){
            this.logger = new Debug(this.getClass(), Level.FINE);
        } else {
            this.logger = new Debug(this.getClass(), Level.SEVERE);
        }
        this.absPath = Paths.get("").toAbsolutePath();
        if (!absPath.toString().matches("project$")){
            this.absPath = Paths.get(this.absPath.toString(),"project","smtlib_input");
        } else {
            this.absPath = Paths.get(this.absPath.toString(),"smtlib_input");
        }
        this.logger.fine("Absolute Path: "+this.absPath.toString());
    }

    public SMTLIBParser(){
        this(false);
    }

    public static void main(String[] args) {
        // Example input
        /* String formula = "(and (or (and (= x0 y0) (= y0 x1)) (and (= x0 z0) (= z0 x1))) (not (= x0 x1)))";

        // Parse and transform the formula
        String transformed = readAssert(formula);
        System.out.println(transformed); */
        SMTLIBParser SmtLib = new SMTLIBParser(true);
        String content = SmtLib.readContentFromFile("eq_diamond1.smt2");
        SmtLib.writeInputFile(content);
    }

}

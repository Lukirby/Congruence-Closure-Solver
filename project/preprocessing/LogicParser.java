package project.preprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.classes.DNFTree;
import project.debug.Debug;

public class LogicParser {
    
    Debug logger;

    public ArrayList<String> formulaList = new ArrayList<String>();

    //select(store(a,i,v),j) = w => i=j & v = w | i!=j & select(v,j) = w 
    /**
     * Splits a formula containing a select-over-store operation into two sub-formulas.
     * 
     * The method identifies the pattern of a select operation over a store operation
     * in the given formula and splits it into two sub-formulas based on the following logic: <br>
     * <br> <br>
     * 1. select(store(a, i, v), j) = w => i = j & v = w <br>
     * <br>
     * 2. select(store(a, i, v), j) = w => i != j & select(a, j) = w <br>
     * <br>
     * 
     * @param formula The input formula containing the select-over-store operation.
     * @return An array of two sub-formulas derived from the input formula. If the pattern
     *         is not found, an empty array is returned.
     */
    public String[] splitSelectOverStore(String formula){
        Matcher M = Pattern.compile(ArraySignature.selectOverStore).matcher(formula);
        if (!M.find()){
            return new String[0];
        }
        int nPar = 2;
        int nArg = 0;
        StringBuilder firstBuilder = new StringBuilder();
        StringBuilder storeBuilder = new StringBuilder();
        StringBuilder secondBuilder = new StringBuilder();
        StringBuilder arrayBuilder = new StringBuilder();
        int i = M.end()+1;
        char[] charsFormula = formula.toCharArray();
        while(nPar>0){
            char c = charsFormula[i];
            switch (c) {
                case '(':
                    nPar++;
                    break;
                case ')':
                    nPar--;
                    break;
                case ',':
                    if (nPar<3 && nArg!=-1){
                        nArg++;
                    }
                    break;
                default:
                    break;
            }
            if(nArg==0 && nPar>0){
                arrayBuilder.append(c);
            }
            if (nArg==1 && nPar>0){
                firstBuilder.append(c);
            }
            if(nArg==2 && nPar>0){
                storeBuilder.append(c);
            }
            if(nArg==-1){
                secondBuilder.append(c);
            }
            if (nPar<2){
                nArg=-1;
            }
            i++;
        }
        String firstIndex = firstBuilder.substring(1).toString();
        String secondIndex = secondBuilder.substring(1,secondBuilder.length()-1).toString();
        String storeValue = storeBuilder.substring(1,storeBuilder.length()-1).toString();
        String arrayVariable = arrayBuilder.toString();
        logger.fine("First Index: "+firstIndex);
        logger.fine("Second Index: "+secondIndex);
        logger.fine("Store Value: "+storeValue);
        logger.fine("Array Variable: "+arrayVariable);
        // select(store(a,i,v),j) = w => i=j & v = w
        StringBuilder sb = new StringBuilder(formula);
        String[] subFormulas = new String[2];
        subFormulas[0] = sb.replace(M.start(), i, storeValue).toString();
        subFormulas[0] += Regex.inputRegex+firstIndex+EqSignature.equality+secondIndex;
        subFormulas[0] = TermsParser.cleanFormula(subFormulas[0]);
        // select(store(a,i,v),j) = w => i!=j & select(a,j) = w 
        sb = new StringBuilder(formula);
        subFormulas[1] = sb.replace(M.start(), i, ArraySignature.select+"("+arrayVariable+","+secondIndex+")").toString();
        subFormulas[1] += Regex.inputRegex+firstIndex+EqSignature.disequality+secondIndex;
        subFormulas[1] = TermsParser.cleanFormula(subFormulas[1]);
        return subFormulas;
    }

    /**
     * Transforms array theory formulas by repeatedly splitting formulas
     * containing select-over-store expressions until no more splits can be made.
     * 
     * The transformation is performed in-place on the formulaList field.
     */
    public void arrayTheoryFormulaTransformation(){
        boolean splitted = true;
        ArrayList<String> flCopy = new ArrayList<String>();
        while (splitted) {
            splitted = false;
            for (String F: this.formulaList){
                String[] splittedFormulas = splitSelectOverStore(F);
                if (splittedFormulas.length>0){
                    splitted = true;
                    flCopy.add(splittedFormulas[0].replace(";;",";"));
                    flCopy.add(splittedFormulas[1].replace(";;",";"));
                } else {
                    flCopy.add(F);
                }
            }
            this.formulaList = new ArrayList<>(flCopy);
            flCopy.clear();
        }
    }

    /**
     * Replaces implications and coimplications in the given logical formula with their 
     * corresponding propositional logic representations.
     *
     * @param formula the logical formula as a string
     * @return the formula with implications and coimplications replaced
     */
    public String castImplications(String formula){
        String S = formula;
        S = S.replace(Regex.coimplicationRegex, PropLogic.coimplication);
        S = S.replace(Regex.implicationRegex, PropLogic.implication);
        return S;
    }

    /**
     * Reverses the cast of implications and coimplications in the given logical formula.
     * 
     * This method replaces all occurrences of coimplications and implications in the 
     * input formula string with their corresponding regular expression representations.
     * 
     * @param formula the logical formula as a string where implications and coimplications 
     *                need to be reversed.
     * @return a new string with implications and coimplications replaced by their 
     *         regular expression equivalents.
     */
    public String reverseCastImplications(String formula){
        String S = formula;
        S = S.replace(PropLogic.coimplication, Regex.coimplicationRegex);
        S = S.replace(PropLogic.implication, Regex.implicationRegex);
        return S;
    }

    /**
     * Replaces all occurrences of the conjunction operator in the given formula
     * with the specified regular expression pattern.
     *
     * @param formula the input logical formula as a string
     * @return the modified formula with conjunctions replaced by the regex pattern
     */
    public String castInputOnConjunctions(String formula){
        String S = formula;
        S = S.replace(PropLogic.conjuction, Regex.inputRegex);
        return S;
    }

    /**
     * Replaces all occurrences of a specific regex pattern in the input formula with a conjunction.
     *
     * @param formula the input logical formula as a string
     * @return the modified formula with conjunctions replacing the specified regex pattern
     */
    public String castConjunctionsOnInput(String formula){
        String S = formula;
        S = S.replace(Regex.inputRegex,PropLogic.conjuction);
        return S;
    }

    /**
     * Formats the given formula string by cleaning it, removing a trailing regex pattern if present,
     * and adding spaces around semicolons.
     *
     * @param formula the formula string to be formatted
     * @return the formatted formula string
     */
    public String formatFormula(String formula){
        String S = TermsParser.cleanFormula(formula);
        if (S.endsWith(Regex.inputRegex)){
            S = S.substring(0, S.length()-1);
        }
        S = S.replace(";", " ; ");
        return S;
    }

    /**
     * Removes quantifiers from the given logical formula.
     *
     * @param formula the logical formula as a string
     * @return the formula with quantifiers removed
     */
    public String removeQuantifiers(String formula){
        String S = formula;
        S = S.replaceAll(Regex.quantifierRegex, "");
        return S;
    }

    /**
     * Converts a given logical formula into its Disjunctive Normal Form (DNF).
     * 
     * @param formula The logical formula to be converted.
     * @return An ArrayList of strings, where each string is a cube of the DNF.
     */
    public ArrayList<String> DNF(String formula){
        ArrayList<String> AL = new ArrayList<String>();
        if (formula.contains(Regex.inputRegex)){
            AL.add(formula);
        } else 
        if(!formula.isEmpty()){
            formula = castImplications(formula);
            DNFTree tree = DNFParser.parse(formula);
            formula = DNFTrasformer.transform(tree);
            formula = castInputOnConjunctions(formula);
            String[] subformulas = formula.split(Regex.disjunctionRegex);
            for (String f : subformulas){
                AL.add(f);
            }
        }
        return AL;
    }

    /**
     * Constructs a LogicParser object that processes a given logical formula.
     *
     * @param formula the logical formula to be parsed and processed
     * @param level the logging level for debugging purposes
     */
    public LogicParser(String formula, Level level){
        this.logger = new Debug(this, level);

        formula = TermsParser.cleanFormula(formula);

        formula = removeQuantifiers(formula);
            
        ArrayList<String> formulaDNF = DNF(formula);

        this.formulaList = new ArrayList<String>(formulaDNF);

        if (TermsParser.checkTheoryType(formula, Regex.arrayRegex)){
            this.arrayTheoryFormulaTransformation();
        }

        this.formulaList.replaceAll(f -> formatFormula(f));

    }

    /**
     * Converts the formula list into its Disjunctive Normal Form (DNF) representation.
     * The DNF is a standardization of a logical formula which is a disjunction of conjunctions.
     * 
     * @return A string representing the formula list in DNF format.
     */
    public String getDNF(){
        String dnf = "DNF:\n";
        Iterator<String> it = this.formulaList.iterator();
        while(it.hasNext()){
            dnf+=castConjunctionsOnInput(it.next());
            if (it.hasNext()){
                dnf+="  "+PropLogic.disjunction+"  ";
            }
        }
        dnf+="\n";
        return dnf;
    }

    /**
     * Generates a string representation of the list of cubes (formulas) to check.
     * Each cube is prefixed with its index in the list.
     *
     * @return A string containing the list of cubes, each on a new line, prefixed with its index.
     */
    public String getListOfCubes(){
        String dnf = "List of Cubes to check:\n";
        int i = 1;
        for (String f : this.formulaList){
            dnf+=i+") "+f+"\n";
            i++;
        }
        dnf+="\n";
        return dnf;
    }
}

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

    public String castImplications(String formula){
        String S = formula;
        S = S.replace(Regex.coimplicationRegex, PropLogic.coimplication);
        S = S.replace(Regex.implicationRegex, PropLogic.implication);
        return S;
    }

    public String reverseCastImplications(String formula){
        String S = formula;
        S = S.replace(PropLogic.coimplication, Regex.coimplicationRegex);
        S = S.replace(PropLogic.implication, Regex.implicationRegex);
        return S;
    }

    public String castInputOnConjunctions(String formula){
        String S = formula;
        S = S.replace(PropLogic.conjuction, Regex.inputRegex);
        return S;
    }

    public String castConjunctionsOnInput(String formula){
        String S = formula;
        S = S.replace(Regex.inputRegex,PropLogic.conjuction);
        return S;
    }

    public String formatFormula(String formula){
        String S = TermsParser.cleanFormula(formula);
        if (S.endsWith(Regex.inputRegex)){
            S = S.substring(0, S.length()-1);
        }
        S = S.replace(";", " ; ");
        return S;
    }

    public String removeQuantifiers(String formula){
        String S = formula;
        S = S.replaceAll(Regex.quantifierRegex, "");
        return S;
    }

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
                System.out.println(f);
                AL.add(f);
            }
        }
        return AL;
    }

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

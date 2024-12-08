package project.preprocessing;

import project.classes.Node;
import project.classes.Theory;
import project.debug.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermsParser {
    
    public Debug logger;

    public int id_index = 0;

    public Theory theory = Theory.EQUALITY;

    public HashMap<String,Integer> SF = new HashMap<String,Integer>();

    public HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
    
    public ArrayList<Integer[]> equalities = new ArrayList<Integer[]>();

    public ArrayList<Integer[]> disequalities = new ArrayList<Integer[]>();

    private boolean isStringValid(String S){
        return !S.trim().isEmpty() && S != null;
    }

    private String[] predicateHandler(String S){
        String[] newLiteral = new String[3];
        if (S.startsWith(Regex.negation)){
            S = S.substring(1);
            newLiteral[2] = "!=";
        } else {
            newLiteral[2] = "=";
        }
        newLiteral[0] = Regex.functionPredicatePrefix.concat(S);
        newLiteral[1] = Regex.trueCostant;
        return newLiteral;
    }

    private String retriveName(String function){
        if(!function.matches(Regex.termRegex)){
            logger.severe("Invalid Name for term ".concat(function));
        }
        int parenIndex = function.indexOf('(');
        String name;
        if (parenIndex != -1) {
            name = function.substring(0, parenIndex).trim();
        } else {
            name = function;
        }
        this.logger.fine("Term Name: "+ name);
        return name;
    }

    public String[] splitArguments(String subformula){
        logger.fine("Subformula to Split: "+subformula);
        ArrayList<String> foundArgs = new ArrayList<String>();
        StringBuilder foundChars = new StringBuilder();
        char[] charsArgs = subformula.toCharArray();
        String recostructed;
        int nested_arg = 0;
        for (int i = 0; i < charsArgs.length; i++) {
            char c = charsArgs[i];
            if (c == ',' && nested_arg == 0){
                recostructed = foundChars.toString();
                logger.fine("Reconstructed: "+recostructed);
                if(isStringValid(recostructed)){
                    foundArgs.add(recostructed);
                }
                foundChars = new StringBuilder();
            } else {
                if (c == '('){
                    nested_arg++;
                }
                if (c == ')'){
                    nested_arg--;
                }
                foundChars.append(c);
            }
        }
        recostructed = foundChars.toString();
        logger.fine("Reconstructed: "+recostructed);
        if(isStringValid(recostructed)){
            foundArgs.add(recostructed);
        }
        return foundArgs.toArray(new String[0]);
    }

    private String[] retrinveArguments(String function){
        String[] args;
        int parenIndex = function.indexOf('(');
        if (parenIndex != -1){
            args = splitArguments(function.substring(parenIndex+1,function.length()-1).trim());
        } else {
            args = new String[0];
        }
        logger.fine("Term Arguments: "+Arrays.toString(args));
        return args;
    }

    private void checkListTheory(Node N){
        if (Pattern.matches(Regex.listTheoryTerms, N.name)){
            if (N.arity == 2){
                if (N.name.equals("cons")){
                    theory = Theory.LIST;
                    logger.fine("List Theory Term Found"); 
                } else {

                } 
            } else
            if (N.arity==1){
                if (!N.name.equals("cons")){
                    theory = Theory.LIST;
                    logger.fine("List Theory Term Found"); 
                } else {

                }
            } else {

            }
        }
    }

    private void checkTheory(Node N){
        checkListTheory(N);
    }

    public int makeNodes(String term,int parId){

        //assert isTermValid(term) : "Error: Invalid Term".concat(term);

        if (!isStringValid(term)) {
            logger.severe("Empty Term");
        }

        if (this.SF.containsKey(term)){
            logger.fine("Found node for term: "+term);
            int exist_id = this.SF.get(term);
            Node exist_node = this.nodes.get(exist_id);
            if (parId != -1){
                exist_node.ccpar.add(parId);
            }
            this.nodes.replace(exist_id, exist_node);
            return exist_id;
        } else {
            this.logger.fine("Creating node for term: "+term);
        }

        String name = this.retriveName(term);
        
        String[] arguments = this.retrinveArguments(term);

        int arity;

        int[] args;

        int id = this.id_index;

        Set<Integer> ccpar = new HashSet<Integer>();

        if (parId != -1){
            ccpar.add(parId);
        }

        this.id_index++;

        if (arguments.length > 0){
            //term is a function
            arity = arguments.length;

            args = new int[arity]; 

            for (int i = 0; i < args.length; i++) {
                if (!isStringValid(arguments[i])){
                    logger.severe("Empty Arguments: "+Arrays.toString(arguments));
                }
                args[i] = makeNodes(arguments[i],id);
            }
        } else {
            //term is a variable
            arity = 0;
            args = new int[0];
        }

        Node N = new Node(
            id,
            name,
            args,
            id,
            ccpar
        );

        this.nodes.put(id, N);
        this.SF.put(term, id);
        
        logger.fine("Node Created: "+ N.toString());
        
        return id;
    }

    public TermsParser(String formula, boolean log){

        Level level = log ? Level.FINE : Level.SEVERE;
        
        this.logger = new Debug(this,level);

        if (!isStringValid(formula)){
            logger.severe("Empty Formula");
        }
        
        //replace white spaces with empty spaces.
        formula = formula.replaceAll("\\s", "");
        formula = formula.replaceAll("\n", "");
        logger.fine("Forumula: "+formula);
        String[] literals = formula.split(Regex.inputRegex);
        for (String literal : literals) {
            logger.fine("Literal: "+literal);

            Pattern literalPattern = Pattern.compile(Regex.literalRegex);
            Matcher literalMatcher = literalPattern.matcher(literal);
            String operator;
            String[] terms = new String[2];
            if (literalMatcher.find()){
                operator = literalMatcher.group();
                terms = literal.split(operator);    
            } else {
                String[] newLiteral = predicateHandler(literal);
                logger.fine("Predicate Found");
                operator = newLiteral[2];
                terms[0] = newLiteral[0];
                terms[1] = newLiteral[1];
            }
            
            logger.fine("Operator Found: "+operator);

            if (terms.length!=2){
                logger.severe("Invalid Number of (Dis)Equalities in Literal ".concat(literal));
            }

            String leftTerm = terms[0];
            String rightTerm = terms[1];

            logger.fine("Left Term: "+leftTerm+" Right Term: "+rightTerm);

            Integer leftId = makeNodes(leftTerm,-1);
            Integer rightId = makeNodes(rightTerm,-1);

            checkTheory(this.nodes.get(leftId));
            checkTheory(this.nodes.get(rightId));

            Integer[] pair = {leftId,rightId};

            if (operator.equals(Regex.equalityPredicate)){
                this.equalities.add(pair);
            } else {
                this.nodes.get(leftId).forb.add(rightId);
                this.nodes.get(rightId).forb.add(leftId);
                this.disequalities.add(pair);
            }
            this.logger.fine("Forbidden Left: "+this.nodes.get(leftId).forb);
            this.logger.fine("Forbidden Right: "+this.nodes.get(rightId).forb);
        }
    };

    public TermsParser(String formula){
        this(formula,false);
    }

    public HashMap<Integer,Node> getNodes(){
        return this.nodes;
    }

    public ArrayList<Integer[]> getEqualities() {
        return this.equalities;
    }

    public ArrayList<Integer[]> getDisequalities() {
        return this.disequalities;
    }

    public static void main(String[] args) {
        //String S = "f(a,b) = a ; f(f(a,b)) != a";
        String R = "R(a,b) ; ~G(b,f(a,c))";
        TermsParser A = new TermsParser(R,true);
        System.out.println(A.SF.toString());
    }

}

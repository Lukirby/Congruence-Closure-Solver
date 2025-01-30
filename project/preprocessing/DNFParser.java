package project.preprocessing;

import project.classes.DNFTree;
import java.util.HashSet;
import java.util.Stack;
import java.util.Arrays;

public class DNFParser {
    // Set of valid operators
    private static final HashSet<String> OPERATORS = new HashSet<String>(Arrays.asList(PropLogic.operators));
    
    // Determine precedence of operators
    /**
     * Determines the precedence of a given logical operator.
     *
     * @param operator the logical operator as a String
     * @return an integer representing the precedence of the operator:
     *  - 3 for negation <br>
     *  <br>
     *  - 2 for conjunction <br>
     *  <br>
     *  - 1 for disjunction <br>
     * <br>
     *  - 0 for implication or coimplication <br>
     * <br>
     *  - -1 for any other operator
     * <br>
     */
    public static int precedence(String operator) {
        if(operator.equals(PropLogic.negation)){
            return 3;
        } else 
        if(operator.equals(PropLogic.conjuction)){
            return 2;
        } else
        if(operator.equals(PropLogic.disjunction)){
            return 1;
        } else
        if(operator.equals(PropLogic.implication) || operator.equals(PropLogic.coimplication)){
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Processes an operator by popping it from the operators stack and applying it to the operands
     * stack to form a DNFTree. If the operator is a negation, it applies it to a single operand.
     * Otherwise, it applies the operator to two operands.
     *
     * @param operators the stack of operators
     * @param operands the stack of DNFTree operands
     * @throws IllegalArgumentException if the expression is invalid (e.g., not enough operands)
     */
    public static void processOperator(Stack<String> operators, Stack<DNFTree> operands) {
        String operator = operators.pop();
        if (operator.equals(PropLogic.negation)) {
            if (operands.isEmpty()) {
                throw new IllegalArgumentException("Invalid expression");
            }
            DNFTree tree = new DNFTree(operator);
            DNFTree operand = operands.pop();
            tree.addChildren(operand);
            operands.push(tree);
        } else {
            if (operands.size() < 2) {
                throw new IllegalArgumentException("Invalid expression");
            }
            DNFTree tree = new DNFTree(operator);
            DNFTree right = operands.pop();
            DNFTree left = operands.pop();
            tree.addChildren(left);
            tree.addChildren(right);
            operands.push(tree);
        }
    }

    /**
     * Parses a given logical expression in Disjunctive Normal Form (DNF) and constructs a DNFTree.
     *
     * @param expression the logical expression in DNF to be parsed
     * @return a DNFTree representing the parsed logical expression
     * @throws IllegalArgumentException if the expression contains mismatched parentheses or is invalid
     */
    public static DNFTree parse(String expression) {
        Stack<DNFTree> operands = new Stack<DNFTree>();
        Stack<String> operators = new Stack<String>();
        StringBuilder variable = new StringBuilder();

        // Remove all immidiate double negations
        expression = expression.replaceAll("~~", "");

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            String S = Character.toString(c);

            if (Character.isWhitespace(c)) {
                continue; // Ignore whitespace
            } else if (c == PropLogic.leftParenthesis.charAt(0)) {
                operators.push(PropLogic.leftParenthesis);
            } else if (isLogicalOperator(S)){
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(S)) {
                    processOperator(operators, operands);
                }
                operators.push(S);
            } else if (c == PropLogic.rightParenthesis.charAt(0)) {
                while (!operators.isEmpty() && !operators.peek().equals(PropLogic.leftParenthesis)) {
                    processOperator(operators, operands);
                }
                if (operators.isEmpty() || !operators.pop().equals(PropLogic.leftParenthesis)) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
            } else {
                while (i < expression.length() && isVariable(S) && !Character.isWhitespace(c)) {
                    variable.append(expression.charAt(i));
                    i++;
                    if(i < expression.length()){
                        c = expression.charAt(i);
                        S = Character.toString(c);
                    }
                }
                operands.push(new DNFTree(variable.toString())); // Push variable
                variable.setLength(0);
                i--; // Move back one character
            }
        }

        while (!operators.isEmpty()) {
            processOperator(operators, operands);
        }

        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid expression "+operands.size());
        }

        return operands.pop();
    }

    /**
     * Checks if the given string represents a logical operator.
     *
     * @param S the string to check
     * @return true if the string is a logical operator, false otherwise
     */
    public static boolean isLogicalOperator(String S) {
        return OPERATORS.contains(Character.toString(S.charAt(0))) || OPERATORS.contains(S);
    }

    /**
     * Checks if the given string is a parenthesis.
     *
     * @param S the string to check
     * @return true if the string is a left or right parenthesis, false otherwise
     */
    public static boolean isParenthesis(String S) {
        return S.equals(PropLogic.leftParenthesis) || S.equals(PropLogic.rightParenthesis);
    }

    /**
     * Checks if the given string is a variable.
     * A string is considered a variable if it is not a logical operator
     * and not a parenthesis.
     *
     * @param S the string to check
     * @return true if the string is a variable, false otherwise
     */
    public static boolean isVariable(String S) {
        return !isLogicalOperator(S) && !isParenthesis(S);
    }



}

package project.preprocessing;

public class Regex {

    public static String commentRegex = "_";

    public static String optionRegex = "-";

    public static String listRegex = "("+ListSignature.cons+"|"
                                        +ListSignature.car+"|"
                                        +ListSignature.cdr+"|"
                                        +ListSignature.atom+")";

    public static String arrayRegex = "("+ArraySignature.select+"|"
                                         +ArraySignature.store+")";

    public static String inputRegex = ";";

    public static String disjunctionRegex = "\\"+PropLogic.disjunction;

    public static String implicationRegex = "->";

    public static String coimplicationRegex = "<->";

    public static String literalRegex = "(=|!=)";

    public static String termRegex = "\\w+\\(.*?\\)|\\w+|"+PropLogic.trueCostant;

    public static String argsRegex = ",";
}

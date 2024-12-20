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

    public static String literalRegex = "(=|!=)";

    public static String termRegex = "\\w+\\(.*?\\)|\\w+";

    public static String argsRegex = ",";
}

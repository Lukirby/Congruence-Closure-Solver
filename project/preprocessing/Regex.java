package project.preprocessing;

public class Regex {

    public static String trueCostant = "_";

    public static String negation = "~";

    public static String equalityPredicate = "=";

    public static String disequalityPredicate = "!=";

    public static String functionPredicatePrefix = "f_";

    public static String listTheoryTerms = "(cons|car|cdr|f_atom)";

    public static String inputRegex = ";";

    public static String literalRegex = "(=|!=)";

    public static String termRegex = "\\w+\\(.*?\\)|\\w+";

    public static String argsRegex = ",";
}

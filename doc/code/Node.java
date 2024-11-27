public class Node {
    /**
     * The unique identifier of the function or variable.
     * A costant integer.
     */
    public final Integer id;

    /**
     * The name of the function or variable.
     * A constant string.
     */
    public final String name;

    /**
     * The list of arguments associated with the function.
     * Empty for variables.
     * An ordered costant ArrayList.
     */
    private final Integer[] args;

    /**
     * The rappresentative id of the function or variable.
     * A mutable integer
     */
    public Integer find;

    /**
     * The set of congruence closure parent (ccpar).
     * An unordered mutable set of integers.
     */
    public Set<Integer> ccpar;    
}

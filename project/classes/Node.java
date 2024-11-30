package project.classes;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a function or variable with an identifier, name, arguments, a find value, 
 * and a set of ccpar (congruence closure parents).
 */
public class Node {

    /**
     * The unique identifier of the function or variable.
     * A costant integer.
     */
    public final int id;

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
    private final int[] args;

    /**
     * The rappresentative id of the function or variable.
     * A mutable integer
     */
    public int find;

    /**
     * The set of congruence closure parent (ccpar).
     * An unordered mutable set of integers.
     */
    public Set<Integer> ccpar;


    /** The set of terms that cannot be mergeto into a congruence class.
     *  An unordered mutable set of integers.
     */
    public Set<Integer> forb;

    /**
     * The arity of the function which is the number of 
     * arguments of a function. Zero for variables. 
     * A costant integer
     */
    public final int arity;

    /**
     * Constructs a new Node with the specified id, name, arguments, find value, and ccpar set.
     *
     * @param id    the [costant] unique identifier of the function or variable
     * @param name  the [costant] name of the function or variable
     * @param args  the [costant ordered] "list" of id arguments associated with the function. 
     * Empty for variables
     * @param find  the rappresentative id of the function or variable
     * @param ccpar the [not ordered] set of congruence closure parent (ccpar)
     * @param forbb the [not ordered] set of terms that cannot be mergeto into a congruence class
     */
    public Node(int id, String name, int[] args, int find, Set<Integer> ccpar) {
        this.id = id;
        this.name = name;
        this.args = args;
        this.find = find;
        this.ccpar = ccpar;
        this.arity = args.length;
        this.forb = new HashSet<Integer>();
    }

    /**
     * The "list" of arguments associated with the function.
     * Empty for variables.
     * An ordered costant array.
     * Method used to make the array immutable 
     */
    public int[] args() {
        return this.args;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node N = (Node) obj;
        return this.id == N.id;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("{\n\tid: ").append(this.id)
              .append(",\n\tname: ").append(this.name)
              .append(",\n\targs: ").append(java.util.Arrays.toString(this.args))
              .append(",\n\tfind: ").append(this.find)
              .append(",\n\tccpar: ").append(this.ccpar)
              .append(",\n\tarity: ").append(this.arity)
              .append("\n\tforb: ").append(this.forb)
              .append("\n}");
        return output.toString();
    }
    
}


package project.classes;

import java.util.ArrayList;

public class DNFTree {

    public DNFTree parent;

    public ArrayList<DNFTree> children;

    public String value;

    /**
     * Checks if the current node is a leaf node.
     * A leaf node is defined as a node with no children.
     *
     * @return true if the current node has no children, false otherwise.
     */
    public boolean isLeaf(){
        return children.isEmpty();
    }

    /**
     * Constructs a DNFTree with the specified value.
     *
     * @param value the value to be assigned to this DNFTree node
     */
    public DNFTree(String value) {
        this.value = value;
        this.children = new ArrayList<DNFTree>();
    }

    /**
     * Constructs a DNFTree with the specified value and children.
     *
     * @param value    the value of the DNFTree node
     * @param children the list of child DNFTree nodes
     */
    public DNFTree(String value, ArrayList<DNFTree> children) {
        this.value = value;
        this.children = children;
    }

    /**
     * Constructs a new DNFTree by performing a deep copy of the given DNFTree.
     *
     * @param tree the DNFTree to be copied
     */
    public DNFTree(DNFTree tree) {
        this.value = tree.value;
        this.children = new ArrayList<DNFTree>();
        for (DNFTree child : tree.children) {
            this.children.add(new DNFTree(child));
        }
    }

    /**
     * Retrieves the value stored in this DNFTree node.
     *
     * @return the value of this node as a String.
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the list of child DNFTree nodes.
     *
     * @return An ArrayList containing the child DNFTree nodes.
     */
    public ArrayList<DNFTree> getChildren() {
        return children;
    }

    /**
     * Adds a child DNFTree to the current DNFTree.
     * The child DNFTree's parent is set to the current DNFTree.
     * The child is then added to the list of children of the current DNFTree.
     *
     * @param child The DNFTree to be added as a child.
     * @return The child DNFTree that was added.
     */
    public DNFTree addChildren(DNFTree child) {
        child.parent = this;
        children.add(new DNFTree(child));
        return child;
    }

    public String toString() {
        if (isLeaf()) {
            return value;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            sb.append("(");
            for (DNFTree child : children) {
                sb.append(child.toString());
                sb.append(" ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        DNFTree rhs = (DNFTree) obj;
        if (!value.equals(rhs.value)) {
            return false;
        }
        if (children.size() != rhs.children.size()) {
            return false;
        }
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).equals(rhs.children.get(i))) {
                return false;
            }
        }
        return true;
    }
}

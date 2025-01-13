package project.classes;

import java.util.ArrayList;

public class DNFTree {

    public DNFTree parent;

    public ArrayList<DNFTree> children;

    public String value;

    public boolean isLeaf(){
        return children.isEmpty();
    }

    public DNFTree(String value) {
        this.value = value;
        this.children = new ArrayList<DNFTree>();
    }

    public DNFTree(String value, ArrayList<DNFTree> children) {
        this.value = value;
        this.children = children;
    }

    public DNFTree(DNFTree tree) {
        this.value = tree.value;
        this.children = new ArrayList<DNFTree>();
        for (DNFTree child : tree.children) {
            this.children.add(new DNFTree(child));
        }
    }

    public String getValue() {
        return value;
    }

    public ArrayList<DNFTree> getChildren() {
        return children;
    }

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

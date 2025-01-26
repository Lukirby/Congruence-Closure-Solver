package project.classes;

public class Options {

    public boolean verbose;

    public boolean recursiveFind;

    public boolean euristicUnion;

    public boolean forbiddenSet;

    public Options() {
        this.verbose=false;

        this.recursiveFind=false;

        this.euristicUnion=false;

        this.forbiddenSet=false;
    };

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setEuristicUnion(boolean euristicUnion) {
        this.euristicUnion = euristicUnion;
    }

    public void setForbiddenSet(boolean forbiddenSet) {
        this.forbiddenSet = forbiddenSet;
    }

    public void setRecursiveFind(boolean recursiveFind) {
        this.recursiveFind = recursiveFind;
    }

    @Override
    public String toString(){
        return "Options: [verbose=" + verbose + ", recursiveFind=" + recursiveFind + ", euristicUnion=" + euristicUnion + ", forbiddenSet=" + forbiddenSet + "]";
    }

}

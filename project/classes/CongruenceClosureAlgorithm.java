package project.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import project.debug.Debug;

public class CongruenceClosureAlgorithm {
    
    public CongruenceClosureDAG DAG;

    public HashMap<Integer,Node> nodes;
    
    public ArrayList<Integer[]> equalities;

    public ArrayList<Integer[]> disequalities;

    private Debug logger;

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Boolean log
    ){
        Level level = log ? Level.FINE : Level.SEVERE;
        this.logger = new Debug(this.getClass(), Level.OFF);
        this.nodes = nodes;
        this.equalities = equalities;
        this.disequalities = disequalities;
        this.DAG = new CongruenceClosureDAG(this.nodes);
    }

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities
    ){
        this(nodes, equalities, disequalities, false);
    }

    private boolean checkSatifiability(){
        for (Integer[] disequality : this.disequalities){
            if (this.DAG.FIND(disequality[0]) == this.DAG.FIND(disequality[1])){
                return false;
            }
        }
        return true;
    }

    public boolean compute(){
        for (Integer[] equality : this.equalities) {
            this.DAG.MERGE(equality[0],equality[1]);
        }
        return checkSatifiability();
    }

}

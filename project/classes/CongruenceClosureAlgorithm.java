package project.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

//import project.debug.Debug;

public class CongruenceClosureAlgorithm {
    
    public CongruenceClosureDAG DAG;

    public HashMap<Integer,Node> nodes;
    
    public ArrayList<Integer[]> equalities;

    public ArrayList<Integer[]> disequalities;

    public boolean verbose;

    //private Debug logger;

    public boolean forbiddenSet;

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Options opt,
        Level log
    ){
        //this.logger = new Debug(this.getClass(), log);
        this.verbose = opt.verbose;
        this.forbiddenSet=opt.forbiddenSet;
        this.nodes = nodes;
        this.equalities = equalities;
        this.disequalities = disequalities;
        this.DAG = new CongruenceClosureDAG(this.nodes,opt);
    }

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Options opt
    ){
        this(nodes, equalities, disequalities, opt, Level.OFF);
    }

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities
    ){
        this(nodes, equalities, disequalities, new Options(), Level.OFF);
    }

    private boolean checkSatifiability(){
        for (Integer[] disequality : this.disequalities){
            if(this.verbose){
                System.out.println("CHECK DISEQUALITY "+this.DAG.printNode(disequality[0])+" "+this.DAG.printNode(disequality[1]));
            }
            if (this.DAG.FIND(disequality[0]) == this.DAG.FIND(disequality[1])){
                return false;
            }
        }
        return true;
    }

    public boolean compute(){
        for (Integer[] equality : this.equalities) {
            this.DAG.MERGE(equality[0],equality[1]);
            if(this.DAG.unsatFlag){
                return false;
            }
        }
        if (this.forbiddenSet){
            return true;
        } else {
            return checkSatifiability();
        }   
    }

}

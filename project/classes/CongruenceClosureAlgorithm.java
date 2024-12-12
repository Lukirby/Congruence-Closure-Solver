package project.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import project.preprocessing.ListSignature;

//import project.debug.Debug;

public class CongruenceClosureAlgorithm {
    
    public CongruenceClosureDAG DAG;

    public HashMap<Integer,Node> nodes;
    
    public ArrayList<Integer[]> equalities;

    public ArrayList<Integer[]> disequalities;

    public Theory theory;

    public boolean verbose;

    //private Debug logger;

    public boolean forbiddenSet;

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Theory theory,
        Options opt,
        Level log
    ){
        //this.logger = new Debug(this.getClass(), log);
        this.verbose = opt.verbose;
        this.forbiddenSet=opt.forbiddenSet;
        this.nodes = nodes;
        this.equalities = equalities;
        this.disequalities = disequalities;
        this.theory = theory;
        this.DAG = new CongruenceClosureDAG(this.nodes,opt,log);
    }

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Theory theory,
        Options opt
    ){
        this(nodes, equalities, disequalities, theory, opt, Level.OFF);
    }

    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Theory theory
    ){
        this(nodes, equalities, disequalities, theory, new Options(), Level.OFF);
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

        //check if atom axiom is not conflicting.
        if (this.theory == Theory.LIST){
            this.nodes = this.DAG.getNodes();
            Node[] consNodes = (Node[]) this.nodes.values().stream()
                                            .filter(N -> N.name.equals(ListSignature.cons))
                                            .toArray(Node[]::new);
            for (Node U : this.nodes.values()){
                if(U.name.equals(ListSignature.atomFunction)){
                    for (Node CONS : consNodes){
                        if(this.verbose){
                            System.out.println("CHECK DISEQUALITY "+
                                                this.DAG.printNode(U.id)+" "+
                                                this.DAG.printNode(CONS.id));
                        }
                        if (this.DAG.FIND(CONS.id) == this.DAG.FIND(U.args()[0])){
                            return false;
                        }
                    }
                }
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

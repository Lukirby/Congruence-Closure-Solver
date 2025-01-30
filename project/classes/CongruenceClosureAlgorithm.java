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

    /**
     * Constructs a new CongruenceClosureAlgorithm instance.
     *
     * @param nodes A HashMap containing the nodes of the congruence closure algorithm.
     * @param equalities An ArrayList of Integer arrays representing the equalities.
     * @param disequalities An ArrayList of Integer arrays representing the disequalities.
     * @param theory The theory to be used in the congruence closure algorithm.
     * @param opt The options for configuring the algorithm.
     * @param log The logging level for debugging purposes.
     */
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

    /**
     * Constructs a new CongruenceClosureAlgorithm instance.
     * Log level is set to OFF.
     *
     * @param nodes A HashMap containing the nodes of the congruence closure algorithm.
     * @param equalities An ArrayList of Integer arrays representing the equalities.
     * @param disequalities An ArrayList of Integer arrays representing the disequalities.
     * @param theory The theory to be used in the congruence closure algorithm.
     * @param opt The options for configuring the algorithm.
     */
    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Theory theory,
        Options opt
    ){
        this(nodes, equalities, disequalities, theory, opt, Level.OFF);
    }

    /**
     * Constructs a new CongruenceClosureAlgorithm instance.
     * Log level is set to OFF.
     * Default options are used (everything is set to false).
     *
     * @param nodes A HashMap containing the nodes of the congruence closure algorithm.
     * @param equalities An ArrayList of Integer arrays representing the equalities.
     * @param disequalities An ArrayList of Integer arrays representing the disequalities.
     * @param theory The theory to be used in the congruence closure algorithm.
     */
    public CongruenceClosureAlgorithm(
        HashMap<Integer,Node> nodes,
        ArrayList<Integer[]> equalities,
        ArrayList<Integer[]> disequalities,
        Theory theory
    ){
        this(nodes, equalities, disequalities, theory, new Options(), Level.OFF);
    }

    /**
     * Checks the satisfiability of the current state of the congruence closure algorithm.
     *
     * @return true if the current state is satisfiable, false otherwise.
     */
    private boolean checkSatifiability(){
        for (Integer[] disequality : this.disequalities){
            if(this.verbose){
                this.DAG.writeMessage("CHECK DISEQUALITY "+this.DAG.printNode(disequality[0])+" "+this.DAG.printNode(disequality[1]));
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
                            this.DAG.writeMessage("CHECK DISEQUALITY "+
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

    /**
     * Computes the satisfiability of the given equalities and disequalities.
     * 
     * @return true if the equalities and disequalities are satisfiable, false otherwise.
     */
    public boolean compute(){
        if (this.forbiddenSet){
            for (Integer[] disequality : this.disequalities){
                if (this.DAG.FIND(disequality[0]) == this.DAG.FIND(disequality[1])){
                    this.DAG.writeMessage("CONFLICT: "+this.DAG.printNode(disequality[0])+" "+this.DAG.printNode(disequality[1]));
                    return false;
                }
            }
        } 
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

package project.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import project.debug.Debug;

public class CongruenceClosureDAG {

    private Debug logger;

    private HashMap<Integer,Node> nodes;

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, boolean log){

        Level level = log ? Level.FINE : Level.SEVERE;

        this.logger = new Debug(this.getClass(), level);

        this.nodes = nodes;

        logger.fine(nodes.toString());
    }

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes){
        this(nodes,true);
    }

    public Node NODE(int id){
        return this.nodes.get(id);
    }

    public int FIND(int id){
        Node N = NODE(id);
        if (N==null){
            logger.severe("Null Node for id "+id);
        }
        if (N.find == id){
            return id;
        } else {
            return FIND(N.find);
        }
    }

    public void UNION(int id1, int id2){
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        N1.find = N2.find;
        N2.ccpar.addAll(N1.ccpar);
        N1.ccpar = new HashSet<Integer>();
        /* for (Node N : nodes.values()) {
            if (N.find == N1.find){
                N.find = N1.id;
                nodes.replace(N.id, newN);
            }
        } */
    }

    public Set<Integer> CCPAR(int id){
        return new HashSet<Integer>(NODE(FIND(id)).ccpar);
    }

    private boolean congruenceCheck(Node N1, Node N2){
        if (!(N1.name.equals(N2.name)) || !(N1.arity.equals(N2.arity))){
            logger.fine("NO CONGRUENCE "+N1.name+" "+N2.name+" ");
            logger.fine("NO CONGRUENCE "+N1.arity+" "+N2.arity+" ");
            return false;
        }
        int[] args1 = N1.args(); 
        int[] args2 = N2.args(); 
        for (int i = 0; i < args2.length; i++) {
            if (FIND(args1[i]) != FIND(args2[i])) {
                logger.fine("NO CONGRUENCE");
                return false;
            }
        }
        logger.fine("OK CONGRUENCE");
        return true;
    }

    public boolean CONGRUENT(int id1, int id2){
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        return congruenceCheck(N1, N2);
    }

    public void MERGE(int id1, int id2){
        if (FIND(id1) != FIND(id2)) {
            Set<Integer> P1 = CCPAR(id1);
            logger.fine("ccpars1: "+P1.toString());
            Set<Integer> P2 = CCPAR(id2);
            logger.fine("ccpars2: "+P2.toString());
            UNION(id1, id2);
            if(!P1.isEmpty() && !P2.isEmpty()){
                for (Integer t1 : P1) {
                    for (Integer t2 : P2) {
                        logger.fine("CONGUENT "+t1+" "+t2);
                        logger.fine("Same Find: "+(FIND(t1) != FIND(t2)));
                        logger.fine("CONGRUENT : "+(CONGRUENT(t1, t2)));
                        if (FIND(t1) != FIND(t2) && CONGRUENT(t1, t2)){
                            MERGE(t1, t2);
                        }                 
                    }                
                }
            }
        }
        logger.fine("END MERGE: "+this.nodes.toString());
    }
    
}

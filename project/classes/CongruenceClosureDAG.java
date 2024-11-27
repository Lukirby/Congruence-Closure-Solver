package project.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import project.debug.Debug;

public class CongruenceClosureDAG {

    private Debug logger;

    private HashMap<Integer,Node> nodes;

    public boolean verbose;

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, boolean verbose ,Level log){

        this.logger = new Debug(this.getClass(), log);

        this.verbose = verbose;

        this.nodes = nodes;

        logger.fine(nodes.toString());
    }

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, boolean verbose){
        this(nodes,verbose,Level.SEVERE);
    }

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes){
        this(nodes,false,Level.SEVERE);
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
        if(this.verbose){
            System.out.println("UNION "+this.printNode(id1)+" "+this.printNode(id2));
        }
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        for (Node N : nodes.values()) {
            if (N.find == N1.find){
                N.find = N2.find;
            }
        }
        N1.find = N2.find;
        N2.ccpar.addAll(N1.ccpar);
        N1.ccpar.clear();
        logger.fine("ccpars1: "+N1.ccpar.toString());
        logger.fine("ccpars2: "+N1.ccpar.toString());
    }

    public Set<Integer> CCPAR(int id){
        return new HashSet<Integer>(NODE(FIND(id)).ccpar);
    }

    private boolean congruenceCheck(Node N1, Node N2){
        if (!(N1.name.equals(N2.name))) {
            if(this.verbose){
                System.out.println("FALSE: "+N1.name+" != "+N2.name);
            }
            return false;
        }
        if (N1.arity != N2.arity){
            if(this.verbose){
                System.out.println("FALSE: "+N1.arity+" != "+N2.arity);
            }
            return false;
        }
        int[] args1 = N1.args(); 
        int[] args2 = N2.args(); 
        for (int i = 0; i < args2.length; i++) {
            if (FIND(args1[i]) != FIND(args2[i])) {
                if(this.verbose){
                    System.out.println("FALSE: FIND("+printNode(args1[i])+") != " + "FIND("+printNode(args2[i])+")");
                }
                return false;
            }
        }
        if(this.verbose){
            System.out.println("TRUE");
        }
        return true;
    }

    public boolean CONGRUENT(int id1, int id2){
        if(this.verbose){
            System.out.println("CONGREUNCE "+this.printNode(id1)+" "+this.printNode(id2));
        }
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        return congruenceCheck(N1, N2);
    }

    public void MERGE(int id1, int id2){
        if(this.verbose){
            System.out.println("MERGE "+this.printNode(id1)+" "+this.printNode(id2));
        }
        if (FIND(id1) != FIND(id2)) {
            Set<Integer> P1 = CCPAR(id1);
            
            Set<Integer> P2 = CCPAR(id2);
            logger.fine("ccpars2: "+P2.toString());
            if(this.verbose){
                System.out.println("P1: "+printCCPAR(P1));
                System.out.println("P2: "+printCCPAR(P2));
            }
            UNION(id1, id2);
            if(!P1.isEmpty() && !P2.isEmpty()){
                for (int t1 : P1) {
                    for (int t2 : P2) {
                        if (FIND(t1) != FIND(t2) && CONGRUENT(t1, t2)){
                            MERGE(t1, t2);
                        }                 
                    }                
                }
            }
        }
        logger.fine("END MERGE: "+this.nodes.toString());
    }
    

    public String printNode(int id){
        Node N = NODE(id);
        String s = N.name;
        if (N.arity > 0){
            s+="(";
            for (int i = 0; i < N.arity; i++) {
                s += printNode(N.args()[i]);
                if (N.arity > 1 && i != N.arity-1){
                    s+=",";
                }
            }
            s+=")"; 
        }
        return s;
    }

    public String printCCPAR(Set<Integer> ccpar){
        String s = "{";
        Iterator<Integer> iter = ccpar.iterator();
        while (iter.hasNext()) {
            s += printNode(iter.next());
            if(iter.hasNext()){
                s += ", ";
            }
        }
        s += "}";
        return s;
    }

}
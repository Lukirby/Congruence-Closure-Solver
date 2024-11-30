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

    public boolean unsatFlag;

    public boolean verbose;

    public boolean recursiveFind;

    public boolean euristicUnion;

    public boolean forbiddenSet;

    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, boolean verbose ,Level log){

        this.logger = new Debug(this.getClass(), log);

        this.verbose = verbose;

        this.nodes = nodes;

        this.unsatFlag = false;

        this.recursiveFind=false;

        this.euristicUnion=true;

        this.forbiddenSet=false;

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

    public int REC_FIND(int id){
        Node N = NODE(id);
        if (N==null){
            logger.severe("Null Node for id "+id);
        }
        if (N.find == id){
            return id;
        } else {
            return REC_FIND(N.find);
        }
    }

    public int FIND(int id){
        if (this.recursiveFind) {
            return REC_FIND(id);
        }
        Node N = NODE(id);
        if (N==null){
            logger.severe("Null Node for id "+id);
        }
        return N.find;
    }

    public void EUR_UNION(int id1, int id2){
        Node N1 = NODE(FIND(id1));
        Node N2 = NODE(FIND(id2));
        Node R;
        Node N;
        if (N1.ccpar.size() > N2.ccpar.size()){
            R = N1;
            N = N2;
        } else {
            R = N2;
            N = N1;
        }
        if (!this.recursiveFind) {
            for (Node M : nodes.values()) {
                if (M.find == N.find){
                    M.find = R.find;
                }
            }
        }
        N.find = R.find;
        R.ccpar.addAll(N1.ccpar);
        N.ccpar.clear();
        if(this.forbiddenSet){
            R.forb.addAll(N.forb);
            N.forb.clear();
        }
        logger.fine("ccpars rapp: "+R.ccpar);
        logger.fine("ccpars node: "+N.ccpar);
        logger.fine("forbidden rapp: "+R.forb);
        logger.fine("forbidden node: "+N.forb);
    }

    public void UNION(int id1, int id2){
        if(this.verbose){
            System.out.println("UNION "+this.printNode(id1)+" "+this.printNode(id2));
        }
        if(this.euristicUnion){
            EUR_UNION(id1, id2);
        } else {
            Node N1 = NODE(FIND(id1));
            Node N2 = NODE(FIND(id2));
            if (!this.recursiveFind) {
                for (Node N : nodes.values()) {
                    if (N.find == N1.find){
                        N.find = N2.find;
                    }
                }
            }
            N1.find = N2.find;
            N2.ccpar.addAll(N1.ccpar);
            N1.ccpar.clear();
            if(this.forbiddenSet){
                N2.forb.addAll(N1.forb);
                N1.forb.clear();
            }
            logger.fine("ccpars1: "+N1.ccpar);
            logger.fine("ccpars2: "+N1.ccpar);
            logger.fine("forbidden1: "+N1.forb);
            logger.fine("forbidden2: "+N1.forb);
        }
    }

    public Set<Integer> CCPAR(int id){
        return new HashSet<Integer>(NODE(FIND(id)).ccpar);
    }

    private boolean FORBIDDEN(int id1, int id2){
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        if (N1.forb.contains(N2.find) || N2.forb.contains(N1.find)){
            this.unsatFlag = true;
            return true;
        }
        return false;
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
        if(this.forbiddenSet){
            if(FORBIDDEN(id1, id2)){
                if(this.verbose){
                    System.out.println("FORBIDDEN "+this.printNode(id1)+" "+this.printNode(id2));
                }    
            }      
        }
        if (this.unsatFlag){
            logger.fine("MERGE INTERRUPTED: "+this.nodes.toString());
            return;
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
                            if (this.unsatFlag){
                                logger.fine("MERGE INTERRUPTED: "+this.nodes.toString());
                                return;
                            }
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
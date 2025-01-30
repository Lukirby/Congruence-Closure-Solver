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

    public String output = "";

    private char indentationChar = '\t';

    private StringBuilder indentation = new StringBuilder();

    /**
     * Constructs a new CongruenceClosureDAG instance.
     *
     * @param nodes A HashMap containing the nodes of the DAG, where the key is an Integer and the value is a Node.
     * @param opt An Options object containing various configuration settings.
     * @param log A Level object specifying the logging level.
     */
    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, Options opt, Level log){

        this.logger = new Debug(this.getClass(), log);

        this.verbose = opt.verbose;

        this.nodes = nodes;

        this.unsatFlag = false;

        this.recursiveFind=opt.recursiveFind;

        this.euristicUnion=opt.euristicUnion;

        this.forbiddenSet=opt.forbiddenSet;

        logger.fine(nodes.toString());
    }

    /**
     * Constructs a CongruenceClosureDAG with the specified nodes and options.
     * Log set to OFF.
     * 
     * @param nodes a HashMap containing the nodes of the DAG, where the key is an Integer and the value is a Node.
     * @param opt an Options object containing configuration settings for the DAG.
     */
    public CongruenceClosureDAG(HashMap<Integer,Node> nodes, Options opt){
        this(nodes,opt,Level.SEVERE);
    }

    /**
     * Constructs a new CongruenceClosureDAG with the specified nodes.
     * Log set to OFF.
     * Default options are used (everything is set to false).
     * 
     * @param nodes a HashMap containing the nodes of the DAG, where the key is an Integer
     *              representing the node identifier and the value is a Node object.
     */
    public CongruenceClosureDAG(HashMap<Integer,Node> nodes){
        this(nodes,new Options(),Level.SEVERE);
    }

    /**
     * Retrieves the Node object associated with the given identifier.
     *
     * @param id the identifier of the Node to retrieve
     * @return the Node object associated with the specified id
     */
    public Node NODE(int id){
        return this.nodes.get(id);
    }

    /**
     * Recursively finds the representative of the set containing the given node id.
     * 
     * @param id the id of the node to find the representative for
     * @return the id of the representative node
     */
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

    /**
     * Finds the representative of the congruence closure containing the given node id.
     * If the recursive find option is enabled, uses the REC_FIND method.
     * Otherwise, it retrieves the node and returns its representative.
     *
     * @param id the ID of the node to find the representative for
     * @return the representative ID of the set containing the given node id.
     */
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

    /**
     * Merges the equivalence classes of the nodes identified by the given IDs.
     * The node with the larger ccpar set becomes the representative of the merged class.
     * If the sizes are equal, the second node becomes the representative.
     * 
     * @param id1 the ID of the first node
     * @param id2 the ID of the second node
     */
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
            int originalFind = N.find;
            for (Node M : nodes.values()) {
                if (M.find == originalFind){
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

    /**
     * Performs the merge operation on two nodes identified by their ids.
     * If euristicUnion is enabled, uses the EUR_UNION method.
     *
     * @param id1 the ID of the first node
     * @param id2 the ID of the second node
     */
    public void UNION(int id1, int id2){
        if(this.verbose){
            writeMessage("UNION "+this.printNode(id1)+" "+this.printNode(id2));
        }
        if(this.euristicUnion){
            EUR_UNION(id1, id2);
        } else {
            Node N1 = NODE(FIND(id1));
            Node N2 = NODE(FIND(id2));
            if (!this.recursiveFind) {
                int originalFind = N1.find;
                for (Node M : nodes.values()) {
                    if (M.find == originalFind){
                        M.find = N2.find;
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

    /**
     * Returns a set of integers representing the congruence closure parents (ccpar) 
     * of the node identified by the given id, in form of a copy of the ccpar set.
     *
     * @param id the identifier of the node whose ccpar set is to be retrieved
     * @return a set of integers representing the ccpar of the specified node
     */
    public Set<Integer> CCPAR(int id){
        return new HashSet<Integer>(NODE(FIND(id)).ccpar);
    }

    private boolean FORBIDDEN(int id1, int id2){
        Node N1 = NODE(FIND(id1));
        Node N2 = NODE(FIND(id2));
        if (N1.forb.contains(N2.find) || N2.forb.contains(N1.find)){
            this.unsatFlag = true;
            return true;
        }
        return false;
    }

    private boolean congruenceCheck(Node N1, Node N2){
        if (!(N1.name.equals(N2.name))) {
            if(this.verbose){
                writeMessage("FALSE: "+N1.name+" != "+N2.name);
            }
            return false;
        }
        if (N1.arity != N2.arity){
            if(this.verbose){
                writeMessage("FALSE: "+N1.arity+" != "+N2.arity);
            }
            return false;
        }
        int[] args1 = N1.args(); 
        int[] args2 = N2.args(); 
        for (int i = 0; i < args2.length; i++) {
            if (FIND(args1[i]) != FIND(args2[i])) {
                if(this.verbose){
                    writeMessage("FALSE: FIND("+printNode(args1[i])+") != " + "FIND("+printNode(args2[i])+")");
                }
                return false;
            }
        }
        if(this.verbose){
            writeMessage("TRUE");
        }
        return true;
    }

    /**
     * Checks if two nodes are congruent.
     *
     * @param id1 the identifier of the first node
     * @param id2 the identifier of the second node
     * @return true if the nodes are congruent, false otherwise
     */
    public boolean CONGRUENT(int id1, int id2){
        if(this.verbose){
            writeMessage("CONGREUNCE "+this.printNode(id1)+" "+this.printNode(id2));
        }
        Node N1 = NODE(id1);
        Node N2 = NODE(id2);
        return congruenceCheck(N1, N2);
    }

    /**
     * Merges two nodes in the congruence closure DAG.
     * 
     * @param id1 The identifier of the first node.
     * @param id2 The identifier of the second node.
     * 
     */
    public void MERGE(int id1, int id2){
        if(this.verbose){
            writeMessage("MERGE "+this.printNode(id1)+" "+this.printNode(id2));
        }
        if(this.forbiddenSet){
            if(FORBIDDEN(id1, id2)){
                if(this.verbose){
                    writeMessage("FORBIDDEN "+this.printNode(id1)+" "+this.printNode(id2));
                }    
            }      
        }
        if (this.unsatFlag){
            logger.fine("MERGE INTERRUPTED");
            return;
        }
        if (FIND(id1) != FIND(id2)) {
            Set<Integer> P1 = CCPAR(id1);
            
            Set<Integer> P2 = CCPAR(id2);
            logger.fine("ccpars2: "+P2.toString());
            if(this.verbose){
                writeMessage("P1: "+printCCPAR(P1));
                writeMessage("P2: "+printCCPAR(P2));
            }
            UNION(id1, id2);
            if(!P1.isEmpty() && !P2.isEmpty()){
                for (int t1 : P1) {
                    for (int t2 : P2) {
                        if (FIND(t1) != FIND(t2) && CONGRUENT(t1, t2)){
                            this.indentation.append(this.indentationChar);
                            MERGE(t1, t2);
                            this.indentation.deleteCharAt(0);
                            if (this.unsatFlag){
                                logger.fine("MERGE INTERRUPTED");
                                return;
                            }
                        }                 
                    }                
                }
            }
        }
        logger.fine("END MERGE");
    }
    
    /**
     * Sets the nodes of the Congruence Closure DAG.
     *
     * @param nodes a HashMap where the key is an Integer representing the node ID
     *              and the value is a Node object representing the node itself.
     */
    public void setNodes(HashMap<Integer, Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Retrieves the map of nodes in the congruence closure DAG.
     *
     * @return a HashMap where the keys are node identifiers (integers) and the values are Node objects.
     */
    public HashMap<Integer, Node> getNodes() {
        return nodes;
    }

    /**
     * Appends a message to the output with the current indentation level.
     *
     * @param message The message to be appended to the output.
     */
    public void writeMessage(String message){
        this.output += this.indentation.toString()+message+"\n";
    }

    /**
     * Recursively constructs a string representation of the node with the given ID.
     *
     * @param id the ID of the node to be printed
     * @return a string representation of the node and its arguments
     */
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

    /**
     * Converts a set of integers representing nodes into a formatted string.
     *
     * @param ccpar the set of integers representing nodes to be formatted
     * @return a string representation of the set in the format "{node1, node2, ...}"
     */
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

    /**
     * Prints the congruence classes of the nodes in the DAG.
     * 
     * @return A string representation of the congruence classes in the format:
     *         {
     *             representative1 : { node1, node2, ... },
     *             representative2 : { node3, node4, ... },
     *             ...
     *         }
     */
    public String printCoungruenceClasses(){
        HashMap<Integer,HashSet<Node>> classes = new HashMap<Integer,HashSet<Node>>();
        for (Node N: this.nodes.values()){
            if (!classes.containsKey(N.find)){
                classes.put(N.find, new HashSet<Node>());
            }
            classes.get(N.find).add(N);
        }
        String s = "{\n";
        for (int key : classes.keySet()){
            s = s+"\t"+this.printNode(key)+" : { ";

            for (Node N : classes.get(key)){
                s = s+this.printNode(N.id)+", ";
            }
            s += "}\n";
        }
        s = s+"\n}";
        return s;
    }

}
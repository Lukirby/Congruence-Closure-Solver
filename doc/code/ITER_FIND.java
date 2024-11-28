import java.util.Collections;

Integer ITER_FIND(Integer id){
    Node N = NODE(id);
    return N.find
}    

void UNION(Integer id1, Integer id2){
    Node N1 = NODE(FIND(id1));
    Node N2 = NODE(FIND(id2));
    for (Node N : nodes.values()) {
        if (N.find == N1.find){
            N.find = N2.find;
        }
    }
    N1.find = N2.find;
    N2.ccpar.addAll(N1.ccpar);
    N1.ccpar = Collections.emptySet();
}                                                       ;       

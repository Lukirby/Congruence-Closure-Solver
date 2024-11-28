void UNION(Integer id1, Integer id2){
    Node N1 = NODE(FIND(id1));
    Node N2 = NODE(FIND(id2));
    N1.find = N2.find;
    N2.ccpar.addAll(N1.ccpar);
    N1.ccpar = Collections.emptySet();
}                                                       ;
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
    for (Node M : nodes.values()) {
        if (M.find == N.find){
            M.find = R.find;
        }
    }
    N.find = R.find;
    R.ccpar.addAll(N1.ccpar);
    N.ccpar.clear();
}                                                       ;
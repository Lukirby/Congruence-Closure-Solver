private boolean FORBIDDEN(int id1, int id2){
    Node N1 = NODE(id1);
    Node N2 = NODE(id2);
    if (N1.forb.contains(N2.find) || N2.forb.contains(N1.find)){
        this.unsatFlag = true;
        return true;
    }
    return false;
}

public void UNION(int id1, int id2){
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
    N.find = R.find;
    R.ccpar.addAll(N1.ccpar);
    N.ccpar.clear();
    R.forb.addAll(N.forb);
    N.forb.clear();
    
}

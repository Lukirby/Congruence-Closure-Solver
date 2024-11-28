Boolean congruenceCheck(Node N1, Node N2){
    if (N1.name != N2.name | N1.arity != N2.arity){
        return false;
    }
    if (Arrays.equals(N1.args(), N2.args())){
        return true;
    } else {
        return false;
    }
}

Boolean CONGRUENT(Integer id1, Integer id2){
    Node N1 = NODE(id1);
    Node N2 = NODE(id2);
    return congruenceCheck(N1, N2);
}                                                       ;
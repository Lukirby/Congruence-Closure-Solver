void MERGE(Integer id1, Integer id2){
    if (FIND(id1) != FIND(id2)) {
        Set<Integer> P1 = CCPAR(id1);
        Set<Integer> P2 = CCPAR(id2);
        UNION(id1, id2);
        for (Integer t1 : P1) {
            for (Integer t2 : P2) {
                if (FIND(t1) != FIND(t2) & CONGRUENT(id1, id2)){
                    MERGE(t1, t2);
                }                 
            }                
        }
    }
}                                                       ;
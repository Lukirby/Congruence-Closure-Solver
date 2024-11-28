Integer FIND(Integer id){
    Node N = NODE(id);
    if (N.find == id){
        return id;
    } else {
        return FIND(N.id);
    }
}                                                       ;

package project.preprocessing;

import java.util.ArrayList;
import java.util.Iterator;

import project.classes.DNFTree;

public class DNFTrasformer {

    public static String transform(DNFTree tree) {
        tree = deleteImplications(tree);
        tree = deleteNestedNegations(tree);
        tree = distributeConjunctions(tree);
        tree = removeDuplicates(tree);

        return makeFormula(tree);

    }

    public static DNFTree deleteImplications(DNFTree tree){
        if(tree.getValue().equals(PropLogic.implication)){
            tree = handleImplication(tree);
        } else
        if(tree.getValue().equals(PropLogic.coimplication)){
            tree = handleCoimplication(tree);
        }
        if (!tree.isLeaf()){
            tree.getChildren().replaceAll(child -> deleteImplications(child));
        }
        return tree;
    }

    public static DNFTree handleImplication(DNFTree tree){
        tree.value = PropLogic.disjunction;
        ArrayList<DNFTree> children = tree.getChildren();
        DNFTree left = children.remove(0);
        DNFTree negation = new DNFTree(PropLogic.negation);
        negation.addChildren(left);
        children.add(0,negation);
        negation.parent = tree;
        return tree;
    }

    public static DNFTree handleCoimplication(DNFTree tree){
        tree.value = PropLogic.disjunction;
        ArrayList<DNFTree> children = tree.getChildren();
        DNFTree left = children.remove(0);
        DNFTree right = children.remove(0);
        DNFTree negatedLeft = new DNFTree(PropLogic.negation);
        negatedLeft.addChildren(left);
        DNFTree negatedRight = new DNFTree(PropLogic.negation);
        negatedRight.addChildren(right);

        DNFTree leftConjuction = new DNFTree(PropLogic.conjuction);
        leftConjuction.addChildren(left);
        leftConjuction.addChildren(right);

        DNFTree rightConjuction = new DNFTree(PropLogic.conjuction);
        rightConjuction.addChildren(negatedLeft);
        rightConjuction.addChildren(negatedRight);

        tree.addChildren(leftConjuction);
        tree.addChildren(rightConjuction);

        return tree;
    }

    public static DNFTree deleteNestedNegations(DNFTree tree){
        if (tree == null) {
            return null;
        }
        if(tree.getValue().equals(PropLogic.negation)){
            tree = handleNegation(tree);
        }
        if (!tree.isLeaf()){
            tree.getChildren().replaceAll(child -> deleteNestedNegations(child));
        }
        return tree;
    }

    public static DNFTree handleNegation(DNFTree tree){
        ArrayList<DNFTree> children = tree.getChildren();
        DNFTree child = children.get(0);
        if(child.getValue().equals(PropLogic.negation)){
            child = child.getChildren().get(0);
            child.parent = tree.parent;
            return child;
        } else 
        if(child.getValue().equals(PropLogic.conjuction)){
            child.value = PropLogic.disjunction;
            DNFTree left = child.getChildren().remove(0);
            DNFTree right = child.getChildren().remove(0);
            DNFTree negatedLeft = new DNFTree(PropLogic.negation);
            negatedLeft.addChildren(left);
            DNFTree negatedRight = new DNFTree(PropLogic.negation);
            negatedRight.addChildren(right);
            child.addChildren(negatedLeft);
            child.addChildren(negatedRight);
            return child;
        } else
        if(child.getValue().equals(PropLogic.disjunction)){
            child.value = PropLogic.conjuction;
            DNFTree left = child.getChildren().remove(0);
            DNFTree right = child.getChildren().remove(0);
            DNFTree negatedLeft = new DNFTree(PropLogic.negation);
            negatedLeft.addChildren(left);
            DNFTree negatedRight = new DNFTree(PropLogic.negation);
            negatedRight.addChildren(right);
            child.addChildren(negatedLeft);
            child.addChildren(negatedRight);
            return child;
        }
        return tree;
    }

    public static DNFTree distributeConjunctions(DNFTree tree){
        if(tree.getValue().equals(PropLogic.conjuction)){
            tree = handleDoubleOperator(tree);
            tree = handleConjunction2(tree);
        }
        if (!tree.isLeaf()){
            tree.getChildren().replaceAll(child -> distributeConjunctions(child));
        }
        if(tree.getValue().equals(PropLogic.disjunction)){
            tree = handleDoubleOperator(tree);
        }
        if(tree.getValue().equals(PropLogic.negation)){
            tree = makeNegatedTerm(tree);
        }
        return tree;    
    }

    public static DNFTree handleConjunction2(DNFTree tree){
        int childrenSize = tree.getChildren().size();
        ArrayList<DNFTree> children = new ArrayList<DNFTree>(childrenSize);
        int disjunctions = -1;
        int branchesSize = childrenSize;
        
        for (int i = 0; i < childrenSize; i++) {
            DNFTree child = tree.children.remove(0);
            if(child.getValue().equals(PropLogic.disjunction) && disjunctions == -1){
                disjunctions = i;
                branchesSize = child.getChildren().size();
            }
            children.add(child);
        }

        if(disjunctions != -1){
            tree.value = PropLogic.disjunction;
            for (int i = 0; i < branchesSize; i++) {
                DNFTree newConjunction = new DNFTree(PropLogic.conjuction);
                for (int j = 0; j < childrenSize; j++) {
                    if (j == disjunctions){
                        DNFTree disjunction = children.get(j);
                        DNFTree child = disjunction.getChildren().get(i);
                        newConjunction.addChildren(child);
                    } else {
                        DNFTree child = children.get(j);
                        newConjunction.addChildren(child);
                    }
                }
                tree.addChildren(newConjunction);
            }
        } else {
            for (int i = 0; i < childrenSize; i++) {
                DNFTree child = children.get(i);
                tree.addChildren(child);
            }
        }
        return tree;
    }

    public static DNFTree handleConjunction(DNFTree tree) {
        ArrayList<DNFTree> children = tree.getChildren();
        DNFTree leftChild = children.get(0);
        DNFTree rightChild = children.get(1);

        if(leftChild.getValue().equals(PropLogic.disjunction)){
            tree.value = PropLogic.disjunction;

            leftChild = tree.getChildren().remove(0);
            rightChild = tree.getChildren().remove(0);

            DNFTree leftLeft = leftChild.getChildren().remove(0);
            DNFTree leftRight = leftChild.getChildren().remove(0);

            DNFTree leftConjuction = new DNFTree(PropLogic.conjuction);
            leftConjuction.addChildren(leftLeft);
            leftConjuction.addChildren(rightChild);

            DNFTree rightConjuction = new DNFTree(PropLogic.conjuction);
            rightConjuction.addChildren(leftRight);
            rightConjuction.addChildren(rightChild);

            tree.addChildren(leftConjuction);
            tree.addChildren(rightConjuction);

        } else 
        if (rightChild.getValue().equals(PropLogic.disjunction)){
            tree.value = PropLogic.disjunction;
            leftChild = tree.getChildren().remove(0);
            rightChild = tree.getChildren().remove(0);

            DNFTree rightLeft = rightChild.getChildren().remove(0);
            DNFTree rightRight = rightChild.getChildren().remove(0);

            DNFTree leftConjuction = new DNFTree(PropLogic.conjuction);

            leftConjuction.addChildren(leftChild);
            leftConjuction.addChildren(rightLeft);

            DNFTree rightConjuction = new DNFTree(PropLogic.conjuction);
            rightConjuction.addChildren(leftChild);
            rightConjuction.addChildren(rightRight);

            tree.addChildren(leftConjuction);
            tree.addChildren(rightConjuction);
        }
        return tree;
    }

    public static DNFTree handleDoubleOperator(DNFTree tree){
        boolean hasRipetitions = true;
        int childrenSize ;
        ArrayList<DNFTree> children;
        while (hasRipetitions) {
            hasRipetitions = false;
            childrenSize = tree.getChildren().size();
            children = new ArrayList<>(tree.getChildren());
            tree.children.clear();
            for (int i = 0; i < childrenSize; i++) {
                DNFTree child = children.get(i);
                if(child.getValue().equals(tree.getValue())){
                    for (DNFTree grandChild : child.getChildren()) {
                        tree.addChildren(grandChild);
                    }
                    hasRipetitions = true;
                } else {
                    tree.addChildren(child);
                }
            }
        }
        return tree;
    }

    public static DNFTree makeNegatedTerm(DNFTree tree){
        DNFTree child = tree.getChildren().get(0);
        if(child.value.contains(EqSignature.disequality)){
            child.value = child.value.replace(EqSignature.disequality, EqSignature.equality);
        } else
        if(child.value.contains(EqSignature.equality)){
            child.value = child.value.replace(EqSignature.equality, EqSignature.disequality);
        } else {
            child.value = PropLogic.negation + child.value;
        }
        return child;
    }

    public static DNFTree removeDuplicates(DNFTree tree){
        if (!tree.isLeaf()){
            ArrayList<DNFTree> children = tree.getChildren();
            for (int i = 0; i < children.size(); i++) {
                DNFTree child = children.get(i);
                if(children.indexOf(child) != i){
                    children.remove(i);
                    i--;
                }
            }
            tree.getChildren().replaceAll(child -> removeDuplicates(child));
        }
        return tree;
    }

    public static String makeFormula(DNFTree tree){
        if(tree.isLeaf()){
            return tree.getValue();
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<DNFTree> iterator = tree.getChildren().iterator();
            while (iterator.hasNext()) {
                DNFTree child = iterator.next();
                sb.append(makeFormula(child));
                sb.append(" ");
                if(iterator.hasNext()){
                    sb.append(tree.getValue());
                    sb.append(" ");
                }
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        String F = TermsParser.cleanFormula("[[[x0 = y0 & y0 = x1] | [x0 = z0 & z0 = x1]] & ~[x0 = x1]]");
        DNFTree tree = DNFParser.parse(F);
        String S = DNFTrasformer.transform(tree);
        System.out.println(S);
    }
}



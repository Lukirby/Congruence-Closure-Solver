package project.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import project.classes.DNFTree;
import project.preprocessing.DNFParser;
import project.preprocessing.DNFTrasformer;
import project.preprocessing.PropLogic;


public class DNFTrasformerTest {

    @Test
    public void testTransform() {
        DNFTree tree = new DNFTree(PropLogic.implication);
        DNFTree left = new DNFTree("A");
        DNFTree right = new DNFTree("B");
        tree.addChildren(left);
        tree.addChildren(right);
        System.out.println(tree.toString());

        String result = DNFTrasformer.transform(tree);
        assertEquals("~A | B ", result);
    }

    @Test
    public void testDeleteImplications() {
        DNFTree tree = new DNFTree(PropLogic.implication);
        DNFTree left = new DNFTree("A");
        DNFTree right = new DNFTree("B");
        tree.addChildren(left);
        tree.addChildren(right);

        DNFTree result = DNFTrasformer.deleteImplications(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(1).getValue());
    }

    @Test
    public void testDeleteCoimplication() {
        DNFTree tree = new DNFTree(PropLogic.coimplication);
        DNFTree left = new DNFTree("A");
        DNFTree right = new DNFTree("B");
        tree.addChildren(left);
        tree.addChildren(right);

        DNFTree result = DNFTrasformer.deleteImplications(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(1).getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(1).getChildren().get(0).getChildren().get(0).getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(1).getChildren().get(1).getValue());
        assertEquals("B", result.getChildren().get(1).getChildren().get(1).getChildren().get(0).getValue());
    }

    @Test
    public void testDeleteNestedNegations() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree negation = new DNFTree(PropLogic.negation);
        DNFTree child = new DNFTree("A");
        negation.addChildren(child);
        tree.addChildren(negation);

        DNFTree result = DNFTrasformer.deleteNestedNegations(tree);
        assertEquals("A", result.getValue());
    }

    @Test
    public void testMorganLaw1() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree conjuction = new DNFTree(PropLogic.conjuction);
        DNFTree child1 = new DNFTree("A");
        DNFTree child2 = new DNFTree("B");
        conjuction.addChildren(child1);
        conjuction.addChildren(child2);
        tree.addChildren(conjuction);

        DNFTree result = DNFTrasformer.deleteNestedNegations(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(1).getValue());
        assertEquals("B", result.getChildren().get(1).getChildren().get(0).getValue());
    }

    @Test
    public void testMorganLaw2() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree disjunction = new DNFTree(PropLogic.disjunction);
        DNFTree child1 = new DNFTree("A");
        DNFTree child2 = new DNFTree("B");
        disjunction.addChildren(child1);
        disjunction.addChildren(child2);
        tree.addChildren(disjunction);

        DNFTree result = DNFTrasformer.deleteNestedNegations(tree);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals(PropLogic.negation, result.getChildren().get(1).getValue());
        assertEquals("B", result.getChildren().get(1).getChildren().get(0).getValue());
    }

    @Test
    public void testDistributeConjunctionsLeft() {
        DNFTree tree = new DNFTree(PropLogic.conjuction);
        DNFTree left = new DNFTree(PropLogic.disjunction);
        DNFTree leftChild1 = new DNFTree("A");
        DNFTree leftChild2 = new DNFTree("B");
        left.addChildren(leftChild1);
        left.addChildren(leftChild2);
        DNFTree right = new DNFTree("C");
        tree.addChildren(left);
        tree.addChildren(right);

        System.out.println(tree.toString());

        DNFTree result = DNFTrasformer.distributeConjunctions(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("C", result.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(1).getValue());
        assertEquals("B", result.getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("C", result.getChildren().get(1).getChildren().get(1).getValue());
    }

    @Test
    public void testDistributeConjunctionsRight() {
        DNFTree tree = new DNFTree(PropLogic.conjuction);
        DNFTree left = new DNFTree("A");
        DNFTree right = new DNFTree(PropLogic.disjunction);
        DNFTree rightChild1 = new DNFTree("B");
        DNFTree rightChild2 = new DNFTree("C");
        right.addChildren(rightChild1);
        right.addChildren(rightChild2);
        tree.addChildren(left);
        tree.addChildren(right);

        System.out.println(tree.toString());

        DNFTree result = DNFTrasformer.distributeConjunctions(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(1).getValue());
        assertEquals("A", result.getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("C", result.getChildren().get(1).getChildren().get(1).getValue());
    }

    @Test
    public void testCompactTreeConjuctions() {
        DNFTree tree = new DNFTree(PropLogic.conjuction);
        DNFTree child1 = new DNFTree(PropLogic.conjuction);
        DNFTree child2 = new DNFTree("A");
        DNFTree child3 = new DNFTree("B");
        child1.addChildren(child2);
        child1.addChildren(child3);
        tree.addChildren(child1);

        DNFTree result = DNFTrasformer.compactTree(tree);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals("A", result.getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(1).getValue());
    }

    @Test
    public void testCompactTreeDisjuctions() {
        DNFTree tree = new DNFTree(PropLogic.disjunction);
        DNFTree child1 = new DNFTree(PropLogic.disjunction);
        DNFTree child2 = new DNFTree("A");
        DNFTree child3 = new DNFTree("B");
        child1.addChildren(child2);
        child1.addChildren(child3);
        tree.addChildren(child1);

        DNFTree result = DNFTrasformer.compactTree(tree);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals("A", result.getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(1).getValue());
    }

    @Test
    public void testRemoveDuplicates() {
        DNFTree tree = new DNFTree(PropLogic.conjuction);
        DNFTree child1 = new DNFTree("A");
        DNFTree child2 = new DNFTree("A");
        tree.addChildren(child1);
        tree.addChildren(child2);

        DNFTree result = DNFTrasformer.removeDuplicates(tree);
        assertEquals(1, result.getChildren().size());
        assertEquals("A", result.getChildren().get(0).getValue());
    }

    @Test
    public void testMakeFormula() {
        DNFTree tree = new DNFTree(PropLogic.conjuction);
        DNFTree child1 = new DNFTree("A");
        DNFTree child2 = new DNFTree("B");
        tree.addChildren(child1);
        tree.addChildren(child2);

        String result = DNFTrasformer.makeFormula(tree);
        assertEquals("A & B ", result);
    }

    @Test
    public void testMakeNegatedTermDisequality() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree child = new DNFTree("A != B");
        tree.addChildren(child);

        DNFTree result = DNFTrasformer.makeNegatedTerm(tree);
        assertEquals("A = B", result.getValue());
    }

    @Test
    public void testMakeNegatedTermEquality() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree child = new DNFTree("A = B");
        tree.addChildren(child);

        DNFTree result = DNFTrasformer.makeNegatedTerm(tree);
        assertEquals("A != B", result.getValue());
    }

    @Test
    public void testMakeNegatedTermOther() {
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree child = new DNFTree("A");
        tree.addChildren(child);

        DNFTree result = DNFTrasformer.makeNegatedTerm(tree);
        assertEquals(PropLogic.negation + "A", result.getValue());
    }

    @Test
    public void ComplexFormula(){
        DNFTree tree = new DNFTree(PropLogic.negation);
        DNFTree implication = new DNFTree(PropLogic.implication);
        DNFTree left = new DNFTree("P");
        DNFTree right = new DNFTree(PropLogic.negation);
        DNFTree disjunction = new DNFTree(PropLogic.conjuction);
        DNFTree child1 = new DNFTree("P");
        DNFTree child2 = new DNFTree("Q");
        disjunction.addChildren(child1);
        disjunction.addChildren(child2);
        right.addChildren(disjunction);
        implication.addChildren(left);
        implication.addChildren(right);
        tree.addChildren(implication);
        DNFTree treeParsed = DNFParser.parse("~[P £ ~[P & Q]]");
        assertEquals(tree, treeParsed);
        String result = DNFTrasformer.transform(tree);
        assertEquals("P & Q ", result);
        result = DNFTrasformer.transform(treeParsed);
        assertEquals("P & Q ", result);
    }

    @Test
    public void ComplexFormula2(){
        DNFTree parsedTree = DNFParser.parse("a&[b|c]");
        String result = DNFTrasformer.transform(parsedTree);
        System.out.println(result);
        assertEquals("a & b  | a & c  ", result);
    }

    @Test
    public void ComplexFormula3(){
        DNFTree parsedTree = DNFParser.parse("[a | b] & [c | d]");
        System.out.println(parsedTree.toString());
        String result = DNFTrasformer.transform(parsedTree);
        System.out.println(result);
        assertEquals("a & c  | a & d  | b & c  | b & d  ", result);
    }
}
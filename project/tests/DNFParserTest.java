package project.tests;

import project.classes.DNFTree;
import project.preprocessing.DNFParser;
import project.preprocessing.PropLogic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DNFParserTest {

    @Test
    public void testPrecedence() {
        assertEquals(3, DNFParser.precedence(PropLogic.negation));
        assertEquals(2, DNFParser.precedence(PropLogic.conjuction));
        assertEquals(1, DNFParser.precedence(PropLogic.disjunction));
        assertEquals(0, DNFParser.precedence(PropLogic.implication));
        assertEquals(0, DNFParser.precedence(PropLogic.coimplication));
    }

    @Test
    public void testParseSimpleExpression() {
        DNFTree result = DNFParser.parse("A");
        assertNotNull(result);
        assertEquals("A", result.getValue());
    }

    @Test
    public void testParseNegation() {
        DNFTree result = DNFParser.parse("~A");
        assertNotNull(result);
        assertEquals(PropLogic.negation, result.getValue());
        assertEquals(1, result.getChildren().size());
        assertEquals("A", result.getChildren().get(0).getValue());
    }

    @Test
    public void testParseDoubleNegation(){
        DNFTree result = DNFParser.parse("~[~A]");
        assertNotNull(result);
        assertEquals(PropLogic.negation, result.getValue());
        assertEquals(1, result.getChildren().size());
        assertEquals(PropLogic.negation, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        System.out.println(result.toString());
    }

    @Test
    public void testParseDoubleNegation2(){
        DNFTree result = DNFParser.parse("~~A");
        assertNotNull(result);
        assertEquals("A", result.getValue());
        System.out.println(result.toString());
    }

    @Test
    public void testParseConjunction() {
        DNFTree result = DNFParser.parse("A & B");
        assertNotNull(result);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals("A", result.getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(1).getValue());
    }

    @Test
    public void testParseDisjunction() {
        DNFTree result = DNFParser.parse("A | B");
        assertNotNull(result);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals("A", result.getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(1).getValue());
    }

    @Test
    public void testParseComplexExpression() {
        DNFTree result = DNFParser.parse("[A & B] | C");
        assertNotNull(result);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals("A", result.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("B", result.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("C", result.getChildren().get(1).getValue());
        System.out.println(result.toString());
    }

    @Test
    public void testInvalidExpression() {
        assertThrows(IllegalArgumentException.class, () -> {
            DNFParser.parse("A &");
        });
    }

    @Test
    public void testMismatchedParentheses() {
        assertThrows(IllegalArgumentException.class, () -> {
            DNFParser.parse("[A & B");
        });
    }

    @Test
    public void testVeryComplexExpression() {
        DNFTree result = DNFParser.parse("[[A & B] | C] & [D | E]");
        assertNotNull(result);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals(PropLogic.disjunction, result.getChildren().get(0).getValue());
        assertEquals(PropLogic.disjunction, result.getChildren().get(1).getValue());
        assertEquals(2, result.getChildren().get(0).getChildren().size());
        assertEquals(2, result.getChildren().get(1).getChildren().size());
        System.out.println(result.toString());
    }

    @Test
    public void testNoParenthesisExpression(){
        DNFTree result = DNFParser.parse("A & B | C & D");
        assertNotNull(result);
        assertEquals(PropLogic.disjunction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals(PropLogic.conjuction, result.getChildren().get(1).getValue());
        assertEquals(2, result.getChildren().get(0).getChildren().size());
        assertEquals(2, result.getChildren().get(1).getChildren().size());
        System.out.println(result.toString());
    }

    @Test
    public void testNoParenthesisExpression2(){
        DNFTree result = DNFParser.parse("A $ B | C & D");
        assertNotNull(result);
        assertEquals(PropLogic.coimplication, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals("A", result.getChildren().get(0).getValue());
        assertEquals(PropLogic.disjunction, result.getChildren().get(1).getValue());
        assertEquals(0, result.getChildren().get(0).getChildren().size());
        assertEquals(2, result.getChildren().get(1).getChildren().size());
        System.out.println(result.toString());
    }

    @Test
    public void testComplex1(){
        DNFTree result = DNFParser.parse("a & [b | c]");
        assertNotNull(result);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals("a", result.getChildren().get(0).getValue());
        assertEquals(PropLogic.disjunction, result.getChildren().get(1).getValue());
        assertEquals(2, result.getChildren().get(1).getChildren().size());
        assertEquals("b", result.getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("c", result.getChildren().get(1).getChildren().get(1).getValue());
        System.out.println(result.toString());
    }

    @Test
    public void testComplex2(){
        DNFTree result = DNFParser.parse("[a | b] & [c | d]");
        assertNotNull(result);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals(PropLogic.disjunction, result.getChildren().get(0).getValue());
        assertEquals(PropLogic.disjunction, result.getChildren().get(1).getValue());
        assertEquals(2, result.getChildren().get(0).getChildren().size());
        assertEquals(2, result.getChildren().get(1).getChildren().size());
        System.out.println(result.toString());
    }

    @Test
    public void testComplex3(){
        DNFTree result = DNFParser.parse("[[[A & B] | [C & D]] & [[E & F] | [G & H]] & I]");
        System.out.println(result.toString());
        assertNotNull(result);
        assertEquals(PropLogic.conjuction, result.getValue());
        assertEquals(2, result.getChildren().size());
        assertEquals(PropLogic.conjuction, result.getChildren().get(0).getValue());
        assertEquals("I", result.getChildren().get(1).getValue());
        DNFTree left1 = result.getChildren().get(0);
        assertEquals(2, left1.getChildren().size());
    }

}
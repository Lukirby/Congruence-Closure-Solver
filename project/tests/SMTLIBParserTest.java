package project.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.preprocessing.SMTLIBParser;
import project.preprocessing.EqSignature;
import project.preprocessing.PropLogic;



public class SMTLIBParserTest {

    private SMTLIBParser parser;

    @BeforeEach
    public void setUp() {
        parser = new SMTLIBParser();
    }

    @Test
    public void testReadDeclareFun_NoArguments() {
        String line = "(declare-fun f1 () S1)";
        String expected = "f1 = S1";
        String result = parser.readDeclareFun(line);
        assertEquals(expected, result);
    }

    @Test
    public void testReadDeclareFun_WithArguments() {
        String line = "(declare-fun f3 (S2 S3 S4 S5 S6) S1)";
        String expected = "f3(S2,S3,S4,S5,S6) = S1";
        String result = parser.readDeclareFun(line);
        assertEquals(expected, result);
    }

    @Test
    public void testReadDeclareFun_NoFunctionDeclarations() {
        String line = "(assert (= x y))";
        String expected = "";
        String result = parser.readDeclareFun(line);
        assertEquals(expected, result);
    }

    @Test
    public void testReadAssert_And() {
        String formula = "(and (= x y) (= y z))";
        String expected = "[x "+EqSignature.equality+" y "+PropLogic.conjuction+" y "+EqSignature.equality+" z]";
        String result = SMTLIBParser.readAssert(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testReadAssert_Or() {
        String formula = "(or (= x y) (= y z))";
        String expected = "[x "+EqSignature.equality+" y "+PropLogic.disjunction+" y "+EqSignature.equality+" z]";
        String result = SMTLIBParser.readAssert(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testReadAssert_Not() {
        String formula = "(not (= x y))";
        String expected = PropLogic.negation+"[x "+EqSignature.equality+" y]";
        String result = SMTLIBParser.readAssert(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testReadAssert_Equality() {
        String formula = "(= x y)";
        String expected = "x "+EqSignature.equality+" y";
        String result = SMTLIBParser.readAssert(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testSplitTopLevel() {
        String input = "(= x y) (= y z)";
        String[] expected = {"(= x y)", "(= y z)"};
        String[] result = SMTLIBParser.splitTopLevel(input);
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }        
}
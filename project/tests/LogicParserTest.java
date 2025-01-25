package project.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.preprocessing.LogicParser;
import project.preprocessing.TermsParser;


public class LogicParserTest {

    private LogicParser logicParser;

    @BeforeEach
    public void setUp() {
        logicParser = new LogicParser("", Level.INFO);
    }

    @Test
    public void testSplitSelectOverStore() {
        String formula = "select(store(a,i,v),j) = w";
        String[] expected = {
            "v=w;i=j",
            "select(a,j)=w;i!=j"
        };
        String[] result = logicParser.splitSelectOverStore(formula);
        assertArrayEquals(expected, result);
        formula = "select(store(store(a,b,c),f(a,g(a)),select(a,b)),select(store(c,s,a),a)) = select(c,d)";
        String[] expected2 = {
            "select(a,b)=select(c,d);f(a,g(a))=select(store(c,s,a),a)",
            "select(store(a,b,c),select(store(c,s,a),a))=select(c,d);f(a,g(a))!=select(store(c,s,a),a)"
        };
        result = logicParser.splitSelectOverStore(formula);
        assertArrayEquals(expected2, result);
    }

    @Test
    public void testArrayTheoryFormulaTransformation() {
        String formula = "select(store(store(a,i,v),j,w),k) != select(a,k)";
        logicParser = new LogicParser(formula,Level.SEVERE);
        logicParser.arrayTheoryFormulaTransformation();
        assertEquals(3,logicParser.formulaList.size());
        String formula1 = logicParser.formulaList.get(0);
        String formula2 = logicParser.formulaList.get(1);
        String formula3 = logicParser.formulaList.get(2);
        assertEquals("w!=select(a,k) ; j=k", formula1);
        assertEquals("v!=select(a,k) ; j!=k ; i=k", formula2);
        assertEquals("select(a,k)!=select(a,k) ; j!=k ; i!=k", formula3);
    }

    @Test
    public void testCastImplications() {
        String formula = "a->b";
        String expected = "a£b";
        String result = logicParser.castImplications(formula);
        assertEquals(expected, result);

        formula = "a<->b";
        expected = "a$b";
        result = logicParser.castImplications(formula);
        assertEquals(expected, result);

        formula = "a -> b <-> c";
        expected = "a £ b $ c";
        result = logicParser.castImplications(formula);
        assertEquals(expected, result);

        formula = "a <-> b -> c";
        expected = "a $ b £ c";
        result = logicParser.castImplications(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testReverseCastImplications() {
        String formula = "a£b";
        String expected = "a->b";
        String result = logicParser.reverseCastImplications(formula);
        assertEquals(expected, result);

        formula = "a$b";
        expected = "a<->b";
        result = logicParser.reverseCastImplications(formula);
        assertEquals(expected, result);

        formula = "a £ b $ c";
        expected = "a -> b <-> c";
        result = logicParser.reverseCastImplications(formula);
        assertEquals(expected, result);

        formula = "a $ b £ c";
        expected = "a <-> b -> c";
        result = logicParser.reverseCastImplications(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testCastInputOnConjunctions() {
        String formula = "a & b";
        String expected = "a ; b";
        String result = logicParser.castInputOnConjunctions(formula);
        assertEquals(expected, result);

        formula = "a & b&c";
        expected = "a ; b;c";
        result = logicParser.castInputOnConjunctions(formula);
        assertEquals(expected, result);

        formula = "[a & b] & [c & d]";
        expected = "[a ; b] ; [c ; d]";
        result = logicParser.castInputOnConjunctions(formula);
        assertEquals(expected, result);

        formula = "a & [b & c]";
        expected = "a ; [b ; c]";
        result = logicParser.castInputOnConjunctions(formula);
        assertEquals(expected, result);
    }

    @Test
    public void testDNF() {
        String formula = "a & b | c";
        String[] expected = {
            "a ; b",
            "c"
        };
        ArrayList<String> result = logicParser.DNF(formula);
        assertEquals(expected.length, result.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(TermsParser.cleanFormula(expected[i]),TermsParser.cleanFormula(result.get(i)));
        }

        String formula2 = "a & [b | c]";
        String[] expected2 = {
            "a ; b",
            "a ; c"
        };
        ArrayList<String> result2 = logicParser.DNF(formula2);
        System.out.println(result);
        assertEquals(expected2.length, result.size());
        for (int i = 0; i < expected2.length; i++) {
            assertEquals(TermsParser.cleanFormula(expected2[i]),TermsParser.cleanFormula(result2.get(i)));
        }

        formula = "[a | b] & [c | d]";
        String[] expected3 = {
            "a ; c",
            "a ; d",
            "b ; c",
            "b ; d"
        };
        result = logicParser.DNF(formula);
        assertEquals(expected3.length, result.size());
        for (int i = 0; i < expected3.length; i++) {
            assertEquals(TermsParser.cleanFormula(expected3[i]),TermsParser.cleanFormula(result.get(i)));
        }
    }

    @Test
    public void testRemoveQuantifiers() {
        String formula = "\\forall x. P(x)".replace(" ", "");
        String expected = "P(x)";
        String result = logicParser.removeQuantifiers(formula);
        assertEquals(expected, result);

        formula = "\\exists y. Q(y)".replace(" ", "");
        expected = "Q(y)";
        result = logicParser.removeQuantifiers(formula);
        assertEquals(expected, result);

        formula = "\\forall x. \\exists y. R(x, y)".replace(" ", "");;
        expected = "R(x,y)";
        result = logicParser.removeQuantifiers(formula);
        assertEquals(expected, result);

        formula = "P(x) & \\forall y. Q(y)".replace(" ", "");;
        expected = "P(x)&Q(y)";
        result = logicParser.removeQuantifiers(formula);
        assertEquals(expected, result);

        formula = "\\exists z. [P(z) | \\forall w. Q(w)]".replace(" ", "");;
        expected = "[P(z)|Q(w)]";
        result = logicParser.removeQuantifiers(formula);
        assertEquals(expected, result);
    }

}
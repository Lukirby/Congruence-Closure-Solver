package project.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.preprocessing.LogicParser;


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
        assertEquals("w!=select(a,k);j=k", formula1);
        assertEquals("v!=select(a,k);j!=k;i=k", formula2);
        assertEquals("select(a,k)!=select(a,k);j!=k;i!=k", formula3);
    }

}
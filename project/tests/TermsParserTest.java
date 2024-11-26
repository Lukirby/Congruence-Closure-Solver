package project.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import project.classes.Node;
import project.preprocessing.TermsParser;

import java.util.HashMap;
import java.util.ArrayList;

public class TermsParserTest {

    @Test
    public void testMakeNodesWithFunction() {
        TermsParser parser = new TermsParser("f(a,b) = x");
        Integer id = parser.makeNodes("f(a,b)");
        HashMap<Integer, Node> nodes = parser.getNodes();
        assertNotNull(nodes.get(id));
        assertEquals("f", nodes.get(id).name);
    }

    @Test
    public void testMakeNodesWithVariable() {
        TermsParser parser = new TermsParser("f(a,b) = x");
        Integer id = parser.makeNodes("x");
        HashMap<Integer, Node> nodes = parser.getNodes();
        assertNotNull(nodes.get(id));
        assertEquals("x", nodes.get(id).name);
    }

    @Test
    public void testTermsParserWithEqualities() {
        TermsParser parser = new TermsParser("a = b; c = d");
        ArrayList<Integer[]> equalities = parser.getEqualities();
        assertEquals(2, equalities.size());
    }

    @Test
    public void testTermsParserWithDisequalities() {
        TermsParser parser = new TermsParser("a != b; c != d");
        ArrayList<Integer[]> disequalities = parser.getDisequalities();
        assertEquals(2, disequalities.size());
    }

    @Test
    public void testTermsParserWithMixedPredicates() {
        TermsParser parser = new TermsParser("a = b; c != d");
        ArrayList<Integer[]> equalities = parser.getEqualities();
        ArrayList<Integer[]> disequalities = parser.getDisequalities();
        assertEquals(1, equalities.size());
        assertEquals(1, disequalities.size());
    }
}
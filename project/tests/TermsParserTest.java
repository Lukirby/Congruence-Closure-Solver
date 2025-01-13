package project.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import project.classes.Node;
import project.classes.Theory;
import project.preprocessing.PropLogic;
import project.preprocessing.TermsParser;
import java.util.ArrayList;

public class TermsParserTest {

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

    @Test
    public void testTermParserWithPositivePredicate(){
        TermsParser parser = new TermsParser("R(a,b);");
        ArrayList<Integer[]> equalities = parser.getEqualities();
        ArrayList<Integer[]> disequalities = parser.getDisequalities();
        assertEquals(1, equalities.size());
        assertEquals(0, disequalities.size());
    }

    @Test
    public void testTermParserWithNegativePredicate(){
        TermsParser parser = new TermsParser("~R(a,b);");
        ArrayList<Integer[]> equalities = parser.getEqualities();
        ArrayList<Integer[]> disequalities = parser.getDisequalities();
        assertEquals(0, equalities.size());
        assertEquals(1, disequalities.size());
    }

    @Test
    public void testTermParserWithPropVariables(){
        TermsParser parser = new TermsParser("R;~G;A");
        ArrayList<Integer[]> equalities = parser.getEqualities();
        ArrayList<Integer[]> disequalities = parser.getDisequalities();
        assertEquals(2, equalities.size());
        assertEquals(1, disequalities.size());
    }

    @Test
    public void testListTermFound(){
        ArrayList<String> A = new ArrayList<String>(5);
        A.add("cons(a,a) != cons(b,a);");
        A.add("car(a) = b;");
        A.add("cdr(b) = a;");
        A.add("atom(a);");
        A.add("~atom(a);"); 
        TermsParser parser;
        for (String formula : A) {
            parser = new TermsParser(formula);    
            assertEquals(Theory.LIST, parser.theory);
        };
        
    }

    @Test
    public void testListTermNotFound(){
        ArrayList<String> A = new ArrayList<String>(5);
        A.add("cns(a) != con(b,a,c);");
        A.add("cr(a,a,a) = ar(b,a);");
        A.add("r(a,a) = c(b,b,a);");
        A.add("atm(a,a);");
        A.add("~tom(a,a,a);"); 
        TermsParser parser;
        for (String formula : A) {
            parser = new TermsParser(formula);    
            assertNotEquals(Theory.LIST, parser.theory);
        };
        
    }

    @Test
    public void testHandleNegatedAtom(){
        TermsParser parser = new TermsParser("~atom(r)");
        Integer[] terms = parser.equalities.get(0);
        Node N0 = parser.nodes.get(terms[0]);
        assertEquals(N0.name,"r");
        Node N1 = parser.nodes.get(terms[1]);
        assertEquals(N1.name,"cons");
        for (int i=1 ; i<3 ; i++){
            Node A = parser.nodes.get(N1.args()[i-1]);
            assertEquals(A.name, "r_"+i);
        }

        parser = new TermsParser("~atom(f(abv))");
        assertTrue(parser.equalities.isEmpty());
        terms = parser.disequalities.get(0);
        N0 = parser.nodes.get(terms[0]);
        assertEquals(N0.name,"f_atom");
        N1 = parser.nodes.get(terms[1]);
        assertEquals(N1.name,PropLogic.trueCostant);
        assertEquals(N0.arity,1);
        Node A = parser.nodes.get(N0.args()[0]);
        assertEquals(A.name, "f");
        Node B = parser.nodes.get(A.args()[0]);
        assertEquals(B.name, "abv");
    }

    @Test
    public void testArrayTermFound(){
        ArrayList<String> A = new ArrayList<String>(5);
        A.add("select(a,a) != select(b,a);");
        A.add("store(a) = f(b);");
        A.add("store(b) != a;");
        A.add("select(a,a) = store(b,b);");
        TermsParser parser;
        for (String formula : A) {
            parser = new TermsParser(formula);    
            assertEquals(Theory.ARRAY, parser.theory);
        };   
        ArrayList<String> B = new ArrayList<String>(5);
        B.add("st(a,a) != st(b,a);");
        B.add("stoe(a) = b;");
        B.add("stoe(b) != a;");
        B.add("selt(a,a) = sore(b,b);");
        for (String formula : B) {
            parser = new TermsParser(formula);    
            assertNotEquals(Theory.ARRAY, parser.theory);
        };   
    }

}
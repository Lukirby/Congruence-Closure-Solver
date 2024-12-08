package project.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import project.classes.Theory;
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
        A.add("cons(a) != cons(b,a,c);");
        A.add("car(a,a,a) = car(b,a);");
        A.add("car(a,a) = car(b,b,a);");
        A.add("atom(a,a);");
        A.add("~atom(a,a,a);"); 
        TermsParser parser;
        for (String formula : A) {
            parser = new TermsParser(formula);    
            assertNotEquals(Theory.LIST, parser.theory);
        };
        
    }
}
package project.tests;

import org.junit.jupiter.api.Test;

import project.classes.CongruenceClosureAlgorithm;
import project.classes.Options;
import project.classes.Node;
import project.preprocessing.TermsParser;

import static org.junit.jupiter.api.Assertions.*;

public class CongruenceClosureAlgorithmTest {

    @Test
    public void testComputeWithNoDisequalities() {
        String formula = " a= b ; b = f(f(a))";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertTrue(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithNoEqualities() {
        String formula = " a != g(b) ; f(a,b) != g(g(a))";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertTrue(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithSatisfiableDisequalities() {
        String formula = " a = c ; b = f(f(a)) ; b != c";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertTrue(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithUnsatisfiableDisequalities() {
        String formula = " f(a,b) = a; f(f(a,b),b) != a;";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertFalse(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertFalse(CCA.compute());
    }

    @Test
    public void testComputeWithUnsatisfiableDisequalities2() {
        String formula = " f(f(f(a))) = a;\r\n" + //
                        "f(f(f(f(f(a))))) = a;\r\n" + //
                        "f(a) != a ;";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertFalse(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertFalse(CCA.compute());
    }

    @Test
    public void testConsMerging() {
        String formula = " cons(a,b) = c";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        CCA.compute();
        assertEquals(CCA.nodes.size(), 6);
        for(Node N : CCA.nodes.values()){
            if(N.name.equals("car")){
                Node A = CCA.nodes.get(N.args()[0]);
                assertEquals("cons",A.name);
                Node B = CCA.nodes.get(A.args()[0]);
                assertEquals("a",B.name);
                assertTrue(CCA.DAG.FIND(N.id) == CCA.DAG.FIND(B.id));
            }
            if(N.name.equals("cdr")){
                Node A = CCA.nodes.get(N.args()[0]);
                assertEquals("cons",A.name);
                Node B = CCA.nodes.get(A.args()[1]);
                assertEquals("b",B.name);
                assertTrue(CCA.DAG.FIND(N.id) == CCA.DAG.FIND(B.id));
            }
        }
    }

    @Test
    public void testComputeUnsatisfiableList() {
        String formula = " car(x) = car(y);\r\n" + //
                        "cdr(x) = cdr(y);\r\n" + //
                        "~atom(x) ; ~atom(y);\r\n" + //
                        "f(x) != f(y) ;";
        TermsParser TP = new TermsParser(formula, false);
        CongruenceClosureAlgorithm CCA;
        Options O = new Options();
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,O);
        assertFalse(CCA.compute());
        O.setEuristicUnion(true);
        O.setForbiddenSet(true);
        O.setRecursiveFind(false);
        CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,TP.theory,new Options());
        assertFalse(CCA.compute());
    }

}
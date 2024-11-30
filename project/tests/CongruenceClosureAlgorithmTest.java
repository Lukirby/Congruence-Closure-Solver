package project.tests;

import org.junit.jupiter.api.Test;

import project.classes.CongruenceClosureAlgorithm;
import project.classes.Options;
import project.preprocessing.TermsParser;

import static org.junit.jupiter.api.Assertions.*;

public class CongruenceClosureAlgorithmTest {

    @Test
    public void testComputeWithNoDisequalities() {
        String formula = " a= b ; b = f(f(a))";
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithNoEqualities() {
        String formula = " a != g(b) ; f(a,b) != g(g(a))";
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithSatisfiableDisequalities() {
        String formula = " a = c ; b = f(f(a)) ; b != c";
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,new Options());
        assertTrue(CCA.compute());
    }

    @Test
    public void testComputeWithUnsatisfiableDisequalities() {
        String formula = " f(a,b) = a; f(f(a,b),b) != a;";
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,new Options());
        assertFalse(CCA.compute());
    }

    @Test
    public void testComputeWithUnsatisfiableDisequalities2() {
        String formula = " f(f(f(a))) = a;\r\n" + //
                        "f(f(f(f(f(a))))) = a;\r\n" + //
                        "f(a) != a ;";
        TermsParser TP = new TermsParser(formula, false);
        System.out.println(TP.SF.toString());
        CongruenceClosureAlgorithm CCA = new CongruenceClosureAlgorithm(TP.nodes, TP.equalities, TP.disequalities,new Options());
        assertFalse(CCA.compute());
    }
}
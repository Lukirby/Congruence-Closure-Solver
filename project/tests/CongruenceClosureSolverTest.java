package project.tests;
import org.junit.jupiter.api.Test;

import project.CongruenceClosureSolver;

import static org.junit.jupiter.api.Assertions.*;

public class CongruenceClosureSolverTest {
    @Test void EqBM3_2Test(){
        String file = "EqBM3.2.txt";
        assertTrue(CongruenceClosureSolver.solveInput(file));
        assertTrue(CongruenceClosureSolver.solveInput(file+"-v -r -e -f"));
    }

    @Test void LisBM3_15Test(){
        String file = "ListBM3.15.txt";
        assertTrue(CongruenceClosureSolver.solveInput(file));
        assertTrue(CongruenceClosureSolver.solveInput(file+"-v -r -e -f"));
    }
}

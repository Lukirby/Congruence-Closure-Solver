package project.tests;
import org.junit.jupiter.api.Test;

import project.CongruenceClosureSolver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

public class CongruenceClosureSolverTest {

    @AfterEach
    public void saveFile(){
        CongruenceClosureSolver.writeOutput();
    }

    @Test void EqBM3_2Test(){
        String file = "EqBM3.2.txt";
        assertTrue(CongruenceClosureSolver.solveInput(file));
        assertTrue(CongruenceClosureSolver.solveInput(file+" -v -r -e -f"));
    }

    @Test void LisTBM3_15Test(){
        String file = "ListBM3.15.txt";
        assertTrue(CongruenceClosureSolver.solveInput(file));
        assertTrue(CongruenceClosureSolver.solveInput(file+" -v -r -e -f"));
    }

    @Test void ArrayBM9_21Test(){
        String file = "ArrayBM9.21.txt";
        assertFalse(CongruenceClosureSolver.solveInput(file));
        assertFalse(CongruenceClosureSolver.solveInput(file+" -v -r -e -f"));
    }

}

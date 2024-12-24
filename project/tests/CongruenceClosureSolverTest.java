package project.tests;
import org.junit.jupiter.api.Test;

import project.CongruenceClosureSolver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class CongruenceClosureSolverTest {

    CongruenceClosureSolver CCS;

    @BeforeEach
    public void setUp(){
        CCS = new CongruenceClosureSolver();
    }

    @AfterEach
    public void saveFile(){
        CCS.writeOutput();
    }

    @Test void EqBM1_25Test(){
        String file = "EqBM1.25.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM3_2Test(){
        String file = "EqBM3.2.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void LisTBM3_15Test(){
        String file = "ListBM3.15.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void ArrayBM9_21Test(){
        String file = "ArrayBM9.21.txt";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

}

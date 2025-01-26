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
    
    @Test void EqBM1_8Test(){
        String file = "EqBM1.8.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM1_12Test(){
        String file = "EqBM1.12.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM1_25Test(){
        String file = "EqBM1.25.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM2_27Test(){
        String file = "EqBM2.27.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM3_2Test(){
        String file = "EqBM3.2.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM3_9Test(){
        String file = "EqBM3.9.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void ListBM3_15Test(){
        String file = "ListBM3.15.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void ArrayBM3_16Test(){
        String file = "ArrayBM3.16.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM3_19Test(){
        String file = "EqBM3.19.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqBM9_1Test(){
        String file = "EqBM9.1.txt";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void ListBM9_6aTest(){
        String file = "ListBM9.6a.txt";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
        assertTrue(CCS.solveInput(file+" -v"));
    }

    @Test void ListBM9_6bTest(){
        String file = "ListBM9.6b.txt";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void ArrayBM9_8dTest(){
        String file = "ArrayBM9.8d.txt";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }
    
    @Test void ArrayBM9_21Test(){
        String file = "ArrayBM9.21.txt";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqDiamond1(){
        String file = "eq_diamond1.smt2";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqDiamond2(){
        String file = "eq_diamond2.smt2";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void EqDiamond3(){
        String file = "eq_diamond3.smt2";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void GEN1(){
        String file = "GEN1.properties";
        assertTrue(CCS.solveInput(file));
        assertTrue(CCS.solveInput(file+" -v -r -e -f"));
    }

    @Test void GENLIST1(){
        String file = "GENLIST1.properties";
        assertFalse(CCS.solveInput(file));
        assertFalse(CCS.solveInput(file+" -v -r -e -f"));
    }


}

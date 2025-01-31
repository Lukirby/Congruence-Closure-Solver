package project.tests;
import org.junit.jupiter.api.Test;

import project.CongruenceClosureSolver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class AnalizerTest {

    CongruenceClosureSolver CCS;

    @BeforeEach
    public void setUp(){
        CCS = new CongruenceClosureSolver();
    }

    @AfterEach
    public void saveFile(){
        CCS.writeOutput();
    }

    @Test void EqBM1_8_rTest(){
        String file = "EqBM1.8_r.txt";
        assertTrue(CCS.solveInput(file+" -r"));
    }

    @Test void EqBM1_8Test(){
        String file = "EqBM1.8.txt";
        assertTrue(CCS.solveInput(file));
    }

    @Test void EqBM1_8_eTest(){
        String file = "EqBM1.8_e.txt";
        assertTrue(CCS.solveInput(file+" -e"));
    }

    @Test void EqBM1_8_fTest(){
        String file = "EqBM1.8_f.txt";
        assertTrue(CCS.solveInput(file+" -f"));
    }



    @Test void ArrayBM9_8d_rTest(){
        String file = "ArrayBM9.8d_r.txt";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void ArrayBM9_8d_reTest(){
        String file = "ArrayBM9.8d_re.txt";
        assertFalse(CCS.solveInput(file+" -r -e"));
    }

    @Test void ArrayBM9_8d_rfTest(){
        String file = "ArrayBM9.8d_rf.txt";
        assertFalse(CCS.solveInput(file+" -r -f"));
    }



    @Test void ListBM9_6b_Test(){
        String file = "ListBM9.6b.txt";
        assertFalse(CCS.solveInput(file));
    }

    @Test void ListBM9_6b_refTest(){
        String file = "ListBM9.6b_ref.txt";
        assertFalse(CCS.solveInput(file+" -r -e -f"));
    }



    @Test void EqDiamond1_r(){
        String file = "eq_diamond1_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond1_ef(){
        String file = "eq_diamond1_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond2_r(){
        String file = "eq_diamond2_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond2_ef(){
        String file = "eq_diamond2_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond3_r(){
        String file = "eq_diamond3_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond3_ef(){
        String file = "eq_diamond3_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond4_r(){
        String file = "eq_diamond4_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond4_ef(){
        String file = "eq_diamond4_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond5_r(){
        String file = "eq_diamond5_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond5_ef(){
        String file = "eq_diamond5_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond6_r(){
        String file = "eq_diamond6_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond6_ef(){
        String file = "eq_diamond6_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond7_r(){
        String file = "eq_diamond7_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond7_ef(){
        String file = "eq_diamond7_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond8_r(){
        String file = "eq_diamond8_r.smt2";
        assertFalse(CCS.solveInput(file+" -r"));
    }

    @Test void EqDiamond8_ef(){
        String file = "eq_diamond8_ef.smt2";
        assertFalse(CCS.solveInput(file+" -e -f"));
    }

    @Test void EqDiamond9_r(){
        String file = "eq_diamond9_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond9_ef(){
        String file = "eq_diamond9_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond10_r(){
        String file = "eq_diamond10_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond10_ef(){
        String file = "eq_diamond10_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond11_r(){
        String file = "eq_diamond11_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond11_ef(){
        String file = "eq_diamond11_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond12_r(){
        String file = "eq_diamond12_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond12_ef(){
        String file = "eq_diamond12_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond13_r(){
        String file = "eq_diamond13_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond13_ef(){
        String file = "eq_diamond13_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond14_r(){
        String file = "eq_diamond14_r.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }

    @Test void EqDiamond14_ef(){
        String file = "eq_diamond14_ef.smt2";
        assertFalse(CCS.solveInput(file+"-e -f"));
    }

    @Test void EqDiamond14_f(){
        String file = "eq_diamond14_f.smt2";
        assertFalse(CCS.solveInput(file+"-f"));
    }

    @Test void EqDiamond14(){
        String file = "eq_diamond14.smt2";
        assertFalse(CCS.solveInput(file+"-r"));
    }


    @Test void GEN_100_r(){
        String file = "GEN100_r.properties";
        assertTrue(CCS.solveInput(file + " -r"));
    }

    @Test void GEN_100_e(){
        String file = "GEN100_e.properties";
        assertTrue(CCS.solveInput(file + " -e"));
    }

    @Test void GEN_100_f(){
        String file = "GEN100_f.properties";
        assertTrue(CCS.solveInput(file + " -f"));
    }

    @Test void GEN_100_ef(){
        String file = "GEN100_ef.properties";
        assertTrue(CCS.solveInput(file + " -e -f"));
    }

    @Test void GEN_100_re(){
        String file = "GEN100_re.properties";
        assertTrue(CCS.solveInput(file + " -r -e"));
    }

    @Test void GEN_100_rf(){
        String file = "GEN100_rf.properties";
        assertTrue(CCS.solveInput(file + " -r -f"));
    }

    @Test void GEN_100_ref(){
        String file = "GEN100_ref.properties";
        assertTrue(CCS.solveInput(file + " -e -r -f"));
    }

    @Test void GEN_100(){
        String file = "GEN100.properties";
        assertTrue(CCS.solveInput(file));
    }





}

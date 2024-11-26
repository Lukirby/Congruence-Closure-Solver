package project.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
}
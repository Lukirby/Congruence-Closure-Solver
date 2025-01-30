package project.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.classes.Theory;
import project.preprocessing.Generator;
import project.preprocessing.PropLogic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTest {

    private Generator generator;

    @BeforeEach
    public void setUp() {
        generator = new Generator(true);
    }

    @Test
    public void testFormatProperties() {
        String property = "a, b, c";
        String[] expected = {"a", "b", "c"};
        assertArrayEquals(expected, generator.formatProperties(property));
    }

    @Test
    public void testFormatPropertiesNull() {
        assertNull(generator.formatProperties(null));
    }

    @Test
    public void testFormatPropertiesEmpty() {
        assertNull(generator.formatProperties(""));
    }

    @Test
    public void testCheckTheoryEquality() {
        generator.functions = new String[]{};
        generator.predicates = new String[]{};
        assertEquals(Theory.EQUALITY, generator.checkTheory());
    }

    @Test
    public void testCheckTheoryList() {
        generator.functions = new String[]{"cons"};
        generator.predicates = new String[]{};
        assertEquals(Theory.LIST, generator.checkTheory());
    }

    @Test
    public void testCheckTheoryArray() {
        generator.functions = new String[]{"select"};
        generator.predicates = new String[]{};
        assertEquals(Theory.ARRAY, generator.checkTheory());
    }

    @Test
    public void testReadFileGenerator() {
        generator.readFileGenerator("GEN1.properties");
        assertNotNull(generator.constants);
        assertNotNull(generator.functions);
        assertNotNull(generator.predicates);
        assertTrue(generator.maxDepth > 0);
        assertTrue(generator.maxArity > 0);
        assertTrue(generator.cubes > 0);
        assertTrue(generator.probabilities.length == 3);
    }

    @Test
    public void testGenerateConstant() {
        generator.constants = new String[]{"a", "b", "c"};
        generator.random = new Random(1);
        String constant = generator.generateConstant();
        assertTrue(constant.equals("a") || constant.equals("b") || constant.equals("c"));
    }

    @Test
    public void testGenerateFunction() {
        generator.functions = new String[]{"f", "g"};
        generator.constants = new String[]{"a", "b"};
        generator.random = new Random(1);
        generator.maxArity = 2;
        String function = generator.generateFunction(1, 1);
        assertTrue(function.startsWith("f(") || function.startsWith("g("));
    }

    @Test
    public void testGeneratePredicate() {
        generator.predicates = new String[]{"p", "q"};
        generator.functions = new String[]{"f", "g"};
        generator.constants = new String[]{"a", "b"};
        generator.random = new Random(1);
        generator.maxArity = 2;
        String predicate = generator.generatePredicate(1, 1);
        assertTrue(predicate.startsWith("p(") || predicate.startsWith("q("));
    }

    @Test
    public void testGenerateEquality() {
        assertEquals("=", generator.generateEquality(true));
        assertEquals("!=", generator.generateEquality(false));
    }

    @Test
    public void testGenerateNegation() {
        assertEquals(PropLogic.negation, generator.generateNegation(true));
        assertEquals("", generator.generateNegation(false));
    }

    @Test
    public void testGetNewExtension() {
        generator.seed = 12345;
        assertEquals("_12345.txt", generator.getNewExtention());
    }

    @Test
    public void testGenerateFile() {
        generator.readFileGenerator("testGenerator.properties");
        generator.constants = new String[]{"a", "b"};
        generator.functions = new String[]{"f"};
        generator.predicates = new String[]{"p"};
        generator.maxDepth = 1;
        generator.maxArity = 1;
        generator.cubes = 1;
        generator.probabilities = new float[]{0.33f, 0.33f, 0.34f};
        generator.costantProbability = 0.5f;
        generator.seed = 1;
        generator.generateFile();
        // Check if the file is created and contains expected content
        Path filePath = Paths.get(generator.totalPath.toString().replace("generator", "input").replace(".properties", generator.getNewExtention()));
        assertTrue(filePath.toFile().exists());
    }
}
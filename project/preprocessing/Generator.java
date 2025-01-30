package project.preprocessing;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.classes.Theory;
import project.debug.Debug;

import java.util.Properties;



public class Generator {

    public Properties generatorProperties = new Properties();

    public Path absPath;
    public Path totalPath;

    public Debug logger;

    public Random random;
    public int seed;
    
    public Theory theory;
    public String[] constants;
    public String[] functions;
    public String[] predicates;
    public int maxDepth; 
    public int maxArity;
    public int cubes;
    public float[] probabilities;
    public float costantProbability;

    /**
     * Formats a given property string by removing all whitespace and splitting it by commas.
     *
     * @param property the property string to be formatted
     * @return an array of formatted property strings, or null if the input property is null or empty
     */
    public String[] formatProperties(String property){
        if (property == null || property.isEmpty()){
            return null;
        } else {
            return property.replaceAll("\\s", "").split(",");
        }
    } 

    /**
     * Determines the theory type based on the functions and predicates provided.
     * 
     * This method checks the provided functions and predicates against predefined
     * regular expressions to determine if they belong to the LIST or ARRAY theories.
     * If no functions or predicates are provided, it defaults to the EQUALITY theory.
     * 
     * @return Theory - The determined theory type (EQUALITY, LIST, or ARRAY).
     */
    public Theory checkTheory(){
        Theory T = Theory.EQUALITY;
        Pattern patternList = Pattern.compile(Regex.listRegex);
        Pattern patternArray = Pattern.compile(Regex.arrayRegex);
        Matcher matcherList;
        Matcher matcherArray;
        String stringTerms = "";
        if (functions != null){
            stringTerms = stringTerms+String.join(",",functions);
        }
        if (predicates != null){
            stringTerms = stringTerms+String.join(",",predicates);
        }
        if (stringTerms.isEmpty()){
            return T;
        }
        matcherList = patternList.matcher(stringTerms);
        matcherArray = patternArray.matcher(stringTerms);
        if (matcherList.find()){
            T = Theory.LIST;
        }
        if (matcherArray.find()){
            if (T == Theory.LIST){
                T = Theory.EQUALITY;
            } else {
                T = Theory.ARRAY;
            }
        }
        return T;
    }

    /**
     * Reads and processes a properties file to initialize the generator's configuration.
     *
     * @param propertiesFile the name of the properties file to be read.
     * @throws IOException if an I/O error occurs when reading the properties file.
     * @throws NumberFormatException if a property that is expected to be a number cannot be parsed.
     *
     * The properties file should contain the following keys:
     * - constants: a comma-separated list of constants.
     * - functions: a comma-separated list of functions.
     * - predicates: a comma-separated list of predicates.
     * - maxDepth: the maximum depth for the generator.
     * - maxArity: the maximum arity for the generator.
     * - cubes: the number of cubes for the generator.
     * - probabilityConstants: the probability associated with constants.
     * - probabilityFunctions: the probability associated with functions.
     * - probabilityPredicates: the probability associated with predicates.
     * - seed: an optional seed for random number generation.
     */
    public void readFileGenerator(String propertiesFile){
        try {
            this.totalPath = Paths.get(this.absPath.toString(),propertiesFile.trim());
            this.generatorProperties.load(new FileReader(this.totalPath.toString()));
            this.constants = formatProperties(generatorProperties.getProperty("constants"));
            this.functions = formatProperties(generatorProperties.getProperty("functions"));
            this.predicates = formatProperties(generatorProperties.getProperty("predicates"));
            this.maxDepth = Integer.parseInt(generatorProperties.getProperty("maxDepth"));
            this.maxArity = Integer.parseInt(generatorProperties.getProperty("maxArity"));
            this.cubes = Integer.parseInt(generatorProperties.getProperty("cubes"));
            this.probabilities = new float[3];
            float p1 = constants == null ? 0 : Float.parseFloat(generatorProperties.getProperty("probabilityConstants"));
            float p2 = functions == null ? 0 : Float.parseFloat(generatorProperties.getProperty("probabilityFunctions"));
            float p3 = predicates == null ? 0 : Float.parseFloat(generatorProperties.getProperty("probabilityPredicates"));
            float sum = p1 + p2 + p3;
            this.probabilities[0] = p1/sum;
            this.probabilities[1] = p2/sum;
            this.probabilities[2] = p3/sum;
            this.costantProbability = p1/(p1+p2);
            this.theory = checkTheory();
            String seedProperty = generatorProperties.getProperty("seed");
            if (seedProperty == null){
                this.seed = new Random().nextInt(Integer.MAX_VALUE);
            } else {
                this.seed = Integer.parseInt(seedProperty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a random constant from the predefined list of constants.
     *
     * @return A randomly selected constant as a String.
     */
    public String generateConstant(){
        return constants[random.nextInt(constants.length)];
    }

    /**
     * Generates a function string based on the specified depth and arity.
     * The function is selected randomly from the available functions.
     * If the theory is LIST or ARRAY, the arity may be adjusted based on the function.
     *
     * @param depth the depth of the function generation, which determines the recursion level
     * @param arity the number of arguments the function takes
     * @return a string representing the generated function
     */
    public String generateFunction(int depth, int arity){
        String function = functions[random.nextInt(functions.length)];
        if(this.theory == Theory.LIST){
            if (function.equals(ListSignature.cons)){
                arity = 2;
            } else 
            if (function.equals(ListSignature.car) || function.equals(ListSignature.cdr)){
                arity = 1;
            }
        } else 
        if (this.theory == Theory.ARRAY){
            if (function.equals(ArraySignature.select)){
                arity = 2;
            } else 
            if (function.equals(ArraySignature.store)){
                arity = 3;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(function);
        sb.append("(");
        for (int i = 0; i < arity; i++){
            if (depth > 0){
                sb.append(generateFunction(depth-1,this.random.nextInt(this.maxArity)+1));
            } else {
                sb.append(generateConstant());
            }
            if (i < arity-1){
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Generates a predicate string with the specified depth and arity.
     *
     * @param depth the depth of the generated predicate, which determines the complexity of nested functions.
     * @param arity the number of arguments the predicate will have.
     * @return a string representing the generated predicate.
     */
    public String generatePredicate(int depth, int arity){
        String predicate = predicates[random.nextInt(predicates.length)];
        if (this.theory == Theory.LIST){
            if (predicate.equals(ListSignature.atom)){
                arity = 1;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(predicate);
        sb.append("(");
        for (int i = 0; i < arity; i++){
            if (depth > 0){
                sb.append(generateFunction(depth-1,this.random.nextInt(this.maxArity)+1));
            } else {
                sb.append(generateConstant());
            }
            if (i < arity-1){
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Generates an equality or disequality string based on the given boolean value.
     *
     * @param b a boolean value; if true, the method returns the equality string,
     *          otherwise it returns the disequality string.
     * @return a string representing either equality or disequality.
     */
    public String generateEquality(boolean b){
        return b ? EqSignature.equality : EqSignature.disequality;
    }

    /**
     * Generates a negation string based on the given boolean value.
     *
     * @param b the boolean value to determine the negation
     * @return the negation string if the boolean is true, otherwise an empty string
     */
    public String generateNegation(boolean b){
        return b ? PropLogic.negation : "";
    }

    /**
     * Generates a new file extension based on the current seed value.
     *
     * @return A string representing the new file extension in the format "_<seed>.txt".
     */
    public String getNewExtention(){
        return "_"+seed+".txt";
    }

    /**
     * Generates a file with randomly generated terms, functions, and predicates.
     * 
     * @throws IOException if an error occurs while writing to the file
     */
    public void generateFile(){
        this.random = new Random(this.seed);
        StringBuilder sb = new StringBuilder();
        String term = null;
        String eq = null;
        String neg = null;
        for (int i = 0; i < cubes; i++){
            int depth = random.nextInt(this.maxDepth)+1;
            int arity = random.nextInt(this.maxArity)+1;
            float prob = random.nextFloat();
            if (prob < this.probabilities[0]){
                // Generate a constant
                term = generateConstant();
                sb.append(term + " ");
                eq = generateEquality(random.nextBoolean());
                sb.append(eq + " ");
                prob = random.nextFloat();
                if (prob < this.costantProbability){
                    // Generate a constant
                    term = generateConstant();
                } else {
                    // Generate a function
                    term = generateFunction(depth,arity);
                }
                sb.append(term );
            } else if (prob < this.probabilities[0]+this.probabilities[1]){
                // Generate a function
                term = generateFunction(depth,arity);
                sb.append(term + " ");
                eq = generateEquality(random.nextBoolean());
                sb.append(eq + " ");
                prob = random.nextFloat();
                if (prob < this.costantProbability){
                    // Generate a constant
                    term = generateConstant();
                } else {
                    // Generate a function
                    term = generateFunction(depth,arity);
                }
                sb.append(term );
            } else {
                // Generate a predicate
                term = generatePredicate(depth,arity);
                neg = generateNegation(random.nextBoolean());
                sb.append(neg + term + " ");
            }
            sb.append(";");
            sb.append("\n");
        }
        logger.fine(sb.toString());
        try {
            // Create a FileWriter object to write to the file
            FileWriter fw = new FileWriter(this.totalPath.toString().replace("generator","input").replace(".properties",getNewExtention()));
            
            // Write the string content to the file
            fw.write(sb.toString());
            
            // Close the FileWriter to release system resources
            fw.close();
        } catch (IOException e) {
            // Handle exceptions that might occur during file operations
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Constructs a Generator object with logging capabilities and sets the absolute path.
     *
     * @param log a Boolean indicating whether to enable fine-grained logging (true) or severe logging (false).
     */
    public Generator(Boolean log){
        if (log){
            this.logger = new Debug(this.getClass(), Level.FINE);
        } else {
            this.logger = new Debug(this.getClass(), Level.SEVERE);
        }
        this.absPath = Paths.get("").toAbsolutePath();
        if (!absPath.toString().matches("project$")){
            this.absPath = Paths.get(this.absPath.toString(),"project","generator");
        } else {
            this.absPath = Paths.get(this.absPath.toString(),"generator");
        }
        this.logger.fine("Absolute Path: "+this.absPath.toString());
    }

    /**
     * Constructs a new Generator instance.
     * Log level is set to SEVERE.
     */
    public Generator(){
        this(false);
    }

    public static void main(String[] args) {
        Generator generator = new Generator(true);
        generator.readFileGenerator("GEN1.properties");
        generator.generateFile();
    }

}

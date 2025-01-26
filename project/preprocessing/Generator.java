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
    public int clauses;
    public float[] probabilities;
    public float costantProbability;

    public String[] formatProperties(String property){
        if (property == null || property.isEmpty()){
            return null;
        } else {
            return property.replaceAll("\\s", "").split(",");
        }
    } 

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
        } else if (matcherArray.find()){
            if (T == Theory.LIST){
                T = Theory.EQUALITY;
            } else {
                T = Theory.ARRAY;
            }
        }
        return T;
    }

    public void readFileGenerator(String propertiesFile){
        try {
            this.totalPath = Paths.get(this.absPath.toString(),propertiesFile.trim());
            this.generatorProperties.load(new FileReader(this.totalPath.toString()));
            this.constants = formatProperties(generatorProperties.getProperty("constants"));
            this.functions = formatProperties(generatorProperties.getProperty("functions"));
            this.predicates = formatProperties(generatorProperties.getProperty("predicates"));
            this.maxDepth = Integer.parseInt(generatorProperties.getProperty("maxDepth"));
            this.maxArity = Integer.parseInt(generatorProperties.getProperty("maxArity"));
            this.clauses = Integer.parseInt(generatorProperties.getProperty("clauses"));
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

    public String generateConstant(){
        return constants[random.nextInt(constants.length)];
    }

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

    public String generateEquality(boolean b){
        return b ? EqSignature.equality : EqSignature.disequality;
    }

    public String generateNegation(boolean b){
        return b ? PropLogic.negation : "";
    }

    public String getNewExtention(){
        return "_"+seed+".txt";
    }

    public void generateFile(){
        this.random = new Random(this.seed);
        StringBuilder sb = new StringBuilder();
        String term = null;
        String eq = null;
        String neg = null;
        for (int i = 0; i < clauses; i++){
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

    public Generator(){
        this(false);
    }

    public static void main(String[] args) {
        Generator generator = new Generator(true);
        generator.readFileGenerator("GEN1.properties");
        generator.generateFile();
    }

}

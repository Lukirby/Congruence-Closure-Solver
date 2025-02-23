package project.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import project.debug.Debug;

public class FormulaReader {

    public Debug logger;

    public String formula;

    private Path absPath;

    public Path totalPath;

    /**
     * Reads a formula from a specified file, ignoring lines that start with an underscore.
     * Logs the file path received, the absolute path, the total path, and the obtained formula.
     *
     * @param fileName the name of the file to read the formula from
     * @return the formula read from the file as a String
     */
    public String readFormulaFromFile(String fileName){
        logger.fine("File Path Received: "+fileName);
        String absString = absPath.toString();
        logger.fine("Absolute Path"+absString);
        this.totalPath = Paths.get(absString,fileName.trim());
        try (BufferedReader br = new BufferedReader(new FileReader(totalPath.toString()))) {
            logger.fine("Total Path: "+totalPath.toString());
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("_")){
                    content.append(line).append("\n");
                }
            }
            this.formula = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.fine("Formula obtained: "+this.formula);
        return this.formula;
    }

    public String readFormulaFromFile(){
        return this.readFormulaFromFile(this.formula);
    }

    /**
     * Constructs a FormulaReader object.
     *
     * @param log a Boolean indicating whether to enable fine-grained logging (true) or severe logging (false).
     *
     * If logging is enabled, a Debug logger is initialized with a FINE level.
     * Otherwise, a Debug logger is initialized with a SEVERE level.
     */
    public FormulaReader(Boolean log){
        if (log){
            this.logger = new Debug(this.getClass(), Level.FINE);
        } else {
            this.logger = new Debug(this.getClass(), Level.SEVERE);
        }
        this.absPath = Paths.get("").toAbsolutePath();
        if (!absPath.toString().matches("project$")){
            this.absPath = Paths.get(this.absPath.toString(),"project","input");
        } else {
            this.absPath = Paths.get(this.absPath.toString(),"input");
        }
        this.logger.fine("Absolute Path: "+this.absPath.toString());
    }

    public FormulaReader(){
        this(false);
    }

    public static void main(String[] args) {
        System.out.println(Paths.get("").toAbsolutePath().toString());
        String filePath = "prova.txt"; // Path to your file
        FormulaReader fr = new FormulaReader(true);
        fr.readFormulaFromFile(filePath);
    }
}

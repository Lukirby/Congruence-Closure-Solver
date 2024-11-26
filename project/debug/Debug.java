package project.debug;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Debug {
    
    Formatter formatter = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String level;
            if(record.getLevel() == Level.FINE){
                level = "CHECK";
            } else
            if(record.getLevel() == Level.SEVERE){
                level = "ERROR";
            } else {
                level = "NONE";
            }
            // Format without the date
            return level + ": " + record.getMessage() + "\n";
        }
    };
    
    public Logger logger;

    ConsoleHandler handler = new ConsoleHandler();

    public Debug(Object o, Level level){
        this.logger = Logger.getLogger(o.getClass().getName());
        logger.setLevel(level);

        handler.setFormatter(formatter);
        handler.setLevel(level);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public void severe(String msg){
        this.logger.severe(msg);
    }

    public void fine(String msg){
        this.logger.fine(msg);
    }

}

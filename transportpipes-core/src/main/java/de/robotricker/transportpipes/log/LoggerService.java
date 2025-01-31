package de.robotricker.transportpipes.log;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public record LoggerService(Logger logger) {

    @Inject
    public LoggerService(Logger logger) {
        this.logger = logger;
        setDebug(false);
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void warning(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void debug(String message) {
        logger.fine(message);
    }

    public void setDebug(boolean debug) {
        if (debug) {
            logger.setLevel(Level.FINE);
        } else {
            logger.setLevel(Level.INFO);
        }
    }

}

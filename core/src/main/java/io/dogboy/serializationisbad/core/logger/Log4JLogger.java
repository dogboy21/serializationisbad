package io.dogboy.serializationisbad.core.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4JLogger implements ILogger {
    private final Logger logger;

    public Log4JLogger(String name) {
        this.logger = LogManager.getLogger(name);
    }
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}

package io.dogboy.serializationisbad.core.logger;

import java.util.logging.Logger;

public class NativeLogger implements ILogger {
    private final Logger logger;

    public NativeLogger(String name) {
        this.logger = Logger.getLogger(name);
    }

    @Override
    public void debug(String message) {
        logger.finest(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.severe(message);
        throwable.printStackTrace();
    }
}

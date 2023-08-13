package io.dogboy.serializationisbad.core.logger;


public class NativeLogger implements ILogger {
    private static final boolean debugEnabled = System.getProperty("serializationisbad.nativelogger.debug", "false").equalsIgnoreCase("true");

    private final String name;

    public NativeLogger(String name) {
        this.name = name;
    }

    private void log(String level, String message) {
        System.out.println("[" + level + "] [" + name + "]: " + message);
    }

    @Override
    public void debug(String message) {
        if (NativeLogger.debugEnabled) {
            this.log("DEBUG", message);
        }
    }

    @Override
    public void info(String message) {
        this.log("INFO", message);
    }

    @Override
    public void warn(String message) {
        this.log("WARN", message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.log("ERROR", message);
        throwable.printStackTrace();
    }
}

package io.dogboy.serializationisbad.core.logger;

public interface ILogger {
    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message, Throwable throwable);
}

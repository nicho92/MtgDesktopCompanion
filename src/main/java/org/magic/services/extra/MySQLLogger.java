package org.magic.services.extra;



import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

import com.mysql.cj.log.Log;
import com.mysql.cj.util.LogUtils;

/**
 * Provides logging facilities for those platforms that don't have built-in facilities. Simply logs messages to STDERR.
 */
public class MySQLLogger implements Log {
    private static final int FATAL = 0;
    private static final int ERROR = 1;
    private static final int WARN = 2;
    private static final int INFO = 3;
    private static final int DEBUG = 4;
    private static final int TRACE = 5;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static StringBuffer bufferedLog = null;

    private boolean logLocationInfo = true;

    /**
     * Creates a new StandardLogger object.
     * 
     * @param name
     *            the name of the configuration to use -- ignored
     */
    public MySQLLogger(String name) {
        this(name, false);
    }

    /**
     * @param name
     *            the name of the configuration to use -- ignored
     * @param logLocationInfo
     *            logLocationInfo
     */
    public MySQLLogger(String name, boolean logLocationInfo) {
        this.logLocationInfo = logLocationInfo;
    }

    public static void startLoggingToBuffer() {
        bufferedLog = new StringBuffer();
    }

    public static void dropBuffer() {
        bufferedLog = null;
    }

    public static Appendable getBuffer() {
        return bufferedLog;
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isFatalEnabled() {
        return true;
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * Logs the given message instance using the 'debug' level
     * 
     * @param message
     *            the message to log
     */
    public void logDebug(Object message) {
        logInternal(DEBUG, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'debug' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logDebug(Object message, Throwable exception) {
        logInternal(DEBUG, message, exception);
    }

    /**
     * Logs the given message instance using the 'error' level
     * 
     * @param message
     *            the message to log
     */
    public void logError(Object message) {
        logInternal(ERROR, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'error' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logError(Object message, Throwable exception) {
        logInternal(ERROR, message, exception);
    }

    /**
     * Logs the given message instance using the 'fatal' level
     * 
     * @param message
     *            the message to log
     */
    public void logFatal(Object message) {
        logInternal(FATAL, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'fatal' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logFatal(Object message, Throwable exception) {
        logInternal(FATAL, message, exception);
    }

    /**
     * Logs the given message instance using the 'info' level
     * 
     * @param message
     *            the message to log
     */
    public void logInfo(Object message) {
        logInternal(INFO, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'info' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logInfo(Object message, Throwable exception) {
        logInternal(INFO, message, exception);
    }

    /**
     * Logs the given message instance using the 'trace' level
     * 
     * @param message
     *            the message to log
     */
    public void logTrace(Object message) {
        logInternal(TRACE, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'trace' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logTrace(Object message, Throwable exception) {
        logInternal(TRACE, message, exception);
    }

    /**
     * Logs the given message instance using the 'warn' level
     * 
     * @param message
     *            the message to log
     */
    public void logWarn(Object message) {
        logInternal(WARN, message, null);
    }

    /**
     * Logs the given message and Throwable at the 'warn' level.
     * 
     * @param message
     *            the message to log
     * @param exception
     *            the throwable to log (may be null)
     */
    public void logWarn(Object message, Throwable exception) {
        logInternal(WARN, message, exception);
    }

    protected void logInternal(int level, Object msg, Throwable exception) {
        StringBuilder msgBuf = new StringBuilder();
   
//        if (msg instanceof ProfilerEvent) {
//            msgBuf.append(LogUtils.expandProfilerEventIfNecessary(msg));
//
//        } else 
        {
            if (this.logLocationInfo && level != TRACE) {
                Throwable locationException = new Throwable();
                msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
                msgBuf.append(" ");
            }

            if (msg != null) {
                msgBuf.append(String.valueOf(msg));
            }
        }

        String messageAsString = msgBuf.toString();
        
        if(level==INFO)
        	logger.info(messageAsString);
        else if(level==DEBUG)
        	logger.debug(messageAsString);
        else if(level==ERROR)
        	logger.error(messageAsString);
        else if(level==WARN)
        	logger.warn(messageAsString);

        
        
        
        
        if (bufferedLog != null) {
            bufferedLog.append(messageAsString);
        }
    }
}

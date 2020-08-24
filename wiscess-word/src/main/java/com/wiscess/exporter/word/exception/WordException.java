package com.wiscess.exporter.word.exception;

/**
 * @author austin
 */
public class WordException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4734787069204186592L;

    /**
     *
     */
    public WordException() {
    }

    /**
     * @param message
     */
    public WordException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public WordException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public WordException(String message, Throwable cause) {
        super(message, cause);
    }

}

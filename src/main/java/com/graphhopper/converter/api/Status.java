package com.graphhopper.converter.api;

/**
 *
 * @author Peter Karich
 */
public class Status {

    public int code;
    public String message;

    public Status() {
    }

    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

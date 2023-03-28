package com.hrm.weblib;

public enum Status {
    OK(200, "OK"),
    NOT_FOUND(404, "Not found"),
    BAD_REQUEST(400, "Bad request"),
    SERVER_ERROR(500, "Server error");

    private final int code;
    private final String name;

    Status(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

package com.hrm.weblib;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {
    private Method method;
    private Map<String, String> headers;
    private String resource;
    private String body;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request request)) return false;
        return method == request.method && Objects.equals(headers, request.headers) && Objects.equals(resource, request.resource) && Objects.equals(body, request.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, headers, resource, body);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", headers=" + headers +
                ", resource='" + resource + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

package com.hrm.webserver;

import com.hrm.weblib.Controller;
import com.hrm.weblib.Method;
import com.hrm.weblib.Request;
import com.hrm.weblib.Response;
import com.hrm.weblib.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Controller controller;

    public Server(Controller controller) {
        this.controller = controller;
    }

    public void run(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[*] Started server at port: " + port);
            //noinspection InfiniteLoopStatement
            while (true) {
                try (Socket socket = server.accept();
                     InputStream in = socket.getInputStream();
                     OutputStream out = socket.getOutputStream()) {
                    System.out.println("\n[*] Accepted request");
                    String rawContent = getRequestContent(in);
                    if (rawContent.isBlank()) {
                        System.out.println("[*] Skip empty request");
                        continue;
                    }
                    System.out.println("[*] Content:\n" + rawContent);
                    Request request = createRequest(rawContent);
                    Response response = createResponse();
                    System.out.println("[*] Delegate request processing to: " + controller.getClass());
                    delegateToController(request, response);
                    System.out.println("[*] Get control back");
                    System.out.println("[*] Response to be sent:\n" + response);
                    sendResponse(out, response);
                    System.out.println("[*] Sent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void delegateToController(Request request, Response response) {
        if (Method.GET.equals(request.getMethod())) {
            controller.doGet(request, response);
        } else if (Method.POST.equals(request.getMethod())) {
            controller.doPost(request, response);
        }
    }

    private static Response createResponse() {
        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }

    private Request createRequest(String content) {
        Request request = new Request();
        int methodEnd = content.indexOf(' ');
        Method method = Method.valueOf(content.substring(0, methodEnd));
        request.setMethod(method);
        int resourceEnd = content.indexOf(' ', methodEnd + 1);
        String resource = content.substring(methodEnd + 1, resourceEnd);
        request.setResource(resource);
        int headersEnd = content.indexOf("\r\n\r\n");
        if (headersEnd == -1) {
            headersEnd = content.length();
        }
        int firstLineEnd = content.indexOf("\r\n") + 2;
        String[] headersRaw = content.substring(firstLineEnd, headersEnd).split("\r\n");
        Map<String, String> headers = new HashMap<>();
        for (String header : headersRaw) {
            int splitter = header.indexOf(":");
            String key = header.substring(0, splitter).trim();
            String value = header.substring(splitter + 1).trim();
            headers.put(key, value);
        }
        request.setHeaders(headers);
        int bodyStart = headersEnd + 4;
        if (bodyStart < content.length()) {
            String body = content.substring(bodyStart);
            request.setBody(body);
        }
        return request;
    }

    private void sendResponse(OutputStream out, Response response) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        Status status = response.getStatus();
        String body = response.getBody();
        writer.println("HTTP/1.1 " + status.getCode() + " " + status.getName());
        if (body != null && !body.isEmpty()) {
            writer.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
            writer.println();
            writer.println(body);
        } else {
            writer.println();
        }
        writer.flush();
    }

    private String getRequestContent(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        if (line == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        boolean hasBody = false;
        int length = 0;
        boolean bodyStarted = false;
        while (line != null) {
            content.append(line).append("\r\n");

            if (line.startsWith("Content-Length: ")) {
                hasBody = true;
                length = Integer.parseInt(line.substring("Content-Length: ".length()));
            }


            if (bodyStarted) {
                char[] buff = new char[length];
                int done = reader.read(buff);
                if (done < buff.length) {
                    throw new RuntimeException("Illegal body length");
                }
                content.append(buff);
                break;
            } else {
                line = reader.readLine();
            }

            if ("".equals(line)) {
                if (hasBody) {
                    bodyStarted = true;
                } else {
                    break;
                }
            }
        }
        return content.toString();
    }
}

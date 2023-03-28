package com.hrm.app;

import com.hrm.weblib.Controller;
import com.hrm.weblib.Request;
import com.hrm.weblib.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserController implements Controller {

    private final UserService userService = new UserServiceImpl();

    @Override
    public void doGet(Request request, Response response) {
        process(request, response);
    }

    @Override
    public void doPost(Request request, Response response) {
        process(request, response);
    }

    private void process(Request request, Response response) {
        User user = userService.getById(1L);
        String html = renderHtml(user);
        response.setBody(html);
    }

    private String renderHtml(User user) {
        return String.format("""
                <html>
                <body>
                    <nav>Hello! Current time: %s</nav>
                    <h1>User</h1>
                    <p>Id: %d</p>
                    <p>Login: %s</p>
                    <p>Password: %s</p>
                </body>
                </html>""", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss")), user.getId(), user.getLogin(), user.getPassword());
    }
}

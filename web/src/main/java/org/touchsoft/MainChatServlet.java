package org.touchsoft;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;

@WebServlet(name = "/")
public class MainChatServlet extends HttpServlet {
    ChatController controller = ChatController.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        controller.newSession(req.getSession().getId());

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String str = req.getReader().readLine();
        controller.processString(req.getSession().getId(), str);
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}

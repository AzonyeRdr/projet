package mg.nathafw.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.nathafw.annotation.MyController;

public class FrontController extends HttpServlet {
    List<Class<?>> list = new ArrayList<>();

    @Override
    public void init() {
        try {
            list = mg.nathafw.util.ClasspathScanner.getClassesAnnotatedWith(MyController.class, "");
        } catch (Exception e) {
            
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<p>Bienvenue dans Natha-FrameWork: "+request.getRequestURL().toString()+"</p>");
        out.println("<ul>");
        for (Class<?> class1 : list) {
            out.println("<li> "+class1.getName()+"</li>");
        }
        out.println("</ul>");
        out.println("</body></html>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

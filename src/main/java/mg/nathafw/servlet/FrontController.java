package mg.nathafw.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;

public class FrontController extends HttpServlet {
    // List<Class<?>> list = new ArrayList<>();
    HashMap<Class<?>, List<Method>> method = new HashMap<>();

    @Override
    public void init() {
        try {
            List<Class<?>> list = mg.nathafw.util.ClasspathScanner.getClassesAnnotatedWith(MyController.class, "");
            method = mg.nathafw.util.ClasspathScanner.scanClass(list, URLAnnotation.class);
        } catch (Exception e) {

        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<p>Bienvenue dans Natha-FrameWork: " + request.getRequestURL().toString() + "</p>");
        out.println("<ul>");
        for (Map.Entry<Class<?>, List<Method>> entry : method.entrySet()) {
            Class<?> clazz = entry.getKey();
            List<Method> methods = entry.getValue();
            out.println("<li>"+(clazz.getName())+"</li>");
            out.println("<ul>");
            if (methods != null) {
                for (Method m : methods) {
                    out.println("<li>" + (m.getName()) + "</li>");
                }
            }
            out.println("</ul>");
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

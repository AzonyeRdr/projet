package mg.nathafw.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.nathafw.err.URLNotSupportedException;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.mapping.URLKey;
import mg.nathafw.mapping.URLProcessor;
import mg.nathafw.util.ScanUtil;

public class FrontController extends HttpServlet {
    private URLProcessor urlProcessor;
    private String controllerPackageName;

    @Override
    public void init() throws ServletException {
        controllerPackageName = getInitParameter("CONTROLLER_PACKAGE");
        if (controllerPackageName == null)
            controllerPackageName = "";
        urlProcessor = new URLProcessor();

        try {
            ScanUtil.fillURLProcessor(controllerPackageName, urlProcessor);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void executeRequest(HttpServletRequest request)
            throws URLNotSupportedException, ReflectiveOperationException {

        String url = getRequestedUrl(request);
        HTTPMethod method = HTTPMethod.buildHTTPMethod(request.getMethod());

        urlProcessor.executeRequest(new URLKey(url, method));
    }

    private String getRequestedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        return uri.substring(context.length());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            executeRequest(request);
            printDebugPage(request, out);
        } catch (URLNotSupportedException e) {
            printError(out, e.toString());
        } catch (ReflectiveOperationException e) {
            printError(out, e.getMessage());
            e.printStackTrace();
        }
        out.close();
    }

    private void printDebugPage(HttpServletRequest request, PrintWriter out) {
        out.println("<html><body>");
        out.println("<h1>Bienvenue dans Natha-FrameWork !</h1>");
        out.println("<p>Vous venez de : " + request.getRequestURL() + "</p>");
        printControllers(out);
        printMappings(out);
        out.println("</body></html>");
    }

    private void printControllers(PrintWriter out) {
        out.println("<h2>Contrôleurs chargés :</h2>");
        out.println("<ul>");
        for (Class<?> controller : urlProcessor.getControllerClasses()) {
            out.println("<li>" + controller.getName() + "</li>");
        }
        out.println("</ul>");
    }

    private void printMappings(PrintWriter out) {
        out.println("<h2>URLs mappées :</h2>");
        out.println("<ul>");
        for (URLKey key : urlProcessor.getUrlMaps().keySet()) {
            out.println("<li>" + key.getMethodHttp() + " " + key.getUrlString() + "</li>");
        }
        out.println("</ul>");
    }

    private void printError(PrintWriter out, String errorMessage) {
        out.println("<html><body>");
        out.println("<h1>Erreur !</h1>");
        out.println("<p>" + errorMessage + "</p>");
        out.println("</body></html>");
    }
}

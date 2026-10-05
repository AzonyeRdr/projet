package mg.nathafw.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.nathafw.err.URLNotSupportedException;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.mapping.URLKey;
import mg.nathafw.mapping.URLProcessor;
import mg.nathafw.util.ModelView;
import mg.nathafw.util.ScanUtil;

public class FrontController extends HttpServlet {
    private static final String DEFAULT_VIEW_PATH = "/WEB-INF/views";
    private static final Set<String> DEFAULT_VIEW_EXTENSIONS = Set.of("jsp");

    private URLProcessor urlProcessor;
    private String controllerPackageName;
    private String viewPath;
    private Set<String> viewExtensions;

    @Override
    public void init() throws ServletException {
        controllerPackageName = getInitParameter("CONTROLLER_PACKAGE");
        if (controllerPackageName == null) {
            controllerPackageName = "";
        }
        viewPath = normalizeViewPath(getInitParameter("VIEW_PATH"));
        viewExtensions = parseViewExtensions(getInitParameter("VIEW_EXTENSIONS"));
        urlProcessor = new URLProcessor();

        try {
            ScanUtil.fillURLProcessor(controllerPackageName, urlProcessor);
        } catch (Exception e) {
            throw new ServletException(e);
        }
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
            throws ServletException, IOException {

        try {
            String url = getRequestedUrl(request);
            HTTPMethod method = HTTPMethod.buildHTTPMethod(request.getMethod());
            URLKey key = new URLKey(url, method);

            Object result = urlProcessor.executeRequest(key,request);

            if (urlProcessor.isAPIRequest(key)) {
                sendJSONResponse(response, result);
            } else if (result instanceof ModelView modelView) {
                renderModelView(request, response, modelView);
            } else {
                response.setContentType("text/html");
                printDebugPage(request, response.getWriter());
            }
        } catch (URLNotSupportedException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Unable to execute the controller method", e);
        }
    }

    private void sendJSONResponse(HttpServletResponse response, Object result)
            throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String jsonResponse = convertToJson(result);
        out.print(jsonResponse);
        out.flush();
    }

    private String convertToJson(Object result) {
        if (result == null) {
            return "null";
        }

        // String
        if (result instanceof String) {
            return "\"" + escapeJsonString((String) result) + "\"";
        }

        // Number
        if (result instanceof Number) {
            return result.toString();
        }

        // Boolean
        if (result instanceof Boolean) {
            return result.toString();
        }

        // Liste
        if (result instanceof List<?> list) {
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                json.append(convertToJson(list.get(i)));
            }

            json.append("]");
            return json.toString();
        }

        // Map
        if (result instanceof Map<?, ?> map) {
            StringBuilder json = new StringBuilder("{");

            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (i++ > 0) {
                    json.append(",");
                }

                json.append("\"")
                        .append(escapeJsonString(String.valueOf(entry.getKey())))
                        .append("\":");

                json.append(convertToJson(entry.getValue()));
            }

            json.append("}");
            return json.toString();
        }
        
        return objectToJson(result);
    }

    private String objectToJson(Object obj) {
        StringBuilder json = new StringBuilder("{");

        Field[] fields = obj.getClass().getDeclaredFields();

        int i = 0;

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                Object value = field.get(obj);

                if (i++ > 0) {
                    json.append(",");
                }

                json.append("\"")
                        .append(escapeJsonString(field.getName()))
                        .append("\":");

                json.append(convertToJson(value));

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        json.append("}");
        return json.toString();
    }

    private String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void renderModelView(
            HttpServletRequest request,
            HttpServletResponse response,
            ModelView modelView) throws ServletException, IOException {

        String view = resolveView(modelView.getDestination());
        for (var attribute : modelView.getAttributes().entrySet()) {
            request.setAttribute(attribute.getKey(), attribute.getValue());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        if (dispatcher == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "View not found: " + view);
            return;
        }
        dispatcher.forward(request, response);
    }

    private String resolveView(String destination) throws ServletException {
        if (destination == null || destination.isBlank()) {
            throw new ServletException("A ModelView destination is required");
        }
        if (destination.contains("\\") || destination.contains("..")) {
            throw new ServletException("Invalid view destination: " + destination);
        }

        String normalizedDestination = destination.startsWith("/")
                ? destination.substring(1)
                : destination;
        int extensionSeparator = normalizedDestination.lastIndexOf('.');
        if (extensionSeparator < 1 || extensionSeparator == normalizedDestination.length() - 1) {
            throw new ServletException("The view destination must include an extension: " + destination);
        }

        String extension = normalizedDestination.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
        if (!viewExtensions.contains(extension)) {
            throw new ServletException("Unsupported view extension: " + extension);
        }
        return viewPath + "/" + normalizedDestination;
    }

    private String normalizeViewPath(String configuredPath) {
        String path = configuredPath == null || configuredPath.isBlank()
                ? DEFAULT_VIEW_PATH
                : configuredPath.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if ("/".equals(path)) {
            return "";
        }
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private Set<String> parseViewExtensions(String configuredExtensions) throws ServletException {
        if (configuredExtensions == null || configuredExtensions.isBlank()) {
            return DEFAULT_VIEW_EXTENSIONS;
        }

        Set<String> extensions = Arrays.stream(configuredExtensions.split(","))
                .map(String::trim)
                .map(extension -> extension.startsWith(".") ? extension.substring(1) : extension)
                .map(extension -> extension.toLowerCase(Locale.ROOT))
                .filter(extension -> !extension.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        if (extensions.isEmpty()) {
            throw new ServletException("VIEW_EXTENSIONS must contain at least one extension");
        }
        return extensions;
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
        out.println("<h2>APIs mappées :</h2>");
        out.println("<ul>");
        for (URLKey key : urlProcessor.getApiMaps().keySet()) {
            out.println("<li>" + key.getMethodHttp() + " " + key.getUrlString() + " (API)</li>");
        }
        out.println("</ul>");
    }
}

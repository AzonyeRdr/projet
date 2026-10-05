package mg.nathafw.mapping;

public enum HTTPMethod {
    GET,
    POST;

    public static HTTPMethod buildHTTPMethod(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return HTTPMethod.GET;
        } else if ("POST".equalsIgnoreCase(method)) {
            return HTTPMethod.POST;
        } else {
            return HTTPMethod.GET;
        }
    }
}

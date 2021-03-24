package webserver;

public enum RequestMethod {
    GET,
    POST;

    public static RequestMethod of(String method) {
        return valueOf(method.toUpperCase());
    }
}

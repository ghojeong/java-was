package webserver;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class RequestBody {
    private final Map<String, String> body;

    public RequestBody(BufferedReader br, int contentLength) throws IOException {
        String requestBody = IOUtils.readData(br, contentLength);
        body = HttpRequestUtils.parseQueryString(requestBody);
    }

    public String get(String key) {
        return body.get(key);
    }
}

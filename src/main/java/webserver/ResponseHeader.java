package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {
    private final String statusCode;
    private final Map<String, String> headers = new HashMap<>();

    public ResponseHeader(String statusCode) {
        this.statusCode = statusCode;
    }

    public ResponseHeader set(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public void writeTo(DataOutputStream dos) throws IOException {
        dos.writeBytes(String.format("HTTP/1.1 %s \r\n", statusCode));
        for (Map.Entry entry : headers.entrySet()) {
            dos.writeBytes(String.format("%s: %s \r\n", entry.getKey(), entry.getValue()));
        }
        dos.writeBytes("\r\n");
    }
}

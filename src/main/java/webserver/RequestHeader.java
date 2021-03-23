package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> headers = new HashMap<>();

    public RequestHeader(BufferedReader br) throws IOException {
        for (String headerLine = br.readLine();
             !headerLine.equals("");
             headerLine = br.readLine()
        ) {
            String[] headerTokens = headerLine.split(": ");
            if (headerTokens.length == 2) {
                put(headerTokens);
            }
        }
    }

    public void put(String[] headerTokens) {
        if (headerTokens.length == 2) {
            headers.put(headerTokens[0], headerTokens[1]);
        }
    }

    public int getContentLength() {
        return Integer.parseInt(
                headers.getOrDefault("Content-Length", "0")
        );
    }
}

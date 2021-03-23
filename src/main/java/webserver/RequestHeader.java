package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private final Map<String, String> headers = new HashMap<>();

    public RequestHeader(BufferedReader br) throws IOException {
        for (String headerLine = br.readLine();
             !headerLine.equals("");
             headerLine = br.readLine()
        ) {
            logger.debug("~~~~~~~~ requestHeader: {} ~~~~~~~~", headerLine);
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

    public String get(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return Integer.parseInt(
                headers.getOrDefault("Content-Length", "0")
        );
    }
}

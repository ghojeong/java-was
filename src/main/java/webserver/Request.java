package webserver;

import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class Request {
    private final RequestMethod method;
    private final String url;
    private final String protocol;
    private final RequestHeader requestHeader;
    private RequestBody requestBody;

    public Request(String requestLine, BufferedReader br) throws IOException {
        String[] splitted = requestLine.split(" ");
        if (splitted.length != 3) {
            throw new RuntimeException("이상한 요청이 들어왔습니다.");
        }
        method = RequestMethod.of(splitted[0]);
        url = splitted[1];
        protocol = splitted[2];

        requestHeader = new RequestHeader(br);

        int contentLength = requestHeader.getContentLength();
        if (contentLength > 0) {
            requestBody = new RequestBody(br, contentLength);
        }
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getQueries() {
        int index = url.indexOf("?");
        String queryString = url.substring(index + 1);
        return HttpRequestUtils.parseQueryString(queryString);
    }
}

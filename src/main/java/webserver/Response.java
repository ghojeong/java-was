package webserver;

import java.io.DataOutputStream;
import java.io.IOException;

public class Response {
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public Response(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Response(ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public void writeTo(DataOutputStream dos) throws IOException {
        responseHeader.writeTo(dos);
        if (responseBody != null) {
            responseBody.writeTo(dos);
        }
    }
}

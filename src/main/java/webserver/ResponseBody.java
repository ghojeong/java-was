package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBody {
    private final byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    public ResponseBody(Path path) throws IOException {
        this.body = Files.readAllBytes(path);
    }

    public void writeTo(DataOutputStream dos) throws IOException {
        int length = body.length;
        if (length < 1) {
            return;
        }
        dos.write(body, 0, body.length);
    }
}

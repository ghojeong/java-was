package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static webserver.RequestMethod.POST;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            Request request = new Request(line, br);
            RequestMethod method = request.getMethod();
            String url = request.getUrl();

            if (url.startsWith("/user/create") && method.equals(POST)) {
                RequestBody requestBody = request.getRequestBody();
                if (requestBody == null) {
                    return;
                }
                User user = new User(requestBody.get("userId"),
                        requestBody.get("password"),
                        requestBody.get("name"),
                        requestBody.get("email")
                );
                log.debug("User : {}", user);

                ResponseHeader responseHeader = new ResponseHeader("302 Found")
                        .set("Location", "/index.html");
                Response response = new Response(responseHeader);
                response.writeTo(new DataOutputStream(out));
                return;
            }

            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            ResponseHeader responseHeader = new ResponseHeader("200 OK")
                    .set("Content-Type", "text/html;charset=utf-8")
                    .set("Content-Length", String.valueOf(body.length));
            ResponseBody responseBody = new ResponseBody(body);
            Response response = new Response(responseHeader, responseBody);
            response.writeTo(new DataOutputStream(out));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

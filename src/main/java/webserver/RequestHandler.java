package webserver;

import model.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static webserver.RequestMethod.GET;
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

            DataOutputStream dos = new DataOutputStream(out);
            if (url.equals("/user/create") && method.equals(POST)) {
                handleUserCreate(request, dos);
            } else if (url.equals("/user/login") && method.equals(POST)) {
                handleUserLogin(request, dos);
            } else if (method.equals(GET)) {
                handleGet(request, dos);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleGet(Request request, DataOutputStream dos) throws IOException {
        String url = request.getUrl();
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        ResponseHeader responseHeader = new ResponseHeader("200 OK")
                .set("Content-Type", "text/html;charset=utf-8")
                .set("Content-Length", String.valueOf(body.length));
        ResponseBody responseBody = new ResponseBody(body);
        Response response = new Response(responseHeader, responseBody);
        response.writeTo(dos);
    }

    private void handleUserCreate(Request request, DataOutputStream dos) throws IOException {
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
        Database.addUser(user);

        ResponseHeader responseHeader = new ResponseHeader("302 Found")
                .set("Location", "/index.html");
        Response response = new Response(responseHeader);
        response.writeTo(dos);
    }

    private void handleUserLogin(Request request, DataOutputStream dos) throws IOException {
        RequestBody requestBody = request.getRequestBody();
        if (requestBody == null) {
            return;
        }
        String userId = requestBody.get("userId");
        String password = requestBody.get("password");
        User user = Database.getUser(userId);
        if (user == null) {
            log.debug("~~~~~~~~ User Not Found! ~~~~~~~~");
            return;
        }
        if (!user.matchPassword(password)) {
            log.debug("~~~~~~~~ Password Mismatch! ~~~~~~~~");
            return;
        }
        log.debug("~~~~~~~~ Login Success! ~~~~~~~~");

        ResponseHeader responseHeader = new ResponseHeader("302 Found")
                .set("Location", "/index.html")
                .set("Set-Cookie", "loggedIn=true");
        Response response = new Response(responseHeader);
        response.writeTo(dos);
    }
}

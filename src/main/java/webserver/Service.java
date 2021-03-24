package webserver;

import model.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Service {
    private static final Logger log = LoggerFactory.getLogger(Service.class);

    public void get(Request request, DataOutputStream dos) throws IOException {
        String url = request.getUrl();
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        String contentType = request.getRequestHeader()
                .get("Accept")
                .split(",")[0];
        ResponseHeader responseHeader = new ResponseHeader("200 OK")
                .set("Content-Type", contentType)
                .set("Content-Length", String.valueOf(body.length));
        ResponseBody responseBody = new ResponseBody(body);
        Response response = new Response(responseHeader, responseBody);
        response.writeTo(dos);
    }

    public void createUser(Request request, DataOutputStream dos) throws IOException {
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

    public void login(Request request, DataOutputStream dos) throws IOException {
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

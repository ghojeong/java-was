package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static webserver.RequestMethod.GET;
import static webserver.RequestMethod.POST;

public class Controller extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final Socket connection;

    private final Service service = new Service();

    public Controller(Socket connectionSocket) {
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
            if (method.equals(POST) && url.equals("/user/create")) {
                service.createUser(request, dos);
            } else if (method.equals(POST) && url.equals("/user/login")) {
                service.login(request, dos);
            } else if (method.equals(GET)) {
                service.get(request, dos);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

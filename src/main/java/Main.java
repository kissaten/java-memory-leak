import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.*;

public class Main extends HttpServlet {

  static final Integer METABYTE = 1000000;
  static final Integer ARRAY_SIZE = METABYTE * 50;

  List<byte[]> bytes = Collections.synchronizedList(new ArrayList<byte[]>());

  List<ByteBuffer> buffers = Collections.synchronizedList(new ArrayList<ByteBuffer>());

  public Main() {
    // seed the memory use
    buffers.add(ByteBuffer.allocateDirect(40000000));
    for (int i = 0; i < 3; i++) {
      byte[] b = new byte[ARRAY_SIZE];
      new Random().nextBytes(b);
      bytes.add(b);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    byte[] b = new byte[ARRAY_SIZE];
    new Random().nextBytes(b);
    bytes.add(b);
    resp.getWriter().print(bytes.size() * (ARRAY_SIZE / METABYTE) + " Megabytes leaked");
  }

  public static void main(String[] args) throws Exception{
    Server server = new Server(Integer.valueOf(System.getenv("PORT")));
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new Main()),"/*");
    server.start();
    server.join();
  }
}

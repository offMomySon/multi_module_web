import java.io.IOException;
import java.net.Socket;

public class Main2 {
    public static void main(String[] args) throws IOException {
        Socket socket1 = new Socket("127.0.0.1", 8080);
        Socket socket2 = new Socket("127.0.0.1", 8080);
        System.out.println("done.");
    }
}

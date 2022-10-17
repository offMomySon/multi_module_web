import java.net.Socket;
import processor.Multiprocessor;

public class App {
    public static void main(String[] args) {
        ClientAccepter clientAccepter = new ClientAccepter();

        Socket socket = clientAccepter.accept();
        Multiprocessor multiprocessor = new Multiprocessor(socket);
        multiprocessor.process();
    }
}

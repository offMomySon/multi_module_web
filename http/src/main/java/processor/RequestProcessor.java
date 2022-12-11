//package processor;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.text.MessageFormat;
//import java.util.concurrent.ThreadPoolExecutor;
//import lombok.extern.slf4j.Slf4j;
//import validate.ValidateUtil;
//
//@Slf4j
//public class RequestProcessor {
//    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor()
//    private final ServerSocket serverSocket;
//
//    public RequestProcessor(int port) {
//        try {
//            log.info("port : {}", port);
//            this.serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
//        }
//    }
//
//    public Socket start() {
//        log.info("ready client connection..");
//        while (true) {
//            Socket socket = null;
//            try {
//                socket = serverSocket.accept();
//                return socket;
//            } catch (IOException acceptFail) {
//                ValidateUtil.validateNull(socket);
//                try {
//                    socket.close();
//                } catch (IOException closeFail) {
//                    throw new RuntimeException(closeFail);
//                }
//                throw new RuntimeException(acceptFail);
//            }
//        }
//    }
//}

import config.HttpConfig;
import config.IpAddress;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import processor.ThreadExecutor;
import request.Uri;
import response.Responser;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public class App {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 500000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
    private static final ClientAccepter clientAccepter = new ClientAccepter(HttpConfig.instance.getPort());
    private static ThreadExecutor threadExecutor = new ThreadExecutor(threadPoolExecutor);
    private static final Responser doesNotLeftThreadResponser = Responser.builder()
        .contentType(Responser.ContentType.TEXT)
        .status(Responser.Status.SERVICE_UNAVAILABLE)
        .contentInputStream(new ByteArrayInputStream("Does not left Thread.".getBytes(UTF_8)))
        .build();


    // main, sub 개념을 잘 살려보자.
    // 동일한 개념에 추상화 해보자.
    // 코드의 겹침, 개념의 겹침 을 잘 생각해보자.

    // clinet 로 부터 connection 대기
    // 필요한 객체 파싱. -> uri, ipAddress
    // 밴ip 여부 확인후 response.
    // content 를 가지고 response 하는 runnable 을 thread 에서 실행.
    public static void main(String[] args) throws IOException {
        while(true){
            Socket socket = clientAccepter.accept();
            log.info("accecpt");





            threadExecutor.execute(()-> {
                String text = "test text";
                Path path = Paths.get("/Users/huni1006/Personal_Project/multi_module_web_server/http/src/main/resources/file/testJPG.jpg");

                boolean exists = Files.exists(path);
                if(exists){
                    log.info("exsit");
                }else{
                    log.info("none");
                }

                File file = new File(path.toUri());

                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Responser responser = Responser.builder()
                    .status(Responser.Status.OK)
                    .contentType(Responser.ContentType.JPG)
                    .contentInputStream(fileInputStream)
                    .build();
                try {
                    responser.send(socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }, ()-> {
                try {
                    doesNotLeftThreadResponser.send(socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }

    }


    public static void main2(String[] args) throws IOException {
        while (true) {
            Socket socket = clientAccepter.accept();
            RequestParser requestParser = RequestParser.parse(socket.getInputStream(), (InetSocketAddress) socket.getRemoteSocketAddress());

            Uri uri = requestParser.getUri();
            IpAddress remoteAddress = requestParser.getRemoteAddress();


//
//            Runnable executeRunnable = () -> {
//                BufferedInputStream contentFinderInputStream = contentFinder.createInputStream(uri.getValue());
//
//                Responser responser = Responser.build().status(Responser.Status.OK)
//                    .contentType(Responser.ContentType.TEXT)
//                    .contentInputStream(contentFinderInputStream)
//                    .build();
//
//                try {
//                    responser.send(socket.getOutputStream());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            };
//
//            Runnable fail = () -> {
//                try {
//                    doesNotLeftThreadResponser.send(socket.getOutputStream());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            };
//
//            threadExecutor.execute(executeRunnable, fail);
        }
    }

    private final List<String> messages = new ArrayList<>();

    public void add(String message) {
        messages.add(message);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                messages.forEach(System.out::println);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

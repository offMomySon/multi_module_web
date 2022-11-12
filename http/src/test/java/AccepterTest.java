//import java.io.IOException;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.Test;
//
//
//class AccepterTest {
//
//    public static String getHttpRequest() {
//        return "GET / HTTP/1.1\n" +
//            "Host: localhost:8080\n" +
//            "Connection: keep-alive\n" +
//            "Cache-Control: max-age=0\n" +
//            "sec-ch-ua: \"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"\n" +
//            "sec-ch-ua-mobile: ?0\n" +
//            "sec-ch-ua-platform: \"macOS\"\n" +
//            "Upgrade-Insecure-Requests: 1\n" +
//            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36\n" +
//            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
//            "Sec-Fetch-Site: none\n" +
//            "Sec-Fetch-Mode: navigate\n" +
//            "Sec-Fetch-User: ?1\n" +
//            "Sec-Fetch-Dest: document\n" +
//            "Accept-Encoding: gzip, deflate, br\n" +
//            "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,la;q=0.6\n" +
//            "\n";
//    }
//
//    @Test
//    public void test1() {
//        System.out.println("start server. read to connection..");
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 8000, TimeUnit.MILLISECONDS,
//                                                                       new LinkedBlockingQueue<>(2));
//
//        for (int i = 0; i < 5; i++) {
//            System.out.println("i : " + i);
//            int finalI = i;
//            threadPoolExecutor.execute(() -> doRequest(finalI));
//
//            System.out.println("active size : " + threadPoolExecutor.getActiveCount());
//            int size = threadPoolExecutor.getQueue().size();
//            System.out.println("queue size : " + size);
//
//        }
//    }
//
//    private static void doRequest(int count) {
//        System.out.println("count : " + count);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("end");
//    }
//
//}
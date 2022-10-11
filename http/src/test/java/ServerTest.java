import org.junit.jupiter.api.Test;


class ServerTest {

    public static String getHttpRequest() {
        return "GET / HTTP/1.1\n" +
            "Host: localhost:8080\n" +
            "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "sec-ch-ua: \"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"\n" +
            "sec-ch-ua-mobile: ?0\n" +
            "sec-ch-ua-platform: \"macOS\"\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
            "Sec-Fetch-Site: none\n" +
            "Sec-Fetch-Mode: navigate\n" +
            "Sec-Fetch-User: ?1\n" +
            "Sec-Fetch-Dest: document\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,la;q=0.6\n" +
            "\n";
    }

    @Test
    public void test1() {


    }

}
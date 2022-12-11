import processor.HttpService;

public class App {
    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        httpService.start();
    }
}
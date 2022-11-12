package request;

import java.net.URL;

public class RequestNewClass {

    public void readClass(){
        URL classLoaderUrl = this.getClass().getClassLoader().getResource("/resources/config/http_config.json");
        System.out.println(classLoaderUrl);

        URL classUrl = this.getClass().getResource("/config/http_config.json");
        System.out.println(classUrl);

//       file:/Users/huni1006/Personal_Project/multi_module_web_server/http/out/production/resources/config/http_config.json
//       file:/Users/huni1006/Personal_Project/multi_module_web_server/http/out/production/resources/config/http_config.json
    }

}

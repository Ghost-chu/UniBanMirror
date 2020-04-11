package rank;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

public class MojangAPI {
    private static Gson gson = new Gson();
    public static String getName(String uuid){
        uuid = uuid.replace("-","");
        try {
            String response = HttpRequest.get(new URL("https://api.mojang.com/user/profiles/"+uuid+"/names"))
                    .execute()
                    .expectResponseCode(200)
                    .returnContent()
                    .asString("UTF-8")
                    .trim();
            UserUUIDResponse[] responseJson = gson.fromJson(response,UserUUIDResponse[].class);
            UserUUIDResponse last = responseJson[responseJson.length-1];
            return last.getName();

        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}

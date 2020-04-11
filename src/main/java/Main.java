import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import rank.BanRanks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    static Config config;
    static File file;
    static Gson gson = new Gson();
    public static File rootPath;
    public static BanRanks banRanks = new BanRanks();
    @SneakyThrows
    public static void main(String[] args) {
        if(System.getProperty("filepath") != null){
            rootPath = new File(System.getProperty("filepath"));
        }else{
            rootPath = new File(".");
        }
        file = new File(rootPath,"config.json");
        System.out.println("UniBan Mirror Service by Ghost_chu. [1.0.1]");
        System.out.println("Current time: "+new Date().toString());
        System.out.println("Reading configuration...");
        config = gson.fromJson(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), Config.class);
        System.out.println("==================================");
        System.out.println(config.getServers().size() + " servers pending for update.");
        config.getServers().forEach(ser -> System.out.println("    * " + ser.getName() +" # "+ser.getDisplay()+ " # " + ser.getUrl() + " # " + ser.getPassword()));
        System.out.println("==================================");
        final Map<Server, String> failed  = new LinkedHashMap<>();
        config.getServers().parallelStream().forEach(server -> {
            System.out.println("Pulling ban-list for server: " + server.getDisplay()+" ...");
            try {
                sync(server);
            } catch (Exception e) {
                System.out.println("Failed to sync for server " + server.getDisplay() +": "+e.getMessage());
                failed.put(server,e.getMessage());
            }
        });
        if(!failed.isEmpty()){
            System.out.println("==================================");
            System.out.println("These servers cannot sync the banlist:");
            failed.forEach((k,v) -> System.out.println(k.getDisplay()));
        }
        System.out.println("==================================");
        System.out.println("Creating works index file...");
        new PageCreation(config.getServers(),failed);
        System.out.println("Works done.");
    }

    public static void sync(@NotNull Server server) throws IOException {
        String data = HttpRequest.get(new URL(server.getUrl()))
                .execute()
                .expectResponseCode(200)
                .returnContent()
                .asString("UTF-8");
        if(data == null){
            throw new RuntimeException("Remote server returned empty response.");
        }
        if (server.getPassword() == null || server.getPassword().isEmpty()) {
            save(server, data);
            return;
        }
        Key key = Encryption.getKeyFromString(server.getPassword());
        String content;
        content = Encryption.decrypt(data, key);
        if(content == null){
            content = "Internal server error";
        }
        save(server, content);
    }

    public static void save(@NotNull Server server, @NotNull String data) throws IOException {
        File file = new File(rootPath,"data");
        file = new File(file, server.getName().toLowerCase());
        file.mkdirs();
        file = new File(file, "get");
        file.delete();
        file.createNewFile();
        Files.write(Paths.get(file.getPath()), data.getBytes(StandardCharsets.UTF_8));
        try {
            String[] bans = gson.fromJson(data,String[].class);
            Arrays.stream(bans).forEach(ban->banRanks.ping(ban));
        }catch (Exception ignored){}
    }

}

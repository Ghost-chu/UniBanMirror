import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;
@AllArgsConstructor
@Data
public class Config {
    private List<Server> servers;

}
@AllArgsConstructor
@Data
class Server {
    private String name;
    private String display;
    private String url;
    private String password;
}
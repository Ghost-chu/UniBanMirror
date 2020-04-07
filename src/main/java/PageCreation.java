import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PageCreation {
    private StringBuilder builder = new StringBuilder();
    public PageCreation(@NotNull List<Server> total, @NotNull Map<Server,String> failed){
        this.buildHead();
        builder.append("<table border=\"3\">");
        builder.append("<td>服务器名称</td><td>服务器镜像订阅命令</td><td>同步状态</td>");
        total.forEach(server->{
            builder.append("<tr>");
            builder.append("<td>");
            builder.append(server.getDisplay());
            builder.append("</td>");
            builder.append("<td>");
            builder.append("/uniban subscribe ").append(getSubUrl(server));
            builder.append("</td>");
            builder.append("<td>");
            builder.append(failed.containsKey(server) ? "同步失败" : "正常");
            builder.append("</td>");
            builder.append("</tr>");
        });
        builder.append("</table>");
        builder.append("<hr />");
        if(!failed.isEmpty()){
            builder.append("<p><i>").append("以下是同步失败的服务器列表及失败原因：</i></p>");
            builder.append("<table border=\"2\">");
            builder.append("<td>服务器名称</td><td>错误信息</td>");
            failed.forEach((k,v)->{
                builder.append("<tr>");
                builder.append("<td>");
                builder.append(k.getDisplay());
                builder.append("</td>");
                builder.append("<td>");
                builder.append(v);
                builder.append("</td>");
                builder.append("</tr>");
            });
        }
        builder.append("<hr />")
                .append("<p>想要将自己的UniBan封禁列表添加到此镜像站? 请将您的列表提交到<a href=\"https://uniban.eumc.cc\">UniBan 公开订阅列表库</a>后耐心等待。</p>")
                .append("<p>如果长时间未同步您的封禁列表，请联系Ghost_chu(admin@mcsunnyside.com)[2908803755]，提醒我进行同步。</p>");
        builder.append("<hr />")
                .append("<p>常见错误：</p>")
                .append("<ul>")
                .append("<li>Unexpected end of file from server - 对方UniBan请求数量已达上限，稍后镜像会再次尝试同步</li>")
                .append("<li>Connection refused (Connection refused) - 对方UniBan未在运行或无法访问</li>")
                .append("</ul>");
        builder.append("</body>")
                .append("</html>");
        File pageFile = new File(Main.rootPath,"index.html");
        try {
            pageFile.delete();
            pageFile.createNewFile();
            Files.write(Paths.get(pageFile.getPath()),builder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getSubUrl(@NotNull Server server){
        return Encryption.encrypt("unibanmirror.mcsunnyside.com/data/"+server.getName().toLowerCase() + ":443@", Encryption.SHARING_KEY);
    }

    private void buildHead(){
        builder.append("<html>")
                .append("<head>")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">")
                .append("<title>")
                .append("阳光之城社区 - UniBan CDN镜像站点")
                .append("</title>")
                .append("<meta name=\"baidu-site-verification\" content=\"IumhkbbtBc\" />")
                .append("</head>")
                .append("<body>");
        builder.append("<h1>").append("阳光之城社区 - SunnySide Community").append("</h1>")
                .append("<h2>").append("UniBan Mirror - CDN镜像服务站").append("</h2>")
                .append("<hr />")
                .append("<p>本镜像站每 10 分钟抓取一次数据，由于CDN缓存，更新时间可能长于 10 分钟。</p>")
                .append("<p>最后更新时间：").append(new Date().toString()).append("</p>")
                .append("<p>本站点仅用于数据分发，对数据来源概不负责，请谨慎订阅！</p>").append("<hr />");

    }
}

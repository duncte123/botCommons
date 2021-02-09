## Usage instructions

### EmbedUtils

Setting the default embed builder:
```java
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;

class SettingBuilderExample {
    public void setBuilderExample() {
      EmbedUtils.setEmbedBuilder(
          () -> new EmbedBuilder()
              .setFooter("Default footer that is present on all embeds")
      );
    }
}
```

Creating an embed:
```java
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

class SendingMessageExample {
    public void sendMessageExample(TextChannel channel) {
      MessageEmbed embed = EmbedUtils.embedMessage("My message here").build();
      
      channel.sendMessage(embed).queue();
    }
}
```

Creating an embed with image:
```java
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

class SendingMessageExample {
    public void sendMessageExample(TextChannel channel) {
      String url = "https://cdn.duncte123.me/AN-wr625PaolD";
      MessageEmbed embed = EmbedUtils.embedImage(url).build();
      
      channel.sendMessage(embed).queue();
    }
}
```

### WebUtils

```java
import me.duncte123.botcommons.web.WebUtils;

class WebUtilsJsonExample {
    public void jsonExample() {
        WebUtils.setUserAgent("MyApp/1.0");
        
        WebUtils.ins.getJSONObject("https://apis.duncte123.me/user-agent").async(
            (json) -> System.out.println(json.get("data").get("user-agent").asText()) // Expected output: MyApp/1.0
        );
    }
}
```

```java
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.botcommons.web.requests.JSONRequestBody;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

class WebUtilsJsonExample {
    public void jsonExample() {
        WebUtils.setUserAgent("MyApp/1.0");
        
        MessageEmbed embed = new EmbedBuilder().build();
        DataObject data = embed.toData();
        // Available bodies are EmptyFromRequestBody, FromRequestBody, JSONRequestBody, PlainTextRequestBody
        // Or use the IRequestBody interface to write your own
        JSONRequestBody body = JSONRequestBody.fromDataObject(data);
        
        WebUtils.ins.postRequest("https://httpbin.org/post", body).async(
            (json) -> System.out.println(json) // Do something with the result
        );
    }
}
```

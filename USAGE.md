## Usage instructions

### EmbedUtils

Setting the default embed builder:
```java
import me.duncte123.botcommons.EmedUtils;
import net.dv8tion.jda.core.EmbedBuilder;

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
import me.duncte123.botcommons.EmedUtils;
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
import me.duncte123.botcommons.EmedUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

class SendingMessageExample {
    public void sendMessageExample(TextChannel channel) {
      MessageEmbed embed = EmbedUtils.embedImage("https://cdn.duncte123.me/Lw6XIw2gYVEe7j4D3eEXYehqhADm3dTq7VEV3RTb7jXBIjKD7nES1R8vIVFG2Z3mYeqG1G").build();
      
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

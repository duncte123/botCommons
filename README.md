# BotCommons [![BuildStatus][circleImage]][circleLink]
A set of tools for the [JDA] library


## Adding to your project
The current latest version is: [ ![version][] ][download]

## Usage

Usage instructions can be found [here][usage] with some examples in the [tests folder](src/test/java/me/duncte123/botcommons)

## Bot not shutting down?
A shutdown method was created in the `BotCommons` class. <br>
This method also accepts your JDA or ShardManager instance for killing the threads that OkHttp created, because of these running threads your bot will not shut down.


#### With gradle
[ ![version][] ][download]

```GRADLE
repositories {
    maven {
        name 'm2-duncte123'
        url 'https://m2.duncte123.dev/releases'
    }
}

dependencies {
    implementation group: 'me.duncte123', name: 'botCommons', version: '[VERSION]'
}
```

#### With maven

```XML
<repository>
    <id>m2-duncte123</id>
    <name>m2-duncte123</name>
    <url>https://m2.duncte123.dev/releases</url>
</repository>

<dependency>
  <groupId>me.duncte123</groupId>
  <artifactId>botCommons</artifactId>
  <version>[VERSION]</version>
</dependency>
```

Make sure to replace `[VERSION]` with the version listed above.

[JDA]: https://github.com/DV8FromTheWorld/JDA
[version]: https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fm2.duncte123.dev%2Freleases%2Fme%2Fduncte123%2FbotCommons%2Fmaven-metadata.xml
[download]: https://m2.duncte123.dev/#/releases/me/duncte123/botCommons
[usage]: USAGE.md
[circleLink]: https://github.com/duncte123/botCommons
[circleImage]: https://github.com/duncte123/botCommons/workflows/release-botcommons/badge.svg

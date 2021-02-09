# BotCommons [![CircleCI][circleImage]][circleLink]
A set of tools for the [JDA] library


## Adding to your project
The current latest version is: [ ![version][] ][download]

## Usage

Usage instructions can be found [here][usage]

## Bot not shutting down?
A shutdown method was created in the `BotCommons` class. <br>
This method also accepts your JDA or ShardManager instance for killing the threads that OkHttp created, because of these running threads your bot will not shut down.


#### With gradle

```GRADLE
repositories {
    maven {
        name 'duncte123-jfrog'
        url 'https://duncte123.jfrog.io/artifactory/maven'
    }
}

dependencies {
    implementation group: 'me.duncte123', name: 'botCommons', version: '[VERSION]'
}
```

#### With maven

```XML
<repository>
    <id>jfrog-duncte123</id>
    <name>jfrog-duncte123</name>
    <url>https://duncte123.jfrog.io/artifactory/maven</url>
</repository>

<dependency>
  <groupId>me.duncte123</groupId>
  <artifactId>botCommons</artifactId>
  <version>[VERSION]</version>
</dependency>
```

Make sure to replace `[VERSION]` with the version listed above.

[JDA]: https://github.com/DV8FromTheWorld/JDA
[version]: https://api.bintray.com/packages/duncte123/maven/botcommons/images/download.svg
[download]: https://bintray.com/duncte123/maven/botcommons/_latestVersion
[usage]: USAGE.md
[circleLink]: https://circleci.com/gh/duncte123/botCommons/tree/master
[circleImage]: https://circleci.com/gh/duncte123/botCommons/tree/master.svg?style=shield

# BotCommons [![CircleCI][circleImage]][circleLink]
A set of tools for the [JDA] library


## Adding to your project
The current latest version is: [ ![version][] ][download]

#### With gradle

```GRADLE
repositories {
    jcenter()
}

dependencies {
    implementation group: 'me.duncte123', name: 'botcommons', version: '[VERSION]'
}
```

#### With maven

```XML
<repository>
    <id>jcenter</id>
    <name>jcenter-bintray</name>
    <url>http://jcenter.bintray.com</url>
</repository>

<dependency>
  <groupId>me.duncte123</groupId>
  <artifactId>botcommons</artifactId>
  <version>[VERSION]</version>
  <type>pom</type>
</dependency>
```

Make sure to replace `[VERSION]` with the version listed above.

## Usage

Usage instructions can be found [here][usage]

[JDA]: https://github.com/DV8FromTheWorld/JDA
[version]: https://api.bintray.com/packages/duncte123/maven/botcommons/images/download.svg
[download]: https://bintray.com/duncte123/maven/botcommons/_latestVersion
[usage]: USAGE.md
[circleLink]: https://circleci.com/gh/duncte123/botCommons/tree/master
[circleImage]: https://circleci.com/gh/duncte123/botCommons/tree/master.svg?style=shield

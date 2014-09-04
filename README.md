# Flow NBT [![License](http://img.shields.io/badge/license-MIT-lightgrey.svg?style=flat)][License] [![Flattr this](http://img.shields.io/badge/flattr-donate-lightgrey.svg?style=flat)][Donate] [![Build Status](http://img.shields.io/travis/flow/flow-nbt/master.svg?style=flat)](https://travis-ci.org/flow/flow-nbt) [![Coverage Status](http://img.shields.io/coveralls/flow/flow-nbt/master.svg?style=flat)](https://coveralls.io/r/flow/flow-nbt)

Flow NBT is a NBT library based on Graham Edgecombe's JNBT library. NBT (Named Binary Tag) is a tag based binary format designed to carry large amounts of binary data with smaller amounts of additional data.

## Getting Started
* [Examples and code snippets](https://github.com/flow/examples/tree/master/nbt)
* [Official documentation](#documentation)
* [IRC support chat](http://kiwiirc.com/client/irc.esper.net/flow)
* [Issues tracker](https://github.com/flow/flow-nbt/issues)

## Source Code
The latest and greatest source can be found here on [GitHub](https://github.com/flow/flow-nbt). If you are using Git, use this command to clone the project:

    git clone git://github.com/flow/flow-nbt.git

Or download the [latest zip archive](https://github.com/flow/flow-nbt/archive/master.zip).

## Dependencies
We love open-source libraries! This project uses are few of them to make things easier. If you aren't using Maven or Gradle, you'll need these!
* [commons-io:commons-io](https://oss.sonatype.org/#nexus-search;gav~commons-io~commons-io~~~)
* [org.apache.commons:commons-lang3](https://oss.sonatype.org/#nexus-search;gav~org.apache~commons-lang3~~~)
* [org.yaml:snakeyaml](https://oss.sonatype.org/#nexus-search;gav~org.yaml~snakeyaml~~~)

## Test Dependencies
The following dependencies are only needed if you compiling the tests included with this project. Gotta test 'em all!
* [junit:junit](https://oss.sonatype.org/#nexus-search;gav~junit~junit~~~)
* [org.hamcrest:hamcrest-library](https://oss.sonatype.org/#nexus-search;gav~org.hamcrest~hamcrest-library~~~)
* [org.powermock:powermock-api-mockito](https://oss.sonatype.org/#nexus-search;gav~org.powermock~powermock-api-mockito~~~)
* [org.powermock:powermock-module-junit4](https://oss.sonatype.org/#nexus-search;gav~org.powermock~powermock-module-junit4~~~)

## Building from Source
This project can be built with the _latest_ [Java Development Kit](http://oracle.com/technetwork/java/javase/downloads) and [Maven](http://maven.apache.org/) or [Gradle](http://www.gradle.org/). Maven and Gradle are used to simplify dependency management, but using either of them is optional.

For Maven, the command `mvn clean package` will build the project and will put the compiled JAR in `target`, and `mvn clean install` will copy it to your local Maven repository.

For Gradle, the command `gradlew` will build the project and will put the compiled JAR in `~/build/distributions`, and `gradlew install` will copy it to your local Maven repository.

## Contributing
Are you a talented programmer looking to contribute some code? We'd love the help!

* Open a pull request with your changes, following our [guidelines and coding standards](CONTRIBUTING.md).
* Please follow the above guidelines for your pull request(s) accepted.
* For help setting up the project, keep reading!

Love the project? Feel free to [donate] to help continue development! Flow projects are open-source and powered by community members, like yourself. Without you, we wouldn't be here today!

Please don't forget to follow and star our repo! Join our growing community to keep up to date with the latest Flow development.

## Usage
If you're using [Maven](http://maven.apache.org/download.html) to manage project dependencies, simply include the following in your `pom.xml` file:

    <dependency>
        <groupId>com.flowpowered</groupId>
        <artifactId>flow-nbt</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

If you do not already have the required repo in your repository list, you will need to add this as well:

    <repository>
        <id>sonatype-nexus</id>
        <url>https://oss.sonatype.org/content/groups/public</url>
    </repository>

If you're using [Gradle](http://www.gradle.org/) to manage project dependencies, simply include the following in your `build.gradle` file:

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = 'sonatype-nexus'
            url = 'https://oss.sonatype.org/content/groups/public/'
        }
    }
    dependencies {
        compile 'com.flowpowered:flow-nbt:1.0.0-SNAPSHOT'
    }

If you'd prefer to manually import the latest .jar file, you can get it [here](https://github.com/flow/flow-nbt/releases).

## Documentation
Want to get friendly with the project and put it to good use? Check out the latest [Javadocs](http://nbt.flowpowered.com/).

To generate the the Javadocs with Maven, use the `mvn javadoc:javadoc` command. To view the javadocs simply go to `target/site/apidocs/` and open `index.html` in a web browser.

To generate Javadocs with Gradle, use the `gradlew javadoc` command. To view the javadocs simply go to `build/docs/javadoc/` and open `index.html` in a web browser.

## Version Control
We've adopted the [git flow branching model](http://nvie.com/posts/a-successful-git-branching-model/) in our projects. The creators of git flow released a [short intro video](http://vimeo.com/16018419) to explain the model.

The `master` branch is production-ready, but is not yet vetted for release. Only small patches and `hotfix/x` branches are pushed to `master`, and will always have a release version. The `develop` and `stage` branches are pre-production, and are where we push `feature/x` branches for testing.

Our release branches are named by version number, e.g. `1.0` and `1.1`. We begin release branches with a beta (b1) designation and progress them through release candidate to stable. All open source releases (included pre-releases) are tagged.

## Legal Stuff
Flow NBT is licensed under the [MIT License][License]. Basically, you can do whatever you want as long as you include the original copyright. Please see the `LICENSE.txt` file for details.

## Credits
* [Spout](https://spout.org/) and contributors - *where we all began, and for much of the re-licensed code.*
* All the people behind [Java](http://www.oracle.com/technetwork/java/index.html), [Maven](http://maven.apache.org/), and [Gradle](http://www.gradle.org/).

[Donate]: https://flattr.com/submit/auto?user_id=spout&url=https://github.com/flow/flow-nbt&title=Flow+NBT&language=Java&tags=github&category=software
[License]: https://tldrlegal.com/license/mit-license

# iDrop

iDrop is a graphical multi-platform file transfer client suitable for moving large files, doing basic synchronization, and other
tasks too 'heavy' for a standard web interface.

## Requirements

- Java 8+
- Maven

## Build

As a convenience, this project provides two scripts to make building the JAR file simple. Use of these scripts also keeps your .m2 directory clean. The scripts use Docker to achieve this.

> [!NOTE]
> For those who wish to build the JAR file without Docker, execute the normal Maven build steps _(consider passing `-Dmaven.test.skip=true`)_.

To build on Linux, run the following.

```
bash build_jar.sh
```

To build on Windows, open PowerShell or the Command Prompt and run this instead.

```
build_jar.cmd
```

## Run

```
java -jar idrop-swing/target/idrop-swing-<version>.jar
```

# launcher
## Requirements

To compile native images (such as EXE, MSI, DPKG, etc.), you need:

- **WiX Toolset** ([Download & Documentation](https://wixtoolset.org/docs/wix3/)) on Windows

## Presentation Video

[Watch on YouTube](https://www.youtube.com/watch?v=SocKuHWuOe8)

## Structure required by your server for the launcher

http://pathToLauncherFolder/launcher/
    - fabric-loader-0.14.21-1.16.5.json // Or any other JSON version of Minecraft
    - ignore.cfg
    - delete.cfg
    - files/ // Contains any kind of Minecraft files (Optional sometimes you just want an empty game)
        - mods/
        - config/
        - etc...

## Ignore.cfg (Exemple)
### These files WILL NOT be deleted beacuse by default the launcher delete every files except some predefined files
```
bin/launcher_config.json

bin/game/config/
bin/game/saves/
bin/game/content-packs/
bin/game/shaderpacks/
bin/game/config-client.json
bin/game/config-server.json
```

## Delete.cfg (Exemple)
### These files WILL BE deleted (Forced deletion)
```
libraries/com/google/guava/guava/15.0/guava-15.0.jar
```

## To build the launcher
You can simply open the terminal in the `launcher` folder and run:
```bash
./gradlew build
```
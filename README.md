# Splitty

## Run instructions
1. Start the server using ./gradlew bootRun
2. Start the client in a seperate terminal using ./gradlew run, while the server is running

## Splitty Configuration File Location
The Splitty application stores its configuration file in a location that varies depending on your operating system. 
### Windows
On Windows, the configuration file is located in the Application Data directory specific to your user account.

Path:
`%APPDATA%\Splitty\config.json`

### macOS

For macOS users, Splitty places its configuration file in the Application Support directory within your user's Library folder.

Path:
`~/Library/Application Support/Splitty/config.json`

### Linux and Other Unix-like Systems
On Linux and other Unix-like systems, Splitty searches for the `XDG_CONFIG_HOME` environment variable to determine the base directory for user-specific configuration files. If `XDG_CONFIG_HOME` is set and not empty, the configuration path will be `$XDG_CONFIG_HOME/Splitty/config.json`. If `XDG_CONFIG_HOME` is not set, it defaults to `~/.config`.

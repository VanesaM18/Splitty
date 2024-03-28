# Splitty

## Run instructions
1. Start the server using ./gradlew bootRun
2. Start the client in a seperate terminal using ./gradlew run, while the server is running
## Email Configuration Guide
To utilize the email functionality within our application, you will need to update the configuration file with your email address and a unique application-specific password. This password is not the same as your regular password used for internet access or email login; it's specifically generated for third-party application access.

### Important: Application-Specific Password Requirement
An application-specific password is required for enhanced security, especially when your email provider supports or requires two-factor authentication (2FA). This type of password ensures that your primary email password remains secure while allowing specific applications to access your email account.

### Generating an Application-Specific Password for Gmail Users
If you are using Gmail, follow these steps to generate your application-specific password:

1. Enable Two-Factor Authentication (2FA): Visit your Google Account settings to activate 2FA. This adds an extra layer of security to your account, and it's required by next step.
2. Generate Application-Specific Password:
   - Navigate to Google App Passwords page. (https://myaccount.google.com/apppasswords)
   - Choose or enter the name of the application you're granting access to, for example, "Splitty".
   - Follow the prompts to generate a new password. This password will be displayed on your screen.
3. Update Configuration File:
   - Copy the newly generated password.
   - Open the application's configuration file and input your email address and the copied password in the designated sections.

By following these steps, you enable your application to securely use your Gmail account for email functionalities. Ensure to store your application-specific password in a safe place, as it grants access to your email account

## Splitty Configuration File Location
The Splitty application stores its configuration file in a location that varies depending on your operating system. 

If it is the first time running the application, it is required to run the application at least once for the config file to show.

If the application you have installed just had a major update, it's recommended to delete the config file in case of unexpected behaviour, and try to run again.
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

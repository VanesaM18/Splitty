# :moneybag: Splitty

[TOC]

## :icecream: Requirements
To run Splitty, the following things are required:
- Java 21
- JavaFX 22 (note: 22, NOT 21!)
  - This should be downloaded automatically by gradle when running using the instructions provided below.
## :runner: Run instructions
> **TIP**:
> Before trying to run Splitty please make sure you have all the requirements installed.
> If you are having trouble running the project using these instructions, please checkout the [troubleshooting](#troubleshooting) section. Also make sure you fulfill all the [requirements](#requirements).

There are two main ways of running Splitty:
1. Using the terminal (recommended)
2. Using IntelliJ

### :keyboard: Terminal (recommended)
1. Open up a terminal and navigate to the directory containing the Splitty source code.
2. Enter `./gradlew bootRun` into the terminal. Then, wait untill the server has fully started (which is when login username and password appear in the output)
3. Open up a different terminal and once again navigate to the directory containing the Splitty source code.
4. Enter `./gradlew run` into the terminal. The client should now launch.

### :construction_worker: IntelliJ
1. Open up the directory containing the code in IntelliJ.
2. Navigate to the Gradle panel on the right of the window. The icon looks like an elephant.
3. Expand the hierarchy so the following items are all visible: app > Client > Tasks > application > run and app > Server > Tasks > application > bootRun
4. Now, first start the server by double clicking on the item located at `app > Server > Tasks > application > bootRun`
5. Wait for the server to finish starting up. You know the server has started when a login username and password are shown in the output.
6. Start the client by double clicking on the item located at `app > Server > Tasks > application > run`

## :package: Implemented features
### :pouch: Implemented feature bundles
- Basic requirements
- Live Language Switch
- Detailed Expenses
- Open Debts
- Statistics
- Email notification

### :wheelchair: HCI/Accessibility
- Accessibility
   - Good color contrast. All text is always readable.
   - There are keyboard shortcuts to navigate through the app:
      - Return to the main menu from an event (Using the escape button, this works on most screens)
      - Create a new event (Press enter while in the event name box)
      - Add an expense to current event (ctrl+e)
      - Show statistics for current event (ctrl+s)
   - At least 3 elements of the interface are multi-modal.
- Navigation
   - We made the navigation as logical as possible.
   - It is possible to navigate the app without using a mouse by repeatedly pressing tab (or shift+tab to go backwards) until the correct UI item is focused, and then interact with the element.
      - When a button is focused, press enter to press it.
      - When a text input box is focused, simply type to enter text into it.
      - When a dropdown box is focused, use the up and down arrow keys to select an item.
- User feedback:
   - Deletion requires a confirmation.

### Long polling/websockets
The implementation of long polling can be found in `OpenDebtsCtrl.java`. Websockets can be found in `WebSocketHandler.java` and `MyWebSocketClient.java`.

## :wrench: Troubleshooting
If you are having trouble running Splitty, please try the following things. They are in order of invasiveness.
Before trying any of these things, make sure both the client and server are not running!
- Double check you have all the requirements installed. See the [requirements](#requirements).
- Delete the project gradle cache using `./gradlew clean`
- Delete the Splitty config file. See [here](#splitty-configuration-file-location) for the location of the config file.
- Delete the Splitty database. This file is located at `server/h2-database.mv.db`
- Delete the global gradle cache by deleting the folder caches in `~/.gradle/caches/` or (`C:\Users\YourUserName\.gradle\caches`)
  - After executing this command, all dependencies will need to be redownloaded next time you try to run any gradle project.

## :email: Email Configuration Guide
To utilize the email functionality within our application, you will need to update the configuration file with your email address and a unique application-specific password. This password is not the same as your regular password used for internet access or email login; it's specifically generated for third-party application access.

### :key: Important: Application-Specific Password Requirement
An application-specific password is required for enhanced security, especially when your email provider supports or requires two-factor authentication (2FA). This type of password ensures that your primary email password remains secure while allowing specific applications to access your email account.

### :family: Generating an Application-Specific Password for Gmail Users
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

## :world_map: Splitty Configuration File Location
The Splitty application stores its configuration file in a location that varies depending on your operating system. 

If it is the first time running the application, it is required to run the application at least once for the config file to show.

If the application you have installed just had a major update, it's recommended to delete the config file in case of unexpected behaviour, and try to run again.
### :microscope: Windows
On Windows, the configuration file is located in the Application Data directory specific to your user account.

Path:
`%APPDATA%\Splitty\config.json`

### :apple: macOS

For macOS users, Splitty places its configuration file in the Application Support directory within your user's Library folder.

Path:
`~/Library/Application Support/Splitty/config.json`

### :penguin: Linux and Other Unix-like Systems
On Linux and other Unix-like systems, Splitty searches for the `XDG_CONFIG_HOME` environment variable to determine the base directory for user-specific configuration files. If `XDG_CONFIG_HOME` is set and not empty, the configuration path will be `$XDG_CONFIG_HOME/Splitty/config.json`. If `XDG_CONFIG_HOME` is not set, it defaults to `~/.config`.

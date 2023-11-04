# Osano Test

This project is used to showcase functionalities in the Osano API Integration Exercise. For frontend display; the Vaadin Framework is utilized with hosting done by Gretty in the backend. This will allow some easy to use frontend fuctionality and tools while validating the project. The actual "meat" of the project can be found within the `DataService` and `CurrencyLookupView` classes. 

## Running With Gretty In Development Mode

Run the following command in this repo:

```bash
./gradlew clean appRun
```

Once you see the message which tells you Jetty has started and is listening on port 8080; usually provided with a link,  you can then open [http://localhost:8080](http://localhost:8080) with your browser.

## Building In Production Mode

Simply run the following command in this repo:

```bash
./gradlew clean build -Pvaadin.productionMode
```

That will build this app in production mode as a WAR archive; please find the
WAR file in `build/libs/base-starter-gradle.war`. You can run the WAR file
by using [Jetty Runner](https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-runner):

```bash
cd build/libs/
wget https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/11.0.15/jetty-runner-11.0.15.jar
java -jar jetty-runner-11.0.15.jar base-starter-gradle.war
```

Now you can open the [http://localhost:8080](http://localhost:8080) with your browser.

### Building In Production On CI

Usually the CI images will not have node.js+npm available. Luckily Vaadin will download `nodejs` and `npm/pnpm` automatically, there is nothing you need to do.
To build your app for production in CI, just run:

```bash
./gradlew clean build -Pvaadin.productionMode
```

## Running/Debugging In Intellij Ultimate With Tomcat in Development Mode

* Download and unpack the newest [Tomcat 10](https://tomcat.apache.org/download-10.cgi).
* Open this project in Intellij Ultimate.
* Click "Edit Launch Configurations", click "Add New Configuration" (the upper-left button which looks like a plus sign `+`), then select Tomcat Server, Local.
  In the Server tab, the Application Server will be missing, click the "Configure" button and point Intellij to the Tomcat directory.
    * Still in the launch configuration, in the "Deployment" tab, click the upper-left + button, select "Artifact" and select `base-starter-gradle.war (exploded)`.
    * Still in the launch configuration, name the configuration "Tomcat" and click the "Ok" button.

Now make sure Vaadin is configured to be run in development mode - run:

```bash
./gradlew clean vaadinPrepareFrontend
```

* Select the "Tomcat" launch configuration and hit Debug (the green bug button).

If Tomcat fails to start with `Error during artifact deployment. See server log for details.`, please:
* Go and vote for [IDEA-178450](https://youtrack.jetbrains.com/issue/IDEA-178450).
* Then, kill Tomcat by pressing the red square button.
* Then, open the launch configuration, "Deployment", remove the (exploded) war, click `+` and select `base-starter-gradle.war`.

## Running/Debugging In Intellij Community With Gretty in Development Mode

Make sure Vaadin is configured to be run in development mode - run:

```bash
./gradlew clean vaadinPrepareFrontend
```

In Intellij, open the right Gradle tab, then go into *Tasks* / *gretty*, right-click the *appRun* task and select Debug.
Gretty will now start in debug mode, and will auto-deploy any changed resource or class.

There are a couple of downsides:
* Even if started in Debug mode, debugging your app won't work.
* Pressing the red square "Stop" button will not kill the server and will leave it running.
  Instead, you have to focus the console and press any key - that will kill Gretty cleanly.
* If Gretty says "App already running", there is something running on port 8080.
  See above on how to kill Gretty cleanly. Failing that, use the command `netstat -ano | findstr 8080` and then `taskkill /F /pid <PID_OF_LISTENING_SERVICE>`. Naturally; these commands will differ slightly on Unix Systems. 
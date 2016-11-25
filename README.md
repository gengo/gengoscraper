# Gengo Scraper
## About
Gengo Scraper is an application to retrieve info from Gengo website by scraping its contents.

Currently it retrieves info about archived jobs.

## Download and use on Windows environment
- Download resources under **run** folder:
  - gengoscraper-1.0.0.jar
  - config.properties
  - runme.bat
- Ensure that you have Java 8 installed.
- Edit **config.properties** to set your user, password and max number of pages to retrieve.
- Run **runme.bat**.
- The results will be stored in a file called **results.csv** in the same folder.

## Download and use on other environments
- Same above steps, but instead of running **runme.bat** you can run the JAR file directly:

```
java -jar ./gengoscraper-1.0.0.jar --spring.config.location=file:./config.properties
```

## If you prefer to compile
- Ensure that you have Java 8 installed.
- Download sources under **src** folder.
- Compile them with Maven: **mvn package**
- The compiled binaries will be generated into **target** folder.
- Create a **config.properties** like the one you can find under **run** folder and configure it.
- Run the application as stated above.

## Configuration
These are the basic parameters you can configure in the **config.properties** file:
- *gengo.user* -> Your Gengo user. Must not be empty
- *gengo.password* -> Your Gengo password. Must not be empty.
- *gengo.max.pages* -> Max number of pages of archived jobs to retrieve. Each page contains 20 archived jobs.

Please notice that your credentials should be 'standard' Gengo credentials. The scraper is not able to deal with Google login or Facebook login yet.

By default, the results are stored in a **results.csv** file in the same folder. You can set your own file adding this parameter:
- *gengo.results.file* -> Complete path and file name to store the results into.

By default, the application generates logs into a **gengo.log** file in the same folder. You can set your own file adding this parameter:
- *logging.file* -> Complete path and file name to generate the logs into.
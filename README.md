# myblog-spring-boot
This is a simple 2-page site. The project if for educational goals to study Spring Boot.

# Description
The application is written using the Spring boot (3.4.2 version)
This application can be running included in web starter servlet container. I used the Tomcat.

First of all the correct folder for upload images should be specified.
The property `application.images.upload-directory-location-path` must be
specified as an absolute path to existing folder with write/read permissions for follow
uploading images that will be shown in the posts.

```properties
application.images.upload-directory=/home/user/myblog/upload
```

The application works using H2 in memory database.
All settings for it are in `application.properties` file. 

# Build the project
```shell
cd ./myblog-site
./gradlew build
```

Then find an executable file in ./myblog-site/build/lib folder

# Run application
```shell
./myblog-site/build/libs/myblogapp
```

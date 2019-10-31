# dynamic-custom-field using https://github.com/krishagni/dynamic_extensions library.
# Steps
1. Run Dynamic Extension Liquibase using following command.
```
liquibase.bat --driver=com.mysql.jdbc.Driver --classpath=D:\apache-tomcat-8.5.37\lib\mysql-connector-java-5.1.38.jar --changeLogFile=db.changelog-master.xml --url="jdbc:mysql://localhost:3306/demo_app?autoReconnect=true&amp;useSSL=false" --username=root --password=password update
```
2. Run Application

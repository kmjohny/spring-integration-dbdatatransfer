## Spring integration database data transfer.

```
gradle bootrun -PjvmArgs="-Dspring.profiles.active=<PROFILE> -Dspring.config.location=<APPLICATION-SECRETS>"
gradle bootrun -PjvmArgs="-Dspring.profiles.active=h2 -Dspring.config.location=/Users/mathewj3/workspace/github/application-secrets/spring-integration-dbdatatransfer.yml"
```
PROFILE:- options are h2, mssql
APPLICATION-SECRETS:- Location to the secrets file. 

```
java -jar -Dspring.profiles.active=h2 -Dspring.config.location=/Users/mathewj3/workspace/github/application-secrets/spring-integration-dbdatatransfer.yml build/libs/spring-integration-datatransfer-0.0.1-SNAPSHOT.jar
```


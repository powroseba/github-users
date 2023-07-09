## How to start

**Requirements:**

* java 17
* maven

1. Go to main directory and build executable jar by running a command

```
mvn clean package
```

2. Run application by command

```
java -jar target/github-users.jar
```

3. Open browser on address http://localhost:8090/users/{login}

   *on placeholder 'login' paste example github user login*

    <br>
    Response:
   
   ```json
   {
      "id": 583231,
      "name": "The Octocat",
      "type": "User",
      "avatarUrl": "https://avatars.githubusercontent.com/u/583231?v=4",
      "createdAt": "2011-01-25 18:44:36+0000",
      "login": "octocat",
      "calculations": [
          {
              "description": "Empik calculation",
              "value": 0.006163961372508733
          },
          {
              "description": "All code sources count",
              "value": 16
          }
      ]
   }
    ```
   1. id - identifier of github user
   2. calculations - list of the performed calculations with theirs descriptions


4. Every request is counted and saved into database per requested login, 
   application is using h2 database and serving ui to perform database queries. 
   To check database state follow below steps. 
   1. Go to website http://localhost:8090/h2-console,
   2. Login into user 'users' with password 'password'
   3. Fetch data from suitable table by command ```SELECT * FROM LOGIN_REQUESTS;```


   
## Description

An alternative to the rebuild Jenkins plugin, which currently converts any custom parameter into a string parameter. This makes it difficult to add logic for correlated arguments (a change in one should affect the other) or arguments that you don't want to be updated if they were set in a previous build.


### Getting a Jenkins instance running locally:
* Run jenkins inside of Docker:
    ```bash
    docker run -d \
        --name jenkins \
        -p 8081:8080  \
        -p 50001:50000 \
        -v jenkins_home:/var/jenkins_home \
        jenkins/jenkins:2.462.3
    ```

* Get Initial Admin Password
    ```bash
    docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
    ```

* Restart Jenkins:
    ```bash
    docker restart jenkins
    ```

* Clean up:
    ```bash
    docker stop jenkins
    docker rm jenkins
    docker volume rm jenkins_home
    ``` 

### Reference:
* [Active Choice Plugin's docs](https://wiki.jenkins.io/JENKINS/Active-Choices-Plugin.html)
* [Scriptler plugin's docs](https://plugins.jenkins.io/scriptler/)

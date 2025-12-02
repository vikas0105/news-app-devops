pipeline {
    // agent { label 'java' }
    //agent none
    agent any
    stages {  
        stage('checkout') {
            steps {
                sh "rm -rf news-app-devops"
              sh "git clone https://github.com/pradeepreddy-hub/hello-world-war"
            }
        }
        stage ('build') {
            steps {
                sh "mvn clean package"           
            }
        }
        stage ('test') {
            steps {
                sh "mvn test"
            }
        }
        stage ('deploy') {
            steps {
                sh "sudo cp /home/ubuntu/news-app-devops/target/news-app.war /opt/tomcat10/webapps/"
            }
        }
    }
}

pipeline {
    // agent { label 'java' }
    //agent none
    agent any
    stages {  
        stage('checkout') {
            agent { label 'java' }
            steps {
                sh "rm -rf news-app-devops"
              sh "git clone https://github.com/pradeepreddy-hub/hello-world-war"
            }
        }
        stage ('build') {
            agent { label 'java' }
            steps {
                sh "mvn clean package"           
            }
        }
        stage ('deploy') {
            agent { label 'java' }
            steps {
                sh "sudo cp /home/slave1/workspace/hello-world-war-pipeline/target/hello-world-war-1.0.1.war /opt/apache-tomcat-10.1.49/webapps/"
            }
        }
    }
}

pipeline {
    agent { label 'Java' }

    stages {
        stage('update and install mvn') {
            steps {
                sh "sudo apt update"
              sh  "sudo apt install maven -y"
            }
        }
      
        stage('checkout') {
            steps {
                sh "rm -rf hello-world-war"
              sh  "git clone https://github.com/vinayak432/hello-world-war/"
            }
        }
         stage('Build - deploy') {
            steps {
                sh "mvn clean package"
                sh "sudo cp /home/slave1/workspace/JenkinsFile/target/hello-world-war-1.0.2.war /opt/apache-tomcat-10.1.49/webapps"
            
            }
        }
    
      
    }
}

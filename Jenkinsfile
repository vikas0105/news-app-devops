pipeline {
    agent { label 'slave2' }

    stages {

        stage('Check Java & Maven') {
            steps {
                sh '''
                echo "Checking Java..."
                java -version || { echo "Java not installed!"; exit 1; }

                echo "Checking Maven..."
                mvn -v || { echo "Maven not installed!"; exit 1; }
                '''
            }
        }

        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: 'main']],
                    userRemoteConfigs: [[url: 'https://your-repo-url.git']]
                ])
            }
        }

        stage('Clean Old Deployment') {
            steps {
                sh '''
                TOMCAT=/opt/tomcat/webapps
                APP=myapp.war

                echo "Removing old deployment..."
                rm -f $TOMCAT/$APP
                rm -rf $TOMCAT/${APP%.war}
                '''
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sh '''
                TOMCAT=/opt/tomcat
                WAR=target/*.war

                echo "Copying WAR file to Tomcat..."
                cp $WAR $TOMCAT/webapps/
                '''
            }
        }

        stage('Restart Tomcat') {
            steps {
                sh '''
                TOMCAT=/opt/tomcat

                echo "Stopping Tomcat..."
                $TOMCAT/bin/shutdown.sh || true
                sleep 5

                echo "Starting Tomcat..."
                $TOMCAT/bin/startup.sh
                '''
            }
        }
    }
}

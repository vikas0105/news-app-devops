pipeline {
    agent { label 'slave2' }

    environment {
        ARTIFACTORY_URL = 'https://trialwd70ug.jfrog.io/artifactory' // <<-- change this
        ARTIFACTORY_REPO = 'pradeep.devops.releases'                         // <<-- change this
        ARTIFACTORY_PATH = 'news-app'                                 // <<-- optional path inside repo
        credentialsId    = 'bbe8e8e6-4c18-47ca-ade6-938feeba4225' // Jenkins credentials ID
        WAR_FILE = "target/news-app.war"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'feature-1', url: 'https://github.com/pradeepreddy-hub/news-app-devops.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy WAR to Tomcat') {
            steps {
                sh '''
                    TOMCAT_PATH="/opt/tomcat10/webapps"
                    WAR_FILE="target/news-app.war"

                    echo "Cleaning old deployment..."
                    sudo rm -rf $TOMCAT_PATH/news-app $TOMCAT_PATH/news-app.war || true

                    echo "Copying new WAR..."
                    sudo cp $WAR_FILE $TOMCAT_PATH/

                    echo "Restarting Tomcat..."
                    pkill -f 'org.apache.catalina.startup.Bootstrap' || true
                    nohup $TOMCAT_PATH/../bin/startup.sh &
                '''
            }
        }

        stage('Publish to Artifactory') {
            when { expression { return fileExists("${env.WAR_FILE}") } }
            steps {
                // Bind both types of credentials (username/password and secret text) - one or both may be empty
                withCredentials([
                    usernamePassword(credentialsId: 'artifactory-username-password', usernameVariable: 'AF_USER', passwordVariable: 'AF_PASS'),
                    string(credentialsId: 'artifactory-apikey', variable: 'AF_APIKEY')
                ]) {
                    sh '''
                        set -e
                        echo "Preparing upload of ${WAR_FILE} to Artifactory"

                        # prefer jfrog CLI if present
                        if command -v jfrog >/dev/null 2>&1; then
                            echo "jfrog CLI found — using jfrog rt upload"
                            jfrog rt config --url="${ARTIFACTORY_URL}" --user="$AF_USER" --password="$AF_PASS" --interactive=false default || true
                            jfrog rt u "${WAR_FILE}" "${ARTIFACTORY_REPO}/${ARTIFACTORY_PATH}/" --flat=true
                            jfrog rt config --delete default || true
                        else
                            # fallback to curl: prefer API key, else username:password
                            TARGET_URL="${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${ARTIFACTORY_PATH}/$(basename ${WAR_FILE})"
                            if [ -n "$AF_APIKEY" ]; then
                                echo "Uploading with API key to ${TARGET_URL}"
                                curl -f -H "X-JFrog-Art-Api: $AF_APIKEY" -T "${WAR_FILE}" "${TARGET_URL}"
                            else
                                echo "Uploading with username/password to ${TARGET_URL}"
                                curl -f -u "$AF_USER:$AF_PASS" -T "${WAR_FILE}" "${TARGET_URL}"
                            fi
                        fi

                        echo "Upload step finished."
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build, deploy and Artifactory publish completed successfully!'
        }
        failure {
            echo 'Build or deployment failed. Check logs for details.'
        }
    }
}

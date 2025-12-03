pipeline {
    agent { label 'slave2' }

    environment {
        ARTIFACTORY_URL = 'https://trialwd70ug.jfrog.io/artifactory'
        ARTIFACTORY_REPO = 'pradeep.devops.releases'
        ARTIFACTORY_PATH = 'news-app'
        ARTIFACTORY_CRED_ID = 'bbe8e8e6-4c18-47ca-ade6-938feeba4225' // update if different
        ARTIFACTORY_APIKEY_CRED_ID = '2d6f0f73-4394-46da-b071-c33385744dbe' // update to your API key credential id
        WAR_FILE = "target/news-app.war"
        TOMCAT_WEBAPPS = "/opt/tomcat10/webapps"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'feature-1', url: 'https://github.com/pradeepreddy-hub/news-app-devops.git'
            }
        }

        stage('Build') {
            steps {
                // run maven build (will run tests because skipTests=false)
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
                // note: the Jenkins agent must have permission to remove/copy/start Tomcat (sudo may be required)
                sh '''
                    set -e

                    echo "Cleaning old deployment..."
                    rm -rf "${TOMCAT_WEBAPPS}/news-app" || true
                    rm -f "${TOMCAT_WEBAPPS}/news-app.war" || true

                    echo "Copying new WAR..."
                    cp "${WAR_FILE}" "${TOMCAT_WEBAPPS}/"

                    echo "Restarting Tomcat (graceful)..."
                    # attempt graceful stop/start; fallback to pkill if needed
                    if [ -x "${TOMCAT_WEBAPPS}/../bin/shutdown.sh" ]; then
                        "${TOMCAT_WEBAPPS}/../bin/shutdown.sh" || true
                        sleep 3
                        "${TOMCAT_WEBAPPS}/../bin/startup.sh" || true
                    else
                        pkill -f 'org.apache.catalina.startup.Bootstrap' || true
                        nohup "${TOMCAT_WEBAPPS}/../bin/startup.sh" &>/dev/null &
                    fi

                    echo "Deployment to Tomcat finished."
                '''
            }
        }

        stage('Publish to Artifactory') {
            when {
                expression { return fileExists(env.WAR_FILE) }
            }
            steps {
                // Use either username/password (from ARTIFACTORY_CRED_ID) or an API key credential (ARTIFACTORY_APIKEY_CRED_ID).
                // Update the credential IDs in the environment block to match your Jenkins credentials.
                withCredentials([
                    usernamePassword(credentialsId: "${ARTIFACTORY_CRED_ID}", usernameVariable: 'AF_USER', passwordVariable: 'AF_PASS'),
                    string(credentialsId: "${ARTIFACTORY_APIKEY_CRED_ID}", variable: 'AF_APIKEY')
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

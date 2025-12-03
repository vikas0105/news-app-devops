pipeline {
    agent { label 'java' }
    stages {
        stage('News-App-Checkout') {
            steps {
                sh 'rm -rf news-app-devops'
                sh 'git clone https://github.com/pradeepreddy-hub/news-app-devops.git'
                echo "git clone completed"
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Version-Build') {
            steps {
                script {
                    // Example: version = 1.0.<BUILD_NUMBER>
                    def version = "1.0.${env.BUILD_NUMBER}"
                    echo "Setting project version to ${version}"
                    
                    // Update pom.xml version
                    sh "mvn versions:set -DnewVersion=${version}"
                    
                    // Build with new version
                    sh "mvn clean package"
                }
            }
        }
        stage('Deploy') {
    steps {
   sh "sudo cp /home/slave2/workspace/ranch_pipeline_Project_feature-1/target/news-app.war /opt/tomcat10/webapps/"
      echo "build deployed"
    }
}
        // 6.3: Push the artifacts to Jfrog repository
stage('Push the artifacts into Jfrog Artifactory') {
    steps {
        script {
            // Get the current date and time in the format: yyyy-MM-dd_HH-mm
            def currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date())

            // Define the target path with the timestamp
            def targetPath = "NewsApp/${currentDate}/"

            // Configure the Artifactory server
            rtServer(
                id: 'Artifactory',
                url: 'https://trialwd70ug.jfrog.io/artifactory',
                credentialsId: 'jfrog-credentials-id'   // must match Jenkins credentials
            )

            // Upload the artifact to JFrog Artifactory with the timestamped path
            rtServer(
  id: 'Artifactory',
  url: 'https://trialwd70ug.jfrog.io/artifactory',
  credentialsId: 'jfrog-username-password-id'
)
rtUpload(
  serverId: 'Artifactory',
  spec: """{
    "files": [
      { "pattern": "${env.BUILT_WAR}", "target": "${ARTIFACTORY_REPO}/NewsApp/${new Date().format('yyyy-MM-dd_HH-mm')}/" }
    ]
  }"""
)

        }
    }


}
    }
    post {
    success {
        archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }
}
    
}

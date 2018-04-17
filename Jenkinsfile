node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        sh "cd $WORKSPACE"
        sh "git checkout alpha"
        sh "git pull"
    }

    stage('Running Fastlane') {
        echo 'Running Fastlane alpha lane...'
        sh 'bundle exec fastlane alpha'
    }
    
    stage('Archiving artifacts') {
        echo 'Archiving artifacts...'
        archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
    }
}
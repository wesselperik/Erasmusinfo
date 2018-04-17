node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        sh "cd $WORKSPACE"
        sh "git checkout alpha"
        sh "git pull"
    }

    stage('Running Fastlane') {
        echo 'Running Fastlane ' + env.JOB_NAME + ' lane...'
        sh 'bundle exec fastlane alpha'
    }
    
    stage('Archiving artifacts') {
        echo 'Archiving artifacts...'
        archiveArtifacts artifacts: 'mobile/build/outputs/apk/release/*.apk', fingerprint: true
    }
}
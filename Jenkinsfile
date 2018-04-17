node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        sh "cd $WORKSPACE"
        sh "git checkout " + env.JOB_NAME.replace("Erasmusinfo/", "")
        sh "git pull origin " + env.JOB_NAME.replace("Erasmusinfo/", "")
    }

    stage('Running Fastlane') {
        echo 'Running Fastlane ' + env.JOB_NAME.replace("Erasmusinfo/", "") + ' lane...'
        sh 'bundle exec fastlane ' + env.JOB_NAME.replace("Erasmusinfo/", "")
    }
    
    stage('Archiving artifacts') {
        echo 'Archiving artifacts...'
        archiveArtifacts artifacts: 'mobile/build/outputs/apk/release/*.apk', fingerprint: true
    }
}

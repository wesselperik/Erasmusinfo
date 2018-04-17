node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        git 'https://github.com/wesselperik/Erasmusinfo.git'
    }

    stage('Running Fastlane') {
        echo 'Running Fastlane beta lane...'
        sh 'bundle exec fastlane beta'
    }
    
    stage('Archiving artifacts') {
        echo 'Archiving artifacts...'
        archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
    }
}
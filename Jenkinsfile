node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        git 'https://github.com/wesselperik/Erasmusinfo.git'
    }

    stage('Running Fastlane') {
        echo 'Running Fastlane production lane...'
        sh 'fastlane production'
    }
    
    stage('Archiving artifacts') {
        echo 'Archiving artifacts...'
        archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
    }
}
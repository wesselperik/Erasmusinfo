node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        git 'https://github.com/wesselperik/Erasmusinfo.git'
    }

    stage('Runnin Fastlane') {
        echo 'Building...'
        sh 'fastlane beta'
    }
    
    stage('Archiving artifacts') {
        archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
    }
}
node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        sh "rm -rf Erasmusinfo"
        sh "rm -rf Erasmusinfo_alpha-*"
        sh "git clone git://github.com/wesselperik/Erasmusinfo.git"
        sh "git checkout alpha"
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
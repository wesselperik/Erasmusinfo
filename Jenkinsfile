node('master') {
    stage('Checkout') {
        echo 'Getting source code...'
        sh "rm -rf $WORKSPACE"
        sh "git clone git://github.com/wesselperik/Erasmusinfo.git $WORKSPACE"
        sh "git checkout alpha"
        sh "cd $WORKSPACE"
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
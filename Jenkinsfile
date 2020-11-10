pipeline {
    agent any
    triggers {
      pollSCM('H/2 * * * *') // Enabling being build on Push
    } 
    stages {
        stage("Helllo") {
            steps {
                echo "Hello There Now"
            }  // steps 
        } // state
    }  // stages 
} // pipeline 

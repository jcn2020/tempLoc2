def call(Map config=[:]) {
    node {
        stage("Say You") {
            println("Say You " + config.target ) 
        } // stage
        
        stage("Say Me") {
            println("Say Me " + config.target )
        } // stage
    } // node
} // call 

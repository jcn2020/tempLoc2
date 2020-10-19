def call(Map config=[:] {
    node {
        stage("Say You") {
            println("Say You") 
        } // stage
        
        stage("Say Me") {
            println("Say Me")
        } // stage
    } // node
} // call 

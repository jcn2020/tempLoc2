def call(Map config=[:] {
    node {
        stage("Say You") {
            println("Say You") 
        } 
        
        stage("Say Me") {
            println("Say Me")
        }
}

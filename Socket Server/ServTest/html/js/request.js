window.onload = function(){
    console.log("request page")


    
}
function accept(){
    console.log("accepted!")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        //look into this
        ALMemory.raiseEvent("app/hangman_word", "1")
        ALTextToSpeech.say("lets play a game!");
        console.log("app/username event was raised with value: 1 ");
      });
      
    
}
function deny(){
    console.log("deny!")
    //send accept http request
}
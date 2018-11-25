window.onload = function(){
    console.log("request page")


    
}
function accept(){
    console.log("accepted!")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        
        ALMemory.raiseEvent("app/incoming_game_request", "1")
        ALTextToSpeech.say("lets play a game!");
        console.log("app/username event was raised with value: 1 ");
      });
      
    
}
function deny(){
    console.log("deny!")
    //send accept http request
}
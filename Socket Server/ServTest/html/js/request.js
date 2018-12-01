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
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        //look into this
        ALMemory.raiseEvent("app/deny_game_request", "1")
        ALTextToSpeech.say("Fine I gues I didnt want to play anyways");
        console.log("Game requet denied");
        console.log("changing page")
        window.setTimeout(changePage, 5000);
      });
}

function changePage(){
    console.log("changing page")
    window.location.href = "home.html"
}
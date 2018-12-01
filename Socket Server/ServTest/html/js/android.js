window.onload = function(){
    console.log("android user info page")

}

function submit(){
    console.log("submitted")
    var form = document.getElementById("form")
    var word = form.elements[0].value
    var hint = form.elements[1].value

    console.log("word is = " + word)
    console.log("hint = " + hint)

    if(word == "" || hint== ""){
        alert("Dont forget any of the requested fields or android wont be able to play!")
    }
    else{
        RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
            ALMemory.raiseEvent("app/hangman_android_hint", hint)
            ALMemory.raiseEvent("app/hangman_android_word", word)
            ALTextToSpeech.say("sending android info!");
            ALMemory.raiseEvent("app/hangman_start_game", "testing")
            console.log("app/hangman event was raised with word: " + word);
          });
        //window.location.href = "game.html"
        window.setTimeout(changePage, 5000)   

    }
    
}

function changePage(){
    window.location.href = 'game.html'
}
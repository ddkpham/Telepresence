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

    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        ALMemory.raiseEvent("app/hangman_android_hint", hint)
        ALMemory.raiseEvent("app/hangman_android_word", word)
        ALTextToSpeech.say("sending android info!");
        console.log("app/hangman event was raised with word: " + word);
      });
}
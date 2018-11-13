RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At Register page");
    console.log("Connected to services");
  });

  function register(){
    console.log("got into register function!")
    var form = document.getElementById("form");
    var pepperid = form[0].value;
    console.log("This is the value of " + pepperid)

  }

  function sendRegisterRequest(){
    
  }
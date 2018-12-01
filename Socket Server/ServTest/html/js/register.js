RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At Register page");
    console.log("Connected to services");
  });

  function register(){
    console.log("got into register function!")
    var form = document.getElementById("form");
    var pepperid = form[0].value;
    var username = form[1].value;
    console.log("This is the value of " + pepperid);
    if(pepperid == "" || username == ""){
      alert("Check all the required fields again!")
    }
    else{
      RobotUtils.onServices(function(ALMemory, ALTextToSpeech){
        console.log("raising registration event ")
        ALMemory.raiseEvent("app/current_pepper_id", pepperid);
        ALMemory.raiseEvent("app/username", username);
        ALTextToSpeech.say("Registering Pepper!")
        //ALTextToSpeech.say("Sign up Events have been raised");
      });  
    }
    
  }

  function sendRegisterRequest(){
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech){
      console.log("raising registration event ")
      ALMemory.raiseEvent("app/current_pepper_id", pepperid);
      //ALTextToSpeech.say("Sign up Events have been raised");
  });
  }

  RobotUtils.subscribeToALMemoryEvent("app/current_pepper_id", function(value) {
    alert("PepperID: " + value + " Registered. Please login now!");
  });
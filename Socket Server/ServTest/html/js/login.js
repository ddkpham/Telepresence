
RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At login page");
    console.log("Connected to services");
  });
  



function login() {
    //call a proxy to ALTextToSpeech
    
    var forminfo = document.getElementById("form");
    var username = forminfo.elements[0];
    var password = forminfo.elements[1];
    console.log("I got into the login function");
    console.log(username.value);
    console.log(password.value);
    if(username.value == "" || password.value== ""){
      console.log("invalid fields")
      alert("Check your required fields again!")
    }
    else{
      //raise events to check login infomation
      RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/username", username.value)
      ALMemory.raiseEvent("app/password", password.value)
      //ALTextToSpeech.say("At login function");
      console.log("app/username event was raised with value: " +username.value);
    });
    
    }
      
}

RobotUtils.subscribeToALMemoryEvent("app/login_status", function(value) {
  alert("login status: " + value);
});

function say(value) {
    RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say(value);
    console.log("Connected to services");
  });
}


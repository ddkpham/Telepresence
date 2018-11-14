
console.log("hello");
RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("Hello welcome home!");
    console.log("I got into robot services")
  });

var PepperID = "PepperID : Salt"  

  function setPep(){
      console.log("setting header!")
      var header = document.getElementById("header")
      header.innerHTML= PepperID
  }

  
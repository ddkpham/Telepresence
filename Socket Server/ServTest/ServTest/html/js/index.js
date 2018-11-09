
console.log("hello");
RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("Hello welcome Home");
    console.log("I got into robot services")
  });




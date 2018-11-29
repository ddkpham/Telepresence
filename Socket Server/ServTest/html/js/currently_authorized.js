
RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
    test = ALMemory.getData("app/current_pepper_id")
    console.log(test)
    ALTextToSpeech.say("At currently_authorized page");
    console.log("Connected to services");

  });
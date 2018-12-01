console.log("On Photos page")
RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
    ALMemory.raiseEvent("app/photo_request", "request")
    ALTextToSpeech.say("At photo page");
    console.log("Connected to services");
  });

//Sets photo on  tablet 
RobotUtils.subscribeToALMemoryEvent("photo", function(value) {
    console.log("grabbing photo")
    console.log(value);
    //decode base 64 string  
    decoded_string =  (atob(value));
    //create blob object
    var blob = new Blob([decoded_string],  {type: 'image/png'});
    console.log(blob);
    //open file and read as stream
    var fr = new FileReader()
    fr.onload = function ( oFREvent ) {
        document.getElementById("photo").src = "data:image/png;base64," + btoa(oFREvent.target.result);
    };
    fr.readAsText(blob, "utf-8");

});
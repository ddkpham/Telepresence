

RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("Let's be friends forever");
    console.log("Connected to services");
  });
  


function grabInput() {
    console.log("got into grabInput Function")
    var forminfo = document.getElementById("form");
    var firstname = forminfo.elements[0];
    var lastname = forminfo.elements[1];
    var email = forminfo.elements[2];
    var username = forminfo.elements[3];
    var password = forminfo.elements[4];
    
    console.log("firstname is : " + firstname.value);
    console.log("lastname is : " + lastname.value);
    console.log("username is : " + username.value);
    console.log("password is : " + password.value);
    console.log("email is : " + email.value);
    
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech){
        console.log("entered Event raising ")
        ALTextToSpeech.say("Inputing sign up info into ALmemory");
        ALMemory.raiseEvent("app/signup_firstname", firstname.value);
        ALMemory.raiseEvent("app/signup_lastname", lastname.value);
        ALMemory.raiseEvent("app/signup_username", username.value);
        ALMemory.raiseEvent("app/signup_password", password.value);
        ALMemory.raiseEvent("app/signup_email", email.value);
        ALTextToSpeech.say("Sign up Events have been raised");
    });
    
}

RobotUtils.subscribeToALMemoryEvent("app/signup_status", function(value) {
  alert("signup_status: " + value);
});
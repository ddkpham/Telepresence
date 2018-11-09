

//create a session for the connection 

var session = new QiSession();

/* This function allow to connect with ALMemory thank to the box "Raise Event".
You need to give the key "PepperQiMessaging/totablet"*/
function startSubscribe() {
    //call a proxy to ALMemory and call it ALmemory
    session.service("ALMemory").done(function (ALMemory) {
      //subscribe to PepperQiMessaging/totablet2 via subscriber 
        ALMemory.subscriber("PepperQiMessaging/totablet2").done(function(subscriber) {
          //call function toTabletHandler 
            subscriber.signal.connect(toTabletHandler);
        });    
    });
}

/* Receive the data send by choregraphe with the id "command". 
You can change the name of the id.*/ 
function toTabletHandler(value) { 
  // get the data and put it in the id "command"
  document.getElementById("command").value= value;
  tmp = document.getElementById("command").value;
  // send the data to html page
  document.getElementById("command").innerHTML= tmp;
  // process data with the function choice()
  choice(tmp)
}

function say() {
    //call a proxy to ALTextToSpeech
    var forminfo = document.getElementById("form");
    var username = forminfo.elements[0];
    var password = forminfo.elements[1];

    session.service("ALTextToSpeech").done(function (tts) {
        tts.say( "username is : " + username.value);
        tts.say("password is " + password.value);
      }).fail(function (error) {
        console.log("An error occurred:", error);
      });
    
      session.service("ALMemory").done(function (ALMemory) {
        console.log("ALMemory");
        ALMemory.raiseEvent("app/username", username.value);
        ALMemory.raiseEvent("app/password", password.value);
    });
      
}



/* Process data */
function choice(tmp){
    /* code */
    if (tmp === "connect"){
        sendToChoregraphe("ok"); 
    }
    if (tmp === "exit"){
        sendToChoregraphe("exit");
        StopProgramm(1);
    }
}

/* Send information to choregraphe thank to the event "PepperQiMessaging/fromtablet".
You need to create this event in choregraphe (add event from ALMemory).*/
function sendToChoregraphe(response) {
    session.service("ALMemory").done(function (ALMemory) {
        console.log("ALMemory");
        ALMemory.raiseEvent("PepperQiMessaging/fromTabletResponse2", response);
    });
}

/* Close the html window and send information to the event "PepperQiMessaging/fromTabletStop".
You need to create this event in choregraphe (add event from ALMemory).*/
function StopProgramm(response) {
    window.close();
    session.service("ALMemory").done(function (ALMemory) {
        console.log("ALMemory");
        ALMemory.raiseEvent("PepperQiMessaging/fromTabletStop2", response);
    });
}

function grabInput() {
        
    var forminfo = document.getElementById("form");
    var username = forminfo.elements[0];
    var password = forminfo.elements[1];
    console.log(username.value);
    console.log(password.value);
    document.getElementById("demo").innerHTML = username.value;
    $("#img-clck").click(grabInput);
}
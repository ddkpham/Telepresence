RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At Authorization page");
    console.log("Connected to services");
  });

var authorize = ["ddkpham", "ddkpham@gmail.com"]
console.log("Hello")

var PepperID = "PepperID : Salt"  

  function setPep(){
      console.log("setting header!")
      var header = document.getElementById("header")
      header.innerHTML= PepperID
  }

  console.log(test.AuthReqs[1][0])
  console.log(test.AuthReqs.length)
  for (var i = 0; i < test.length; i++){
    console.log(test.AuthReqs[i])
  }


 function getAuthRequests(){
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        var count = Math.random()
        var random_string = count.toString()
        console.log("raising new auth request with value: " + random_string)
        //raise event to get new auth requests
        ALMemory.raiseEvent("app/new_auth_requests", random_string)
        //console.log("app/username event was raised with value: " +username.value);
      });
 } 


 
function createTable() {
    //var jsonTest2 = JSON.parse(test)
    //console.log(jsonTest2)
    getAuthRequests();
    setPep();

RobotUtils.subscribeToALMemoryEvent("app/tablet_new_auth_request", function(value) {
    console.log("this is new auth requests "+ value)
    console.log(typeof value)
    console.log(typeof test2)
    test = JSON.parse(value)
    console.log(typeof test)
    for(var i=0; i< test.AuthReqs.length; i++ ){
        //create row
        var table = document.getElementById("authTable");
        var row = table.insertRow(0);
        var username = row.insertCell(0);
        var email = row.insertCell(1);
        var accept = row.insertCell(2);
        var deny = row.insertCell(3);

        //create buttons and define class
        var acceptBtn = document.createElement("BUTTON")
        var denyBtn = document.createElement("BUTTON")
        
        acceptBtn.id = test.AuthReqs[i][0]
        acceptBtn.onclick = function(){
            pusername = this.id 
            console.log(pusername)
            acceptRequest(pusername)
        }

        denyBtn.id = test.AuthReqs[i][0]
        denyBtn.onclick  = function (){
            pusername = this.id 
            console.log(pusername)
            denyRequest(pusername)
        }
        acceptBtn.className="button"
        denyBtn.className="button2"
        //set HTML text 
        acceptBtn.innerHTML = "ACCEPT"
        denyBtn.innerHTML = "DENY"

        //bind methods and append to cell

        accept.appendChild(acceptBtn)
        deny.appendChild(denyBtn)
        //set text for username // email
        username_text = test.AuthReqs[i][0] + "    "
        email_text = test.AuthReqs[i][1] + "    "
        username.innerHTML = username_text;
        email.innerHTML = email_text;
        
    }
    createheaders();
  });
    
    //parseJson();
}

function acceptRequest(pusername) {
    alert("request has been granted!")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        ALMemory.raiseEvent("app/auth_reply", "accept")
        ALTextToSpeech.say("You made a friend! Good for you");
        ALMemory.raiseEvent("app/auth_username_reply", pusername)        
        console.log("Connected to services");
      });
}

function denyRequest(pusername){
    alert("request has been denied!")
    RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
        ALLeds.randomEyes(2.0);
        ALTextToSpeech.say("Stranger Danger");
        ALMemory.raiseEvent("app/auth_reply", "deny")
        ALTextToSpeech.say("Stranger Danger");
        ALMemory.raiseEvent("app/auth_username_reply", pusername)
        
        console.log("Connected to services");
      });
}

function createheaders() {
    console.log("in create header")
    var table = document.getElementById("authTable");
    var row = table.insertRow(0);
    var username = row.insertCell(0);
    var email = row.insertCell(1);
    var accept = row.insertCell(2);
    var deny = row.insertCell(3);

    username.innerHTML= "USERNAME"
    email.innerHTML = "EMAIL"
    accept.innerHTML = "ACCEPT"
    deny.innerHTML = "DENY"
  
  }


  

  for(var i=0; i<jsonTest.length;i++){
      //console.log(jsonTest[i].username)
  }
  

  function parseJson(obj){
      console.log("in parse function!")
      console.log(obj.length)
      for(var i=0 ; i< authRequests.length; i++){
          var user = authRequests[i];
          //console.log(user.username);
          //console.log(user.email)
      }
    
  }
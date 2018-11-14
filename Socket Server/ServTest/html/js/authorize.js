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

var jsonTest = [
    {
      username:"ddkpham",
      email: "ddkpham@gmail.com"
    },
    {
      username:"fionaR",
      email:"fionacroome@gmail.com"
    },
    {
        username: "Johnny",
        email:"Jpop@gmail.com"
    }
]

var test2 = {
    "AuthReqs": [
      [
        "kassym",
        "kassym@a.c"
      ],
      [
        "paul",
        "paul@a.com"
      ],
      [
        "salt",
        "salt@a.z"
      ],
      [
        "testuser",
        "test@a.b"
      ]
    ]
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
        acceptBtn.className="button"
        denyBtn.className="button2"
        //set HTML text 
        acceptBtn.innerHTML = "ACCEPT"
        denyBtn.innerHTML = "DENY"

        //bind methods and append to cell
        acceptBtn.onclick = acceptRequest
        denyBtn.onclick = denyRequest 
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

function acceptRequest() {
    alert("request has been granted!")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        ALMemory.raiseEvent("app/app/auth_username_reply", "")
        ALMemory.raiseEvent("app/auth_reply", "accept")
        ALTextToSpeech.say("You made a friend! Good for you");
        console.log("Connected to services");
      });
}

function denyRequest(){
    alert("request has been denied!")
    RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
        ALLeds.randomEyes(2.0);
        ALTextToSpeech.say("Stranger Danger");
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
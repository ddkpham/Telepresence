RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At Authorization page");
    console.log("Connected to services");
  });

var authorize = ["ddkpham", "ddkpham@gmail.com"]
console.log("Hello")

var PepperID = "PepperID : 424242424242424242424"  

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

var test = {
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


function createTable() {
    //var jsonTest2 = JSON.parse(test)
    //console.log(jsonTest2)
    setPep();
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
    //parseJson();
}

function acceptRequest() {
    alert("request has been granted!")
    RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
        ALLeds.randomEyes(2.0);
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
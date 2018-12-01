//THIS IS AN INCOMPLETE JS FILE 


RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At De-Authorization page");
    console.log("Connected to services");
  });

  var PepperID = "PepperID : SALT"

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
  var authorize=["ddkpham", "ddkpham@gmail.com"]
  var headers = ["username", "email", "request response"]

  var authUsers = {
    "AuthUsers": [
      [
        "admin",
        "admin@example.com"
      ],
      [
        "admin2",
        "admin@example.com"
      ]
    ]
  }
  console.log(authUsers)
  console.log(authUsers)
  console.log(authUsers.AuthUsers.length)
  //console.log(jsonTest[1].username)

  //Creates authorization table 
  function createTable() {
    setPep();
    for(var i=0; i< authUsers.AuthUsers.length; i++ ){
      //create row
      var table = document.getElementById("deauthTable");
      var row = table.insertRow(0);
      var username = row.insertCell(0);
      var email = row.insertCell(1);
      var deny = row.insertCell(2);
      //create buttons
      var denyBtn = document.createElement("BUTTON")
      denyBtn.className="button2"
      //set attributes
      denyBtn.innerHTML = "UNFRIEND"
      denyBtn.id = String(authUsers.AuthUsers[i][0])
      denyBtn.onclick = function(){
        alert("unfriended!")
        username = String(this.id)
        RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        ALMemory.raiseEvent("app/deauth_user", username)
        ALTextToSpeech.say("Good riddance!");
        console.log("Connected to services");
    });
      };
      deny.appendChild(denyBtn)
      //set text for username // email
      username_text = authUsers.AuthUsers[i][0] + "    "
      email_text = authUsers.AuthUsers[i][1] + "    "
      username.innerHTML = username_text;
      email.innerHTML = email_text;

  }
    //Create headers
    createheaders();

  }
  //handles unfriending request 
  function unfriend() {
    alert("unfriended!")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/deauth_user", "test")
      ALTextToSpeech.say("Good riddance!");
      console.log(index)
      console.log("Connected to services");
    });
  }

  function createheaders() {
    console.log("in create header")
    var table = document.getElementById("deauthTable");
    var row = table.insertRow(0);
    var username = row.insertCell(0);
    var email = row.insertCell(1);
    var accept = row.insertCell(2);

    username.innerHTML= "USERNAME"
    email.innerHTML = "EMAIL"
    accept.innerHTML = "are you sure?"
  
  }
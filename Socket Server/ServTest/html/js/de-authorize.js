RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("At De-Authorization page");
    console.log("Connected to services");
  });

  var PepperID = "PepperID : 42424242424242424"
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

  //console.log(jsonTest[1].username)
  function createTable() {
    setPep();
    for(var i=0; i< jsonTest.length; i++ ){
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
      denyBtn.onclick = unfriend;
      deny.appendChild(denyBtn)
      //set text for username // email
      username_text = jsonTest[i].username + "    "
      email_text = jsonTest[i].email + "    "
      username.innerHTML = username_text;
      email.innerHTML = email_text;

  }
    //Create headers
    createheaders();

  }
  //handles unfriending request 
  function unfriend() {
    alert("unfriended!")
    RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
      ALLeds.randomEyes(2.0);
      ALTextToSpeech.say("Good riddance!");
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
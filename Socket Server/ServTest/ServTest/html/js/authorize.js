RobotUtils.onServices(function(ALLeds, ALTextToSpeech) {
    ALLeds.randomEyes(2.0);
    ALTextToSpeech.say("Authorize users to connect");
    console.log("Connected to authorization page");
  });

var authorizationRequest = ["David" , "ddkpham@gmail.com"]

myTable(){
    var authTable = document.getElementById("authTable");
    var row = authTable.insertRow(0)
//add info
//username, email, accept, deny
    var username = row.insertCell(0)
    var email = row.insertCell(1)
    var acceptBtn = row.insertCell(2)
    var denyBtn = row.insertCell(3)
    console.log("row created")
//set values of rows
    username.innerHTML = authorizationRequest[0]
    email.innerHTML = authorizationRequest[1]
    console.log("values set in cells")
    var acceptbutton = document.createElement("button")
    acceptbutton.onclick = function(){
        alert("I have been clicked")
    }
    console.log("accept button created")
    acceptBtn.appendChild(acceptbutton)
    acceptbutton.innerHTML = "ACCEPT"
    var denybutton = document.createElement("button")
    denybutton.onclick = function(){
        alert("I have been clicked")
    }
    denybutton.innerHTML = "DENY"
    denyBtn.appendChild(denybutton)  
}

//var authTable = document.getElementById("authTable");
//var row = authTable.insertRow(0)
//add info
//username, email, accept, deny
//var username = row.insertCell(0)
//var email = row.insertCell(1)
//var acceptBtn = row.insertCell(2)
//var denyBtn = row.insertCell(3)

//set values of rows
//username.innerHTML = usernamedata
//email.innerHTML = emaildata
//var acceptbutton = document.createElement("button")
//acceptbutton.onclick = function()
//acceptbutton.innerHTML = ACCEPT
//var denybutton = document.createElement("button")
//denybutton.onclick = function()
//denybutton.innerHTML = DENY



//Send a request to telepresence service for currently authorized users
RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
    ALTextToSpeech.say("At currently_authorized page");
    ALMemory.raiseEvent("app/currently_authorized_request","request")
    console.log("Connected to services");
  });

  //Waits for response from Cloud Server
RobotUtils.subscribeToALMemoryEvent("app/authorized_users", function(value) {
    console.log("grabbing currently authorized users")
    console.log(value)
    createTable(value);
    
});

//Creates table for authorized users 
function createTable(value){
    console.log("creating table")
    console.log(typeof value)
    AuthUsers = JSON.parse(value)
    console.log(typeof AuthUsers)
    //console.log(AuthUsers.AuthUsers[0][0])
    console.log("creating rows")
    for(var i = 0; i< AuthUsers.AuthUsers.length; i++){
        //create rows
        var table = document.getElementById('authTable')
        var row = table.insertRow(0)
        var username = row.insertCell(0);
        var email = row.insertCell(1);
        var button = row.insertCell(2);

        //create buttons and attach to Table DOM
        var unfriendBtn = document.createElement("BUTTON")
        unfriendBtn.className="button2"
        unfriendBtn.id = AuthUsers.AuthUsers[i][0];
        unfriendBtn.onclick = function(){
            unfriend_id = this.id
            console.log("username = " + unfriend_id)
            unfriend(unfriend_id)
        }
        button.appendChild(unfriendBtn)

        unfriendBtn.innerHTML = 'UNFRIEND'
        username.innerHTML = AuthUsers.AuthUsers[i][0] + "  "
        email.innerHTML = AuthUsers.AuthUsers[i][1] + "  "
    }
    createheaders();
    
}

//Send unfriend request
function unfriend(username){
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
   
        ALTextToSpeech.say("Unfriending!");
        ALMemory.raiseEvent("app/deauth_user", username)
        console.log("Connected to services");
        //reset page
        window.location.reload()
      });
}

//Create Headers for table 
function createheaders() {
    console.log("in create header")
    var table = document.getElementById("authTable");
    var row = table.insertRow(0);
    var username = row.insertCell(0);
    var email = row.insertCell(1);
    var accept = row.insertCell(2);

    username.innerHTML= "USERNAME"
    email.innerHTML = "EMAIL"
    accept.innerHTML = "are you sure?"
  
  }
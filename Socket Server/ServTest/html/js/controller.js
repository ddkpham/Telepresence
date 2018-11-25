window.onload = function () {

    var alphabet = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
          'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
          't', 'u', 'v', 'w', 'x', 'y', 'z'];
    

    var word ;              // Selected word
    var guess ;             // Geuss
    var geusses = [ ];      // Stored geusses
    var space;
    var numOfCorrectGuesses ; // Count correct geusses               
    var numOfIncorrectGuesses = 0; 
    var androidHint;
    var hint;
    var lives = 6;
    var android_username;
    var pepper_time = 10;

    // Get elements
    var showLives = document.getElementById("mylives");
    var showClue = document.getElementById("clue");
    
    // creates buttons for game 
    var buttons = function () {
      myButtons = document.getElementById('buttons');
      letters = document.createElement('ul');
      //sets up buttons and text
      //console.log("this is length of alphabet = " + alphabet.length)
      for (var i = 0; i < alphabet.length; i++) {
        letters.id = 'alphabet';
        list = document.createElement('li');
        list.id = 'letter';
        list.innerHTML = alphabet[i];
        list.setAttribute("class", "btn")
        //console.log("setting = " + alphabet[i])
        //list.setAttribute("class", "btn-default")
        //set onclick for buttons 
        list.onclick = function (){
          var guess = (this.innerHTML)
          //set to chosen
          this.setAttribute("class", "chosen")
          list.setAttribute("class", "btn")
          //void button
          this.onclick=null;
          //check if in word 
          for(var i = 0; i<word.length; i++){
            if(word[i]===guess){
              geusses[i].innerHTML = guess;
              numOfCorrectGuesses += 1;  
              pepper_happy_remark();            
              winCheck();
            }
          }
          //if not in word
          var index = word.indexOf(guess);
          if(index === -1){
            numOfIncorrectGuesses += 1;
            lives -= 1;
            console.log("number of lives = " + lives)
            loseCheck();
            pepper_sad_remark();
            numOfLivesDisplay();
            drawHangman();
          }
          else{
            numOfLivesDisplay();
            drawHangman();
          }
        }
        myButtons.appendChild(letters);
        letters.appendChild(list);
      }
    }


    var pepper_sad_remark = function(){
      //make pepper say something sad
    }

    var pepper_happy_remark = function(){
      //make pepper say something happy
    }

    //checks android finish status via telepresence_service
    function check_android_finish_status(){
      RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        console.log
        var count = Math.random()
        var random_string = count.toString()
        console.log("raising new auth request with value: " + random_string)
        //raise event to check status 
        ALMemory.raiseEvent("app/hangman_android_check_status", random_string)
      });
    }
    
    //once android user has finished, pepper will calculate results 
    function result_check(){
      //grab android user lives and time
      RobotUtils.subscribeToALMemoryEvent("app/hangman_android_user_lives", function(value) {
        console.log("android lives = " + value)
        var android_lives = value
        RobotUtils.subscribeToALMemoryEvent("app/hangman_android_user_time", function(value) {
          console.log("anroid time = " + value)
          var android_time = value
        });
        //case 1: both users lose
        if((android_lives == 0) && (lives==0)){
          console.log("both users died")
          RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
            ALMemory.raiseEvent("app/hangman_victory", "tie")
            ALTextToSpeech.say("YOU BOTH DIED NOOOOOOOOOOOOO try again ")
            document.getElementById("waiting").innerHTML = "YOU TIED"
          });
          
        }
        //case 2: pepper wins
        else if (lives > android_lives){
          console.log("pepper wins ")
          RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
            ALMemory.raiseEvent("app/hangman_victory", "pepper")
            //ALTextToSpeech.say("YOU WON GOOD JOB")
            document.getElementById("waiting").innerHTML = "YOU WIN"
          });
          
        }
        //case 3: android wins
        else if(android_lives > lives){
          console.log("android wins")
          RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
            ALMemory.raiseEvent("app/hangman_victory", "android")
            ALTextToSpeech.say("YOU LOST. Its a strange feeling being let down")
            document.getElementById("waiting").innerHTML = "YOU LOST"
          });
        }
        //case 4a) 4b) same lives check time
        else if(android_lives == lives){
          if (android_time > pepper_time){
            console.log("pepper wins with a time tiebreaker!")
            RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
              ALMemory.raiseEvent("app/hangman_victory", "pepper")
              ALTextToSpeech.say("YOU WON GOOD JOB. That was close tho")
              document.getElementById("waiting").innerHTML = "YOU WIN"
            });
          }
          else if (pepper_time > android_time){
            console.log("android wins with a time tiebreaker!")
            RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
              ALMemory.raiseEvent("app/hangman_victory", "android")
              ALTextToSpeech.say("YOU LOST. Its a strange feeling being let down")
              document.getElementById("waiting").innerHTML = "YOU LOST"
            });
          }
          else{
            console.log("IMPOSSIBLE!")
            RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
              ALMemory.raiseEvent("app/hangman_victory", "tie")
              ALTextToSpeech.say("YOU tied. Weird")
              document.getElementById("waiting").innerHTML = "YOU TIED"
            });
          }
        }
      });
    }



    //checks if Pepper user has won 
    var winCheck = function(){
      if((numOfCorrectGuesses + space) == word.length){
        alert("YOU GUESSED THE WORD!")
        //check if android has finished
        clear_display();
        check_android_finish_status();
        //check to see who has won game
        var waiting = document.getElementById("waiting")
        waiting.innerHTML = "Please wait for Android. Feel Free to Annoy Android while you wait!"
        result_check();
        //restart game for now later change to different screen
        //restart();
      }
    }

    //checks if Pepper user has lost
    var loseCheck = function(){
      if(numOfIncorrectGuesses==6){
        alert("YOU LOSE")
        //add code to connect to pepper
        //check if android has finished
        clear_display();
        check_android_finish_status();
        //check to see who has won game
        var waiting = document.getElementById("waiting")
        waiting.innerHTML = "Please wait for Android. Feel Free to Annoy Android while you wait!"
        result_check();
        //restart game for now later change to different screen 
        //restart();
      }
    }

    
    //draws hangman
    var drawHangman = function () {
      var image; 
      if(numOfIncorrectGuesses==0){image = "images/hang1.png"}
      else if (numOfIncorrectGuesses==1){image= "images/hang2.png"}
      else if (numOfIncorrectGuesses==2)(image = "images/hang3.png")
      else if (numOfIncorrectGuesses==3)(image = "images/hang4.png")
      else if (numOfIncorrectGuesses==4)(image = "images/hang5.png")
      else if (numOfIncorrectGuesses==5)(image = "images/hang6.png")
      else if (numOfIncorrectGuesses==7)(image = "images/hang7.png")
      else{image= "images/hang1.png"}
      document.getElementById("stickguy").src = image;
    }

  
  
    // Create geusses ul
    var result = function () {
      wordHolder = document.getElementById('hold');
      correct = document.createElement('ul');
  
      for (var i = 0; i < word.length; i++) {
        correct.setAttribute('id', 'my-word');
        guess = document.createElement('li');
        guess.setAttribute('class', 'guess');
        if (word[i] === "-") {
          guess.innerHTML = "-";
          guess.setAttribute('class', "invisible")
          space += 1;
          console.log("This is the number of spaces = " + space )
        } else {
          guess.innerHTML = "_";
        }
  
        geusses.push(guess);
        wordHolder.appendChild(correct);
        correct.appendChild(guess);
      }
    }
    
    // Show number of lives
    var numOfLivesDisplay = function () {
      if (numOfIncorrectGuesses == 6) {
        showLives.innerHTML = "Game Over";
      }
      else{
        var livesText = "You have " + (6- numOfIncorrectGuesses) + " lives left"
        showLives.innerHTML = String(livesText)
      }
    }
  
    function get_game_info(){
      RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
        console.log
        var count = Math.random()
        var random_string = count.toString()
        console.log("raising new auth request with value: " + random_string)
        //raise event to get new auth requests
        ALMemory.raiseEvent("app/hangman_initiate", random_string)
        console.log("app/hangman_initiate event was raised with value = " +random_string);
      });
    }

    // startgame
    start = function () {
      get_game_info();

      RobotUtils.subscribeToALMemoryEvent("app/hangman_hint_for_pepper", function(value) {
        console.log("hint = " + value)
        hint = value
      });

      RobotUtils.subscribeToALMemoryEvent("app/hangman_word_for_pepper", function(value) {
        console.log("word = " + value)
        word = value
        word = word.replace(/\s/g, "-");
        word = word.toLowerCase(word)
        buttons();
        numOfIncorrectGuesses = 0;
        numOfCorrectGuesses = 0;
        lives = 6;
        drawHangman();
        //hard coded for now
        androidHint = hint
        showClue.innerHTML = "HINT:  " + androidHint;
        geusses = [ ];
        space = 0;
        result();
      });

      //grabs android username 
      RobotUtils.subscribeToALMemoryEvent("app/hangman_android_user", function(value) {
        console.log("android user = " + value)
        android_username = value
      });
 
      numOfLivesDisplay();
      timer();
    }
  
    start();
    
    // // Hint
  
    //   hint.onclick = function() {
    //   //alert("HINT")
    //   showClue.innerHTML = "HINT:  " + androidHint;
    // };
  

     // Reset
    document.getElementById('reset').onclick = function(){
      correct.parentNode.removeChild(correct);
      letters.parentNode.removeChild(letters);
      showClue.innerHTML = "";
      start();
    };

    var clear_display = function (){
      correct.parentNode.removeChild(correct);
      letters.parentNode.removeChild(letters);
      showClue.innerHTML = "";
    }

    var restart = function(){
      correct.parentNode.removeChild(correct);
      letters.parentNode.removeChild(letters);
      showClue.innerHTML = "";
      start();
    }
  }
  
  function eventMusic(){
    console.log("Play Music")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "music");
      ALTextToSpeech.say("Let's make android sing");
      console.log("Connected to services");
    });
  }

  function eventDance(){
    console.log("EVENT Dance")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "dance");
      ALTextToSpeech.say("Let's make android dance");
      console.log("Connected to services");
    });
  }

  function eventTool(){
    console.log("EVENT Tool")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "vibrate");
      ALTextToSpeech.say("Let's make android vibrate");
      console.log("Connected to services");
    });
  }
  function eventCheer(){
    console.log("EVENT Cheer")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "pop_up");
      ALTextToSpeech.say("Let's make android popup");
      console.log("Connected to services");
    });
  }

  function timer(){
    console.log("starting timer")
    var time = new Date()
    //get current time then display time elapsed since then. 
    var seconds_passed = time.getSeconds();
    var ms_passed = time.getMilliseconds();
    console.log(seconds_passed)
    console.log(ms_passed)
    //document.getElementById("timer").innerHTML = seconds_passed + ms_passed;
  }
  

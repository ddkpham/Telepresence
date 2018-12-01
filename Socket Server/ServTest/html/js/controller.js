window.onload = function () {


    var alphabet = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
          'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
          't', 'u', 'v', 'w', 'x', 'y', 'z'];
    

    var word ;              // Selected word
    var guess ;             // Geuss
    var geusses = [ ];      // Stored geusses
    var space;              //number of spaces in word
    numOfCorrectGuesses = 0; // Count correct geusses               
    numOfIncorrectGuesses = 0; //Count incorrect guesses
    var androidHint;          //preprocessed androidhint
    var hint;                 //processed hint
    var lives = 6;
    var android_username;
    pepper_time = 0;
    startTime = new Date();   //start time - compute total time elapsed
    stillPlaying = 'true';

    // Get elements
    var showLives = document.getElementById("mylives");
    var showClue = document.getElementById("clue");
    
    // creates buttons for game 
    var buttons = function () {
      myButtons = document.getElementById('buttons');
      letters = document.createElement('ul');
      //sets up buttons and text
      //console.log("this is length of alphabet = " + alphabet.length)
      for (var i = 0; i < (alphabet.length); i++) {
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
            pepper_sad_remark();
            loseCheck();
            numOfLivesDisplay();
            drawHangman();
          }
          else{
            numOfLivesDisplay();
            drawHangman();
          }
        }
        //add buttons to screen
        myButtons.appendChild(letters);
        letters.appendChild(list);
      }
    }

    //pepper says sad things :(
    var pepper_sad_remark = function(){
      //make pepper say something sad
      console.log("sad remark")
      if(numOfIncorrectGuesses==1){
        RobotUtils.onServices(function(ALTextToSpeech) {
          ALTextToSpeech.say("Ouch");
        });
      }
      else if (numOfIncorrectGuesses==2){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Ouchie Ouchie Ouchie");
        });
      }
      else if (numOfIncorrectGuesses==3){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("uh oh youre scaring me ");
        });
      }
      else if (numOfIncorrectGuesses==4){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("im getting scared");
        });
      }
      else if (numOfIncorrectGuesses==5){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Im really scared now");
        });
      }
      else{
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Goodbye Forever I guess");
        });
      }
      
    }

    //Pepper says something when you get a correct answer!
    var pepper_happy_remark = function(){
      //make pepper say something happy
      console.log("happy remark")
      if(numOfCorrectGuesses==1){
        RobotUtils.onServices(function(ALTextToSpeech) {
          ALTextToSpeech.say("Yay! First one! feels good doesn't it");
        });
      }
      else if (numOfCorrectGuesses==2){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("WOOOOOOOOOOOOOOOOOOOO Killing it");
        });
      }
      else if (numOfCorrectGuesses==3){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Youre on a rolll");
        });
      }
      else if (numOfCorrectGuesses==4){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("you are really really really good!");
        });
      }
      else if (numOfCorrectGuesses==5){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("you might just be my hero");
        });
      }
      else if (numOfCorrectGuesses== (word.length + space)){
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Keep going!!");
        });
      }
      else {
        RobotUtils.onServices(function( ALTextToSpeech) {
          ALTextToSpeech.say("Youre so close!");
        });
      }
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
        RobotUtils.subscribeToALMemoryEvent("app/hangman_android_final_result", function(value) {
          console.log("android time = " + value)
          //var android_time = value
          value = value.toString();
          result = value.split(' ');
          var android_lives = parseInt(result[0])
          var android_time = parseInt(result[1])
          //case 1: both users lose
          console.log("android_lives = " + android_lives)
          console.log("android_time = " + android_time)

        if((android_lives == 0) && (lives==0)){
          console.log("both users died")
          RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
            ALMemory.raiseEvent("app/hangman_victory", "tie")
            ALTextToSpeech.say("YOU BOTH DIED That is a real shame. or is it...")
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
            console.log("android_time = " + android_time);
            console.log("pepper_time = " + pepper_time);
            RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
              ALMemory.raiseEvent("app/hangman_victory", "pepper")
              //ALTextToSpeech.say("YOU WON GOOD JOB. That was close tho")
              document.getElementById("waiting").innerHTML = "YOU WIN"
            });
          }
          else if (pepper_time > android_time){
            console.log("android_time = " + android_time);
            console.log("pepper_time = " + pepper_time);
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
        pepper_time = getTime(pepper_time)
        console.log(pepper_time)
        //check if android has finished
        clear_display();
        check_android_finish_status();
        stopTimer();
        displayHomeBtn();
        //check to see who has won game
        var waiting = document.getElementById("waiting")
        waiting.innerHTML = "Please wait for Android. Feel Free to Annoy Android while you wait!"
        result_check();
        //restart game for now later change to different screen
        //restart();
      }
    }

    //Displays home button after pepper finishes hangman game
    var displayHomeBtn = function (){
      console.log("creating button!")
      var button = document.getElementById('home')
      
      button.classList.remove("hidden")
      //button.setAttribute('class', 'play_again')
    }

    function changePage(){
      window.location.href = 'home.html'
    } 
    //checks if Pepper user has lost
    var loseCheck = function(){
      if(numOfIncorrectGuesses==6){
        alert("YOU DIED")
        //add code to connect to pepper
        //check if android has finished
        stopTimer();
        pepper_time = getTime(pepper_time)
        console.log(pepper_time)
        clear_display();
        displayHomeBtn();
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
      if(numOfIncorrectGuesses==0){image = "images/pephang1.png"}
      else if (numOfIncorrectGuesses==1){image= "images/pephang2.png"}
      else if (numOfIncorrectGuesses==2)(image = "images/pephang3.png")
      else if (numOfIncorrectGuesses==3)(image = "images/pephang4.png")
      else if (numOfIncorrectGuesses==4)(image = "images/pephang5.png")
      else if (numOfIncorrectGuesses==5)(image = "images/pephang6.png")
      else if (numOfIncorrectGuesses==7)(image = "images/pephang7.png")
      else{image= "images/hang1.png"}

      document.getElementById("stickguy").src = image;
    }

    
  
    // Displays the placeholder for gue
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
  
    //grabs info from android user from ALMemory
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
      //set game info 
      RobotUtils.subscribeToALMemoryEvent("app/hangman_game_info", function(value) {
        game_info = value.toString();
        game_info = game_info.split('/');
        console.log(game_info)
        hint = game_info[0]
        word = game_info[1]
        android_username = game_info[2]
        console.log(typeof(word))
        word = word.replace(/\s/g, "-");
        word = word.toLowerCase(word)
        buttons();
        //set game values to start
        numOfIncorrectGuesses = 0;
        numOfCorrectGuesses = 0;
        lives = 6;
        drawHangman();
        androidHint = hint
        showClue.innerHTML = "HINT:  " + androidHint;
        geusses = [ ];
        space = 0;
        result();
        startTime = new Date();
        numOfLivesDisplay();
        timer();
      });
      
    }
  
    start();  //starts the hangman game 
    
   
  

     // takes user back to home page once game ends
    document.getElementById('home').onclick = function(){
      window.setTimeout(changePage, 1000)  
    };

    //change to home page
    function changePage(){
      window.location.href = 'home.html'

    }

    //clears discription when game is over
    var clear_display = function (){
      correct.parentNode.removeChild(correct);
      letters.parentNode.removeChild(letters);
      showClue.innerHTML = "";
    }

    //restarts game TESTING PURPOSES 
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
      ALMemory.raiseEvent("app/annoy_android", "song");
      ALTextToSpeech.say("Let's make android sing");
      console.log("Connected to services");
    });
  }

  function eventDance(){
    console.log("EVENT Dance")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "vibration");
      ALTextToSpeech.say("Let's make android dance");
      console.log("Connected to services");
    });
  }

  function eventTool(){
    console.log("EVENT Tool")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "vibration");
      ALTextToSpeech.say("Let's make android vibrate");
      console.log("Connected to services");
    });
  }
  function eventCheer(){
    console.log("EVENT Cheer")
    RobotUtils.onServices(function(ALMemory, ALTextToSpeech) {
      ALMemory.raiseEvent("app/annoy_android", "song");
      ALTextToSpeech.say("Let's make android popup");
      console.log("Connected to services");
    });
  }

  //sets timer for game 
  function timer(){
    //console.log("starting timer")
    var currentTime = new Date()
    //get current time then display time elapsed since then. 
    var timeDiff = startTime - currentTime;
    //strip off milliseconds
    timeDiff /= 1000;

    //get seconds
    var total_seconds = Math.round(timeDiff);
    //console.log("seconds = " + total_seconds)
    if(total_seconds < 0){
      total_seconds = total_seconds * (-1)
    }
    
    var min = total_seconds / 60;
    var seconds = total_seconds % 60;
    min = Math.floor(min)
    min = checkTime(min);
    seconds = checkTime(seconds);
    var milliseconds = currentTime.getMilliseconds();
    //console.log(min)
    //console.log(seconds)
    document.getElementById('timer').innerHTML = min + ':' + seconds + ":" + milliseconds;
    if(stillPlaying == 'true'){
      var t = setTimeout(timer,200)
    }
   
  }

  //adds 0 to numbers <10
  function checkTime(i){
    if (i<10){
      i = "0" + i
    }
    return i;
  }
  
  //stops timer 
  function stopTimer(){
    stillPlaying = 'false'
  }
  
  //calculates total time played
  function getTime(time){
    //console.log("starting timer")
    var currentTime = new Date()
    //get current time then display time elapsed since then. 
    var timeDiff = startTime - currentTime;
    //strip off milliseconds
    timeDiff /= 1000;

    //get seconds
    var total_seconds = Math.round(timeDiff);
    time = total_seconds * (-1)
    return time
  }
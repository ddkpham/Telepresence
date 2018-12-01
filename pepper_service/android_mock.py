#THIS IS A SERVICE THAT MOCKS ANDROID POSTS/RESPONSES TO TEST PEPPER HANGMAN GAME
#TO USE SSH INTO PEPPER AND RUN NEEDED COMMANDS
import qi
import sys


class MyFooService:
  def __init__(self, session, *args, **kwargs):
    #define a signal 'onBang'
    self.tts = session.service("ALTextToSpeech")
    self.mem = session.service("ALMemory")
    self.prefmanager = session.service("ALPreferenceManager")
    self.subscribe()
	
  #define a bang method that will trigger the onBang signal
  def bang(self):
    #trigger the signal with 42 as value
    self.tts.say("Hello")

  #Mocks android sending a finish game message
  def finish_game(self):
      self.tts.say("android finished")
      self.mem.raiseEvent("app/hangman_android_user_lives", '4')
      self.mem.raiseEvent("app/hangman_android_user_time", '10')
      self.mem.raiseEvent("app/hangman_android_finish_status", 'finish')
      self.mem.raiseEvent("app/hangman_android_final_result", "6 10")

  #Resets finish status // Run when restarting hangman game for testing 
  def reset_finish_status(self):
      self.mem.raiseEvent("app/hangman_android_user_lives", '6')
      self.mem.raiseEvent("app/hangman_android_user_time", '20')
      self.mem.raiseEvent("app/hangman_android_finish_status", 'nope')    
    #start game 


  def start_game(self):
      self.tts.say("starting game")
      self.mem.raiseEvent("app/incoming_game_request", "1")
      self.mem.raiseEvent("app/hangman_android_user", "FionaBot")
      self.mem.raiseEvent("app/hangman_hint_for_pepper", "what is the secret to life")
      self.mem.raiseEvent("app/hangman_word_for_pepper", "naps")

  #Mocks Android word/hint
  def send_android(self):
      self.mem.raiseEvent("app/hangman_android_word", "word")
      self.mem.raiseEvent("app/hangman_android_hint", "hint")

  #Mocks a game request 
  def go_to_game(self):
    self.mem.raiseEvent("app/hangman_start_game", "test")

  #Mocks an end game request
  def end_game(self):
    self.tts.say("end game")
    self.mem.raiseEvent("app/hangman_victory", "pepper")
  
  #Sets PepperID in prefmanager 
  def set_pepid(self):
    self.tts.say("setting pep id")
    self.prefmanager.setValue("telepresence.DASK", "pepper_id2", "fraser")

  #Gets PepperID in prefmanager
  def get_pepid(self):
    self.tts.say("getting pep id")
    test = self.prefmanager.getValue("telepresence.DASK", "pepper_id2")
    if test is None:
      print("no pep id")
    else:
      print("pepid = " + test)

  #Sets current pepid in ALmemory. 
  def set_current_pepid(self):
    self.tts.say("setting current_pepid")
    self.mem.raiseEvent("app/current_pepper_id", "fraser" )

  #Sets current pepid in ALmemory
  def get_current_pepid(self):
    self.tts.say("get current pepid")
    test = self.mem.getData("app/current_pepper_id")
    print(test)

  def subscribe(self):
    self.mem.subscribeToEvent("FrontTactilTouched", "foo", "bang")
    #self.mem.subscribeToEvent("app/incoming_game_request", "foo", "start_game")
  


#create an application
app = qi.Application()
app.start()

#create an instance of MyFooService
myfoo = MyFooService(app.session)

#let's register our service with the name "foo"
id = app.session.registerService("foo", myfoo)

#let the application run
app.run()

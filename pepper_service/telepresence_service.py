import qi
import sys
import random, string
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import json 
import urllib2
import urlparse
import time
import hashlib, binascii
from pepper_server_service import *

#Test
#global PepperSecurityKey = None

class MyTabletService:
  def __init__(self, session, *args, **kwargs):
    #Sets all subscriptions and grabs all ALModules required for service
    self.tts = session.service("ALTextToSpeech")
    self.memory = session.service("ALMemory")
    self.tabletService = session.service("ALTabletService")
    self.frameManager = session.service("ALFrameManager")
    self.prefmanager = session.service("ALPreferenceManager")
    self.subscribe()
    

  
  #proactive response for head touching 
  def proactive_response(self):
    #self.tts.say("BANG BANG BANG BANG")
    username = self.memory.getData("app/active_user")
    print("username = " + username)
    psk = self.memory.getData("app/pepper_security_key")
    psk = self.update_psk(psk)
    print("psk = " + psk)
    pep_id = self.prefmanager.getValue("telepresence.DASK", "pepper_id2")
    print("pepid = " + pep_id)
    msg = 'Someone touched my head!'


    server_repsonse = handleHTTPRequest.send_proactiveRequest(msg, psk, username, pep_id)
    print(server_repsonse)
    if server_repsonse == '400':
      print("missing data")
    elif server_repsonse =='409':
      print("user not found")
    elif server_repsonse == '410':
      print('message failed to send')
    elif server_repsonse == '200':
      print("SUCESS")
    else:
      print("ERROR: RESPONSE NOT RECOGNIZED ")

  #Activates proactive response. This is to ensure an Android User is logged in 
  def activate_proactive(self):
    self.memory.subscribeToEvent("FrontTactilTouched", "telepresence_tablet", "proactive_response")
    

  #Will handle sign in logic
  def signup_process(self, value):
    #To check if login fields are all present
    correct_signup_fields = True
    email_is_valid = False
    if value==[]:
      print "Key is not recognized"
    else:
      #get sign up info
      signup_email = self.memory.getData(value)
      signup_firstname = self.memory.getData("app/signup_firstname")
      signup_lastname = self.memory.getData("app/signup_lastname")
      signup_username = self.memory.getData("app/signup_username")
      signup_password = self.memory.getData("app/signup_password")
      
      #Check if fields have values and have pepper respond
      if not signup_email:
        correct_signup_fields = False
      else:
        for e in signup_email:
          if e == "@":
            email_is_valid = True
      if not signup_firstname:
        correct_signup_fields = False
      if not signup_lastname:
        correct_signup_fields = False
      if not signup_username:
        correct_signup_fields = False
      if not signup_password:
        correct_signup_fields = False
      

      if correct_signup_fields:
        print "Sign up was a success"
        #correct login fields, check server to see if it is valid
        response_check = handleHTTPRequest.addUser(signup_username,signup_password,signup_email,signup_firstname,signup_lastname)
        handleHTTPRequest.addUserResponseProcess(response_check)
        print("server response = " + response_check)
        if response_check == "500":
          print("Internal Server Error")
          self.tts.say("Try signing up later. It looks like the cloud gods are not with us today")
        elif response_check == "400":
          print("Missing info ")
        elif response_check == '409':
          print("Username is already used. Try a different one")
          self.tts.say("That Username is already used. Try a different one")
        elif response_check == '412':
          print("Email is already used")
          self.tts.say("Email is already used. Try a different one")
        elif 'ASK' in response_check: 
          print("Successful Signup")
          self.tts.say("YAY! Thanks for friending me. I'm so excited for all our adventures")
          #Raising this event will change page to login.html
          self.memory.raiseEvent("app/signup_status", "login.html")
        else:
          print("ERROR: SERVER RESPONSE NOT RECOGNIZED")
        
      else:
        print "Sign up was a failure"
        self.tts.say("It looks like you might've forgotten a field or two. Don't give up on us!")
        if not email_is_valid:
          print "email was invalid"
        self.memory.raiseEvent("app/signup_status", "signup.html")
        print "set app/signup_status to bad"
      

  #Grabs path to app
  def _getAppName(self):
      import os
      if self.frameManager:
          #behaviorPath = os.path.normpath(self.frameManager.getBehaviorPath(self.behaviorId))
          #behaviorPath = "/home/nao/.local/share/PackageManager/apps/.lastUploadedChoregrapheBehavior/behavior_1"
          behaviorPath = self.memory.getData("app/html_path")
          print "This is path pulled from ALMemory" + behaviorPath
          if os.path.isdir(behaviorPath):
              behaviorPath = os.path.join(behaviorPath, "")
          appsFolderFragment = os.path.join("PackageManager", "apps")
          print "This is the value of appsFolderFragment : " + appsFolderFragment
          print "This is the value of behaviourPath : " + behaviorPath
          if not (appsFolderFragment in behaviorPath):
              print("appsFolderFragment is not in behaviorPath")
              return None
          fragment = behaviorPath.split(appsFolderFragment, 1)[1]
          fragment = fragment.split("/")[1]
          return fragment.lstrip("\\/")
      else:
          print("No ALFrameManager")
          return None

  #RUNS login html page
  def run_login_html(self, value, tabletService):
        # We create TabletService here in order to avoid
        # problems with connections and disconnections of the tablet during the life of the application
      html_file = self.memory.getData("app/login_status")
      print "This is the html file to be rendered " + html_file
      appName = str(self._getAppName())+"/"+ str(html_file)
      print "This is appName: " + appName
      state = False
      if appName:
          #should have started with initiation of class
          if self.tabletService:
              if self.tabletService.loadApplication(appName):
                  print "Successfully set application: " + appName
                  self.tabletService.showWebview()
                  state = True
              else:
                  print "Got tablet service, but failed to set application:" + appName
          else:
              print "Couldn't find tablet service, so can't set application:" + appName
      if state:
          print "Success"
            #self.onSuccess()
      else:
        print "Failure"
            #self.onFailure()

  #Runs sign up page 
  def run_signup_html(self, value, tabletService):
        # We create TabletService here in order to avoid
        # problems with connections and disconnections of the tablet during the life of the application
      html_file = self.memory.getData("app/signup_status")
      print "running sign up process"
      print "This is the html file to be rendered " + html_file
      appName = str(self._getAppName())+"/"+ str(html_file)
      print "This is appName: " + appName
      state = False
      if appName:
          #should have started with initiation of class
          if self.tabletService:
              if self.tabletService.loadApplication(appName):
                  print "Successfully set application: " + appName
                  self.tabletService.showWebview()
                  state = True
              else:
                  print "Got tablet service, but failed to set application:" + appName
          else:
              print "Couldn't find tablet service, so can't set application:" + appName
      if state:
          print "Success"
            #self.onSuccess()
      else:
        print "Failure"
            #self.onFailure()


  #Handles hangman game request
  #redirects screen to hangman request page
  def run_hangman_game(self, value, tabletService):
        # We create TabletService here in order to avoid
        # problems with connections and disconnections of the tablet during the life of the application
      print "initializing hangman game"
      self.tts.say("incoming game request")
      if value == []:
        print ("Key is not recognized ")
        return
      incoming_request = self.memory.getData("app/incoming_game_request")
      if incoming_request == "0":
        print("no game request")
        return
      html_file = "request.html"
      print "This is the html file to be rendered " + html_file
      appName = str(self._getAppName())+"/"+ str(html_file)
      print "This is appName: " + appName
      state = False
      if appName:
          #should have started with initiation of class
          if self.tabletService:
              if self.tabletService.loadApplication(appName):
                  print "Successfully set application: " + appName
                  self.tabletService.showWebview()
                  state = True
              else:
                  print "Got tablet service, but failed to set application:" + appName
          else:
              print "Couldn't find tablet service, so can't set application:" + appName
      if state:
          print "Success"
            #self.onSuccess()
      else:
        print "Failure"
            #self.onFailure()

  def start_hangman_game(self, value, tabletService):
        # We create TabletService here in order to avoid
        # problems with connections and disconnections of the tablet during the life of the application
      print "initializing hangman game!"
      #self.tts.say("incoming game request")
      if value == []:
        print ("Key is not recognized ")
        return
      
      html_file = "game.html"
      print "This is the html file to be rendered " + html_file
      appName = str(self._getAppName())+"/"+ str(html_file)
      print "This is appName: " + appName
      state = False
      if appName:
          #should have started with initiation of class
          if self.tabletService:
              if self.tabletService.loadApplication(appName):
                  print "Successfully set application: " + appName
                  self.tabletService.showWebview()
                  state = True
              else:
                  print "Got tablet service, but failed to set application:" + appName
          else:
              print "Couldn't find tablet service, so can't set application:" + appName
      if state:
          print "Success"
            #self.onSuccess()
      else:
        print "Failure"
            #self.onFailure()

  #Will handle login logic
  def login_process(self, value):
    #self.tts.say("Login attempt is in process...")
    if value == []:
      self.tts.say("Key not recognized")   
    username = self.memory.getData("app/username")
    password = self.memory.getData("app/password")
    pep_id = self.prefmanager.getValue("telepresence.DASK", "pepper_id2")
    if pep_id is None:
      self.tts.say("No PepperID is detected. Please register a pepperid to this pepper")
      print("No PepperID is detected. Please register a pepperid to this pepper")
    check_user_input = True

    #no userinput handle empty input 
    if not username:
      self.tts.say("Don't forget to input a username you cutie!")
      check_user_input = False
    if not password:
      self.tts.say("Dont forget to input a password you cutie!")
      check_user_input = False
    
    if check_user_input:
      print "login fields are good. checking cloud server!"
      #Change pep ID later, create a ALmemory event for PepID
      server_repsonse = handleHTTPRequest.pepperLogin(username, password, pep_id)
      handleHTTPRequest.pepperLoginResponse(server_repsonse)
      print("server response = " + server_repsonse)
      print(type(server_repsonse))
      #RETURN TYPE: JSON 
		#ON SUCCESS: RETURNS 200 
		#ON FAILURE: 400 Bad Request. Missing input
		#409 Conflict User not found in database
		#403 Forbidden User not authorized for Pep Id
		#401 Password does not match
      if server_repsonse == '403':
        self.tts.say("User is not authorized for PepperID")
      elif server_repsonse == '401':
        self.tts.say("Username or Password is invalid. Please be careful when typing")
      elif server_repsonse == '400':
        self.tts.say("It looks like you were missing some fields try again")
      elif server_repsonse == '200':
        self.tts.say("Hello! welcome back friend")
      #global PepperSecurityKey = generatePSK(15)
        self.memory.raiseEvent("app/login_status", "home.html")
      else:
        print("ERROR: SERVER RESPONSE IS NOT RECOGNIZED")
    else:
      print "login is incomplete"
      self.memory.raiseEvent("app/login_status", "login.html")
      
  #Generates PSK For Session  
  def generatePSK(self):
   letters = string.ascii_lowercase
   return ''.join(random.choice(letters) for i in range(15))

  #Hashes PSK for security
  def hashPSK(self, psk):
    salt = 'HcU8jhcPFG'
    psk = psk + salt
    hashed_psk = hashlib.sha256(psk).hexdigest()
    return hashed_psk

  #Registers Pepper into cloud
  def pepperRegister(self, value):
    if value ==[]:
      print "key is not recognized"
    pepperid = self.memory.getData(value)
    username = self.memory.getData("app/username")
    print "this is pepperid  "  + pepperid

    #Generate a PSK 
    psk = self.generatePSK()
    hashed_psk = self.hashPSK(psk)
    print("hashed_psk = " + hashed_psk)
    #Put into ALMemory
    self.memory.raiseEvent("app/pepper_security_key", str(hashed_psk))
    #Send to Cloud server and check response
    server_repsonse = handleHTTPRequest.addPepper(pepperid, username, hashed_psk)
    handleHTTPRequest.addPepperResponseProcess(server_repsonse)
    if server_repsonse == '200':
      print("registration success")
      self.tts.say("Yay! I guess we can be friends now! This is a new pepper")
      #Put into Preference manager. Pepper now has a permnament Pep ID
      self.prefmanager.setValue("telepresence.DASK", "pepper_id2", str(pepperid))
      self.memory.raiseEvent("app/signup_status", "home.html")
    elif server_repsonse == "500":
      print("Internal Server Error")
      self.tts.say("uh oh. that wasn't supposed to happen try again ")
    elif server_repsonse =="400":
      print("Bad Request")
      self.tts.say("It looks like youre missing some fiends")
    elif server_repsonse == "409":
      print("Conflict")
      self.tts.say("Looks like Pepper is already in use")
      #self.memory.raiseEvent("app/signup_status", "login.html")
    elif server_repsonse == "410": 
      print("No server repsonse")
      self.tts.say("Reregister pepper or go back to login")
    else:
      print("ERROR: SERVER RESPONSE NOT RECOGNIZED")
    

  #Extracts authorization from requests from user via Cloud Server
  def extractAuthRequests(self, value):
    print "AuthRequest event raised!"
    psk = self.memory.getData('app/pepper_security_key')
    pep_id = self.memory.getData('app/current_pepper_id')
    psk = self.update_psk(psk)
    server_repsonse = handleHTTPRequest.getAuthRequests(pep_id, psk)
    #response_check = handleHTTPRequest.getAuthRequestsResponseProcess(server_repsonse)
    if server_repsonse=='500':
      print("FAILURE")
    else:
      print (server_repsonse)
      #insert into ALmemory
      self.memory.raiseEvent("app/tablet_new_auth_request", server_repsonse)


  #SEND ANIMATION REQUEST TO ANDROID
  def annoy_android(self, value):
    #self.tts.say("let's annoy android!")
    if value == []:
      print("key is not recognized")
    android_message = self.memory.getData("app/annoy_android")
    android_user = self.memory.getData("app/hangman_android_user")
    print("android message = " + android_message)
    self.tts.say("annoying android with message")
    #Send message to android! 
    server_response = handleHTTPRequest.send_android_animation(android_message, android_user)
    if server_response == '500':
      print("Internal Server Error")
    elif server_response == '409':
      print("Invalid Data")
    else:
      print("Success")


  #SEND ANDROID GAME INFO 
  def send_android_game_info(self, value):
    self.tts.say("sending to android!")
    if value == []:
      print ("key is not recognized")
    username = self.memory.getData("app/username")
    hint = self.memory.getData("app/hangman_android_hint")
    word = self.memory.getData("app/hangman_android_word")
    android_username = self.memory.getData("app/hangman_android_user")
    print ("username = " + username)
    print ("hint = " + hint)
    print ("word = " + word)
    print ("username = " + android_username)
    #Send to cloud server so it can relay it to android
    print("sending game info to android!")
    http_result = handleHTTPRequest.send_accept(word, hint, android_username, username )
    if http_result == '409':
      print("Data is wrong")
    elif http_result == '500':
      print("Internal Server Error")
    else:
      print("success!")

    
  
  #Grabs Android hint and word for Pepper hangman game 
  def grab_android_game_info(self, value):
    self.tts.say("extracting game info!")
    if value ==[]:
      print("key not recognized")
      return
    #collect information to initiate game
    pepper_hint = self.memory.getData("app/hangman_hint_for_pepper")
    pepper_word = self.memory.getData("app/hangman_word_for_pepper")
    android_user = self.memory.getData("app/hangman_android_user")

    #raise events so that pepper user may pick up info 
    print("pepper_hint = " + pepper_hint)
    print("pepper_word = " + pepper_word)
    print("android_user = " + android_user)
    game_info = pepper_hint + "/" + pepper_word + "/" + android_user
    print("game_info = " + game_info)
    self.memory.raiseEvent("app/hangman_hint_for_pepper", pepper_hint)
    self.memory.raiseEvent("app/hangman_word_for_pepper", pepper_word)
    self.memory.raiseEvent("app/hangman_android_user", android_user)
    self.memory.raiseEvent("app/hangman_game_info", game_info)


  #Checks to see if android user is finished with hangman game
  def android_status(self, value):
    #self.tts.say("checking to see if android is finished!")
    #check android status, if not finished keep checking 
    android_status = self.memory.getData("app/hangman_android_finish_status")
    #wait until android is finished
    print("this is android_status = " + android_status)
    while (android_status != 'finish'):
      print("waiting for android to finish. Sleeping for 1 sec...")
      android_status = self.memory.getData("app/hangman_android_finish_status")
      time.sleep(1)

    print("android is finished! waiting for response...")
    time.sleep(2)
    #once android is finished
    android_lives = self.memory.getData("app/hangman_android_user_lives")
    android_time = self.memory.getData("app/hangman_android_user_time")
    print("sending android lives = " + android_lives)
    print("sending android time = " + android_time)
    android_lives = str(android_lives)
    android_time = str(android_time)
    result = android_lives + " " + android_time
    print("result = " + result)
    
    #raise event so that pepper can receive data
    self.memory.raiseEvent("app/hangman_android_user_lives", android_lives)
    self.memory.raiseEvent("app/hangman_android_user_time", android_time)
    self.memory.raiseEvent("app/hangman_android_final_result", result)
    print("android lives and time have been sent")

    ##reset android finish status 
    self.memory.raiseEvent("app/hangman_android_finish_status", "none")


  #Deauthorize a user from authorization list and database for that PepID
  def deauth_user(self, value):
    print("deauthorizing user!")
    if value ==[]:
      print("key is not recognized")
      return
    username = self.memory.getData('app/deauth_user')
    psk = self.memory.getData('app/pepper_security_key')
    pep_id = self.memory.getData('app/current_pepper_id')
    psk = self.update_psk(psk)

    print("user to be deauthorized = " + username)
    print("hashed psk = " + psk)

    server_response = handleHTTPRequest.deAuth(username, pep_id, psk)
    if server_response is None:
      print("server sent back nothing")
    print("server response = " + server_response)
    if server_response == '200':
      print("Deauth Success!")
    elif server_response == '500':
      print("Internal Server Error")
    elif server_response == '400':
      print("Bad Request missing input data")
    elif server_response == '409':
      print("Conflict")
    elif server_response == '403':
      print("Unauthorized PSK check failed")
    else:
      print("ERROR: SERVER NOT RECOGNIZED")


  #Displays hangman result and sends android results
  def hangman_result(self, value):
    self.memory.unsubscribeToEvent("app/hangman_victory", "telepresence_tablet")
    self.tts.say("calculating hangman results")
    if value ==[]:
      print("key is not recognized")
      return
    #send android user results of who won
    winner = self.memory.getData("app/hangman_victory")
    print(" Winner = " + winner)
    print("Nicely done")

    android_user = self.memory.getData("app/hangman_android_user")
    if(winner == 'pepper'):
      result = 0
    elif (winner=='android'):
      result = 2
    else:
      result = 1
    
    #print(android_user)
    server_response = handleHTTPRequest.send_endgame(result, android_user)
    if server_response == '500':
      print("Internal Server Error")
    elif server_response == '409':
      print("Invalid data")
    else:
      print("hangman result sent")

    self.memory.subscribeToEvent("app/hangman_victory", "telepresence_tablet", "hangman_result")
    self.memory.raiseEvent("app/game_mode", "0")


  def send_animation(self):
    self.tts.say("buzz")


  #get currently authorized users 
  def get_currently_authorized(self,value):
    self.tts.say("getting currently authorized users")
    pep_id = self.memory.getData("app/current_pepper_id")
    print("pepper id = " + pep_id)
    #Use psk 
    psk = self.memory.getData("app/pepper_security_key")
    psk = self.update_psk(psk)

    server_repsonse = handleHTTPRequest.getAuthUsers(pep_id, psk)
    print("server_response = " + server_repsonse)
    self.memory.raiseEvent("app/authorized_users", server_repsonse)

  #Process authorization request response from pepper user // Deny or Accept Anroid User
  def authorization_processing(self, value):
    print("Processing authorization request")
    action = self.memory.getData("app/auth_reply")
    username = self.memory.getData("app/auth_username_reply")
    pep_id = self.memory.getData("app/current_pepper_id")
    psk = self.memory.getData("app/pepper_security_key")
    psk = self.update_psk(psk)

    #Success code:  200
    #Error code: 500 - check cloud server log for details
    #400 - missing input 
    #403 unauthorized psk check failed, relogin pepper
    #409 conflict, user, pep id not found 
    if action == 'accept':
      server_repsonse = handleHTTPRequest.authorizeUser(pep_id, psk, username)
      if server_repsonse == '200':
        print("Success")
      elif server_repsonse == "500":
        print("Internal Server Error")
      elif server_repsonse == "400":
        print("missing input data")
      elif server_repsonse == "403":
        print("PSK check failed, relogin pepper")
      elif server_repsonse == "409":
        print("userAuth or Pep Id not found")
      else:
        print("ERROR: SERVER RESPONSE NOT RECOGNIZED")
    elif action == 'deny':
      #NEED TO UPDATE THIS 
      print('deny request')
    
    else:
      server_repsonse = handleHTTPRequest.deAuth(username, pep_id, psk)
      if server_repsonse == '200':
        print("Success")
      elif server_repsonse == "500":
        print("Internal Server Error")
      elif server_repsonse == "400":
        print("missing input data")
      elif server_repsonse == "403":
        print("PSK check failed, relogin pepper")
      elif server_repsonse == "409":
        print("userAuth or Pep Id not found")
      else:
        print("ERROR: SERVER RESPONSE NOT RECOGNIZED")
      print("ACTION NOT RECOGNIZED ERROR ")
    

  def photo_web_display(self, value, tabletService):
        # We create TabletService here in order to avoid
        # problems with connections and disconnections of the tablet during the life of the application
      print "initializing photo page!"
      #self.tts.say("incoming game request")
      if value == []:
        print ("Key is not recognized ")
        return
      
      html_file = "photo.html"
      print "This is the html file to be rendered " + html_file
      appName = str(self._getAppName())+"/"+ str(html_file)
      print "This is appName: " + appName
      state = False
      if appName:
          #should have started with initiation of class
          if self.tabletService:
              if self.tabletService.loadApplication(appName):
                  print "Successfully set application: " + appName
                  self.tabletService.showWebview()
                  state = True
              else:
                  print "Got tablet service, but failed to set application:" + appName
          else:
              print "Couldn't find tablet service, so can't set application:" + appName
      if state:
          print "Success"
            #self.onSuccess()
      else:
        print "Failure"
            #self.onFailure()

  def deny_game_request(self, value):
    username = self.memory.getData("app/hangman_android_user")
    server_repsonse = handleHTTPRequest.send_deny(username)
    print("server_response = " + str(server_repsonse))
    if server_repsonse == '400':
      print("missing android username")
    elif server_repsonse == '409':
      print("user not found")
    elif server_repsonse == '410':
      print("msg failed to send")
    elif server_repsonse == '200':
      print("success")
    else:
      print("ERROR: INTERNERAL SERVER ERROR ")
  
  def send_photo(self, value):
    self.tts.say("photo alert")
    if value ==[]:
      print ("key is not recognized")
    print("sending photo to photo.html")
    photo_data = self.memory.getData("photo")
    self.memory.raiseEvent("photo", str(photo_data))

  def update_psk(self,psk):
    hashed_psk = self.hashPSK(psk)
    self.memory.raiseEvent("app/pepper_security_key" , hashed_psk)
    return hashed_psk

  

  #unsubscribe to events to reset fields 
  def unsubscribe(self):
    self.memory.unsubscribeToEvent("app/username", "telepresence_tablet")
    self.memory.unsubscribeToEvent("app/signup_email", "telepresence_tablet")
  
    

  #subscribe to all memory events 
  def subscribe(self):
    self.memory.subscribeToEvent("app/password", "telepresence_tablet", "login_process")
    self.memory.subscribeToEvent("app/signup_email" , "telepresence_tablet", "signup_process" )
    self.memory.subscribeToEvent("app/login_status", "telepresence_tablet" , "run_login_html")
    self.memory.subscribeToEvent("app/signup_status", "telepresence_tablet", "run_signup_html")
    self.memory.subscribeToEvent("app/current_pepper_id","telepresence_tablet","pepperRegister")
    self.memory.subscribeToEvent("app/new_auth_requests","telepresence_tablet","extractAuthRequests")
    self.memory.subscribeToEvent("app/deauth_user", "telepresence_tablet", "deauth_user")
    self.memory.subscribeToEvent("app/currently_authorized_request", "telepresence_tablet", "get_currently_authorized")
    self.memory.subscribeToEvent("app/auth_username_reply", "telepresence_tablet", "authorization_processing" )
    self.memory.subscribeToEvent("app/photo_received", "telepresence_tablet", "photo_web_display")
    self.memory.subscribeToEvent("app/photo_request", "telepresence_tablet", "send_photo")
    #FOR HANGMAN GAME 
    self.memory.subscribeToEvent("app/annoy_android", "telepresence_tablet", "annoy_android")
    self.memory.subscribeToEvent("app/hangman_android_word", "telepresence_tablet", "send_android_game_info")
    self.memory.subscribeToEvent("app/hangman_start_game", "telepresence_tablet", "start_hangman_game")
    self.memory.subscribeToEvent("app/incoming_game_request", "telepresence_tablet", "run_hangman_game")
    self.memory.subscribeToEvent("app/hangman_initiate", "telepresence_tablet", "grab_android_game_info")
    self.memory.subscribeToEvent("app/hangman_android_check_status", "telepresence_tablet", "android_status")
    self.memory.subscribeToEvent("app/hangman_victory", "telepresence_tablet", "hangman_result")
    self.memory.subscribeToEvent("app/deny_game_request", "telepresence_tablet", "deny_game_request" )
    self.memory.subscribeToEvent("app/active_user", "telepresence_tablet", 'activate_proactive')

#create an application
app = qi.Application()
app.start()

#create an instance of MyTabletService
MyTabletService = MyTabletService(app.session)

#let's register our service with the name "telepresence_tablet"
id = app.session.registerService("telepresence_tablet", MyTabletService)

#let the application run
app.run()

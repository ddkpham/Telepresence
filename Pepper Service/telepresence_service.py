import qi
import sys
import random, string
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import json 
import urllib2
import urlparse
from pepper_server import *

#Test
#global PepperSecurityKey = None

class MyTabletService:
  def __init__(self, session, *args, **kwargs):
    #define a signal 'onBang'
    self.tts = session.service("ALTextToSpeech")
    self.memory = session.service("ALMemory")
    self.tabletService = session.service("ALTabletService")
    self.frameManager = session.service("ALFrameManager")
    self.subscribe()
    


  def generatePSK(self, length):
   letters = string.ascii_lowercase
   return ''.join(random.choice(letters) for i in range(length))
  
  #define a bang method that will trigger the onBang signal
  def bang(self):
    #trigger the signal with 42 as value
    self.tts.say("BANG BANG BANG BANG")
  
  #Will handle sign in logic
  def signup_process(self, value):
    #self.tts.say("attempting to sign up a new user")
    #To check if login fields are all present

    #self.tts.say("Lets check if the user entered all fields correctly")
    correct_login_fields = True
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
        correct_login_fields = False
      else:
        for e in signup_email:
          if e == "@":
            email_is_valid = True
      if not signup_firstname:
        correct_login_fields = False
      if not signup_lastname:
        correct_login_fields = False
      if not signup_username:
        correct_login_fields = False
      if not signup_password:
        correct_login_fields = False
      

      if correct_login_fields:
        print "Sign up was a success"
        #correct login fields, check server to see if it is valid
        response_check = handleHTTPRequest.addUser(signup_username,signup_password,signup_email,signup_firstname,signup_lastname)
        server_msg = handleHTTPRequest.addUserResponseProcess(response_check)
        if server_msg == "500":
          self.tts.say("Hmmmm it looks like you need a different username or email")
        else:
          self.tts.say("YAY! Thanks for friending me. I'm so excited for all our adventures")
          #Raising this event will change page to login.html
          self.memory.raiseEvent("app/signup_status", "login.html")
      else:
        print "Sign up was a failure"
        self.tts.say("It looks like you might've forgotten a field or two. Don't give up on us!")
        if not email_is_valid:
          print "email was invalid"
        self.memory.raiseEvent("app/signup_status", "signup.html")
        print "set app/signup_status to bad"
      


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

  #Will handle login logic
  def login_process(self, value):
    #self.tts.say("Login attempt is in process...")
    if value == []:
      self.tts.say("Key not recognized")   
    username = self.memory.getData(value)
    password = self.memory.getData("app/password")
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
      server_repsonse = handleHTTPRequest.pepperLogin(username, password, 'bob')
      response_check = handleHTTPRequest.pepperLoginResponse(server_repsonse)
      if response_check == '410':
        self.tts.say("User is not authorized for PepperID")
      elif response_check == '409':
        self.tts.say("Username or Password is invalid. Please be careful when typing")
      else:
        self.tts.say("Hello! welcome back friend")
      #global PepperSecurityKey = generatePSK(15)
        self.memory.raiseEvent("app/login_status", "home.html")
    else:
      print "login is incomplete"
      #self.tts.say("Poop")
      self.memory.raiseEvent("app/login_status", "login.html")
      
    
  
  def pepperRegister(self, value):
    if value ==[]:
      print "key is not recognized"
    pepperid = self.memory.getData(value)
    print "this is pepperid  "  + pepperid
    server_repsonse = handleHTTPRequest.addPepper(pepperid)
    response_check = handleHTTPRequest.addPepperResponseProcess(server_repsonse)
    if response_check == '200':
      self.tts.say("Yay! I guess we can be friends now! This is a new pepper")
      self.memory.raiseEvent("app/signup_status", "login.html")
    else:
      self.tts.say("This is a old pepper reporting for fun!")
      self.memory.raiseEvent("app/signup_status", "login.html")

  def extractAuthRequests(self, value):
    print "AuthRequest event raised!"
    pepperid = self.memory.getData("app/current_pepper_id")
    server_repsonse = handleHTTPRequest.getAuthRequests(pepperid, '127.0.0.1')
    #response_check = handleHTTPRequest.getAuthRequestsResponseProcess(server_repsonse)
    if server_repsonse=='500':
      print("FAILURE")
    else:
      print (server_repsonse)
      #insert into ALmemory
      self.memory.raiseEvent("app/tablet_new_auth_request", server_repsonse)
  #unsubscribe to events to reset fields 
  def unsubscribe(self):
    self.memory.unsubscribeToEvent("app/username", "telepresence_tablet")
    self.memory.unsubscribeToEvent("app/signup_email", "telepresence_tablet")

  #subscribe to all memory events 
  def subscribe(self):
    self.memory.subscribeToEvent("FrontTactilTouched", "telepresence_tablet", "bang")
    self.memory.subscribeToEvent("app/username", "telepresence_tablet", "login_process")
    self.memory.subscribeToEvent("app/signup_email" , "telepresence_tablet", "signup_process" )
    self.memory.subscribeToEvent("app/login_status", "telepresence_tablet" , "run_login_html")
    self.memory.subscribeToEvent("app/signup_status", "telepresence_tablet", "run_signup_html")
    self.memory.subscribeToEvent("app/current_pepper_id","telepresence_tablet","pepperRegister")
    self.memory.subscribeToEvent("app/new_auth_requests","telepresence_tablet","extractAuthRequests")

#create an application
app = qi.Application()
app.start()

#create an instance of MyTabletService
MyTabletService = MyTabletService(app.session)

#let's register our service with the name "telepresence_tablet"
id = app.session.registerService("telepresence_tablet", MyTabletService)

#let the application run
app.run()

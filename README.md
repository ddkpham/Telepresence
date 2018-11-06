Sprint 2 Project Details
Python Servers:
Created using Python Flask: http://flask.pocoo.org/docs/1.0/
Send requests using Requests: http://docs.python-requests.org/en/latest/user/quickstart/

Definitions:
Android: Android Device running Telepresence App
Cloud (Server): Python Server running on Google Cloud Platform 
Pepper Server: Python Server running on Pepper
Database: Google Cloud Platform mysql database
Security Key: randomly generated string for security

Connection:
Send/receive HTTP requests to communicate between Android, Cloud, Pepper
All data from Android to Pepper or vice versa will go through the Cloud

Authentication and Security:
User account, Username + Password combo
Security Key generation on Pepper or Cloud using random algorithm
Pepper’s Python Server should compare security keys before processing requests
Android must send security key in request requiring authentication

-new Android security key generated on each request?
-ALL security keys need to be hashed before being sent

-If time permits:
Json Web Token: https://en.wikipedia.org/wiki/JSON_Web_Token
HTTPS

//------------------------------------------------

Database Schema:
Pepper Entity: 
Pepper ID,String, PRIMARY KEY
IP Address, String
Pepper Security Key, String (Hashed)

AuthUsers Entity: 
Pepper ID,String
Username,String
Email,String
Composite Key(Pepper ID,Username)

User Entity: 
Username,String, PRIMARY KEY
Email,String, UNIQUE
Name,String
Password,String
Android Security Key,String (Hashed)

AuthRequest Entity: 
Pepper ID,String
Username,String
Email,String
Composite Key(Pepper ID,Username)

//------------------------------------------------

Device Requirements:
Pepper Tablet:
-show pepper ID
-register new pepper
-approve authorization requests, deauthorize users
-see authorized users
-add/remove users
-get information from Pepper Server
	-make requests to Cloud via Pepper Server

Android App:
-send authorization requests
-add/remove user account
-send requests to Cloud
-able to send requests including:
login, message, photo, reqAuth, deAuth, addUser, removeUser
Note:
-For login, if multiple Pepper IDs returned then ask user to choose one
	-maybe add a way for users to switch which Pepper they are interacting with

Pepper Server:
-send Pepper ID, IP Address, NEW pepper Security Key to Cloud when the app is started
-get information to the Tablet
-send/receive requests from cloud
-able to send requests including:
deAuth, addUser, removeUser, getAuthRequests, authorizeUser, setPepperActive
-able to handle requests including:
message,photo

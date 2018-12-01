﻿# Robot telepresense
Solution allows user to communicate with Pepper robot
## System Architecture
<img width="1052" alt="screen shot 2018-11-13 at 7 29 24 pm" src="https://user-images.githubusercontent.com/34588197/48458275-7fbe3b80-e77a-11e8-9f69-00dcce7f954d.png"/></br>
## Android Client
### Language (IDE)
Java (Android Studio)
### User Libraries in Android
* Google AppCompat
* [Picasso](http://square.github.io/picasso/) - Image downloading and caching library
* [ChatKit](https://github.com/stfalcon-studio/ChatKit) - Message UI library
* [CircleIndicator](https://github.com/ongakuer/CircleIndicator) - Customed Circle Indicator
* [Butterknife](http://jakewharton.github.io/butterknife/) - Field and method binding for Android views

### screenshot 
<img width="831" alt="screen shot 2018-11-30 at 6 57 43 am" src="https://user-images.githubusercontent.com/34588197/49296499-4b7e9680-f46d-11e8-949f-a2dba756c2a3.png">

## Cloud Server
### Prerequisites and Installation
Google Cloud Platform Account with an App Engine Project and a SQL Database Instance on the Google Cloud Platform
* Google Cloud SDK and Cloud SQL Proxy
* [Google Cloud SDK](https://cloud.google.com/sdk/install)
* [Cloud SQL Proxy](https://cloud.google.com/sql/docs/mysql/sql-proxy)
* Python 2.7 & 3 (Used Pacakages: Flask, Requests, flask_sqlalchemy, sqlalchemy)
```
which can be installed using pip
Ex: pip install <packagename>
```

### Deployment
To upload and run the Cloud Server in the Cloud:
* Using the Google Cloud SDK go to the Cloud Server folder from our repository
* run the command: gcloud app deploy </br>
To setup a local instance of the Cloud Server:
* Using the Google Cloud SDK go to the folder with the cloud_sql_proxy.exe you downloaded </br>
  a. Run a proxy by using the command: cloud_sql_proxy.exe -instances=<PROJECTNAME>=tcp:<PORT> </br>
      (Note: PROJECTNAME should be the “cloud_sql_instances:” value in app.yaml) </br>
* Using a terminal go to the Cloud Server folder from our repository </br>
  a. Run the Cloud Server instance by using:	python main.py

### Built With
* Flask – Web framework used
* Flask-sqlalchemy – Object Relational Mapper used to interact with Database

## Pepper Server
### Language
Python
### Built With
* Urllib2 - Python library for sending HTTP POST requests
* BaseHTTPServer - Python library for receiving and handling HTTP POST requests 

## WebApp for Tablet

Built with Bootstrap/JQuery, qimessaging.js and robotutilis.js from pepperhacking/robot-jumpstarter

* Pepperhacking/robot- jumpstarter Author(s): ekroeger@aldebaran.com
* Service built with naoqi (see qimessaging service guide for more details)


## Service for Pepper Server and Tablet

To install service onto Pepper's head (Service must be run to connect Pepper's Tablet to Pepper Server)
*   run command below to copy current directory into Pepper's head
*   rsync -av . nao@salt.local:~/telepresence_server
*   ssh into Pepper's head
*   cd into telepresence_server 
*   run telepresence_server.py



Copyright (C) 2015-2016 SBRE


### Language
HTML5, CSS, JavaScript



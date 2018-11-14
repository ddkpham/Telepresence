# Robot telepresense
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
![layer 0-8](https://user-images.githubusercontent.com/34588197/48456692-d294f480-e774-11e8-9fe6-8483908d95ca.png)
![layer 0-10](https://user-images.githubusercontent.com/34588197/48456694-d32d8b00-e774-11e8-8e98-603b4ec23e15.png)
![layer 0-11](https://user-images.githubusercontent.com/34588197/48456695-d32d8b00-e774-11e8-8a45-784ad2a7eb14.png) 
![layer 0-12](https://user-images.githubusercontent.com/34588197/48456696-d32d8b00-e774-11e8-96a3-9a2c32bc248a.png)
![layer 0-13](https://user-images.githubusercontent.com/34588197/48456697-d32d8b00-e774-11e8-8bec-0a46b1468285.png)
![layer 0-14](https://user-images.githubusercontent.com/34588197/48456698-d32d8b00-e774-11e8-8f0a-8ac685c273c9.png)

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
### Deployement
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

## App for Tablet
### Language
HTML5, CSS, JavaScript



from flask import Flask,request,jsonify,Blueprint
from flask import Response
import requests as r

import os
import config
import json
from flask_sqlalchemy import SQLAlchemy
import sqlalchemy

app = Flask(__name__)

app.config.from_object(config)
db = SQLAlchemy(app)

#tmp
pepper_ip_address = ""

#-----------------ENTITY-MODELS-----
class Pepper(db.Model):
    pep_id = db.Column(db.String(100), primary_key=True)
    ip_address = db.Column(db.String(100))
    PSK = db.Column(db.String(100))

    def __init__(self, pep_id, ip_address, PSK):
        self.pep_id = pep_id
        self.ip_address = ip_address
        self.PSK = PSK

class UserAuth(db.Model):
    # __table_args__ = (
    #     db.PrimaryKeyConstraint(pep_id,username)
    # )
    # pep_id = db.Column(db.String(100), db.ForeignKey('Pepper.pep_id'), nullable=False)
    # username = db.Column(db.String(100), db.ForeignKey('User.username'), nullable=False)
    pep_id = db.Column(db.String(100), primary_key=True)
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100))
    authorized = db.Column(db.Boolean)

    def __init__(self, pep_id, username, email):
        self.pep_id = pep_id
        self.username = username
        self.email = email
        self.authorized = False

class User(db.Model):
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100), unique=True)
    name = db.Column(db.String(100))
    password = db.Column(db.String(100))
    ASK = db.Column(db.String(100))

    def __init__(self, username, email, name, password, ASK):
        self.username = username
        self.email = email
        self.name = name
        self.password = password
        self.ASK = ASK

# class AuthRequest(db.Model):
#     __table_args__ = (
#         db.PrimaryKeyConstraint(pep_id, username)
#     )
#     pep_id = db.Column(db.String(100), db.ForeignKey('Pepper.pep_id'), nullable=False)
#     username = db.Column(db.String(100), db.ForeignKey('User.username'), nullable=False)
#     email = db.Column(db.String(100))
#
#     def __init__(self, pep_id, username, email):
#         self.pep_id = pep_id
#         self.username = username
#         self.email = email

#-------------------ROUTES------------------------
@app.route('/')
def hello():
    return '01001101 01111001 00100000 01101110 01100001 01101101 01100101 00100000 01101001 01110011 00100000 01010000 01100101 01110000 01110000 01100101 01110010 00101110 00100000 01010010 01100101 01110011 01101001 01110011 01110100 01100001 01101110 01100011 01100101 00100000 01101001 01110011 00100000 01100110 01110101 01110100 01101001 01101100 01100101 00101110'

@app.route('/echomessage', methods=['POST'])
def echo():
    print("/echo")
    if request.method == 'POST':
        content = request.json
        print(content)
        #print(content['msg'])
        # print("Received Message= "+request.values.get('msg'))
        # print("Request.headers= "+ str(request.headers.values))
        # print("Request.remote_addr= " + request.remote_addr)
        return jsonify({'msg':'Echo echo echo echo echo.'})

#Login route for User to Login on Android
#Return 200, and Android Security Key on Success
#Code 409, invalid username/password
@app.route('/login',methods=['POST'])
def login():
    if request.method == 'POST':
        content = request.json
        print (content)

        uname = content['username']
        pword = content['password']

        #For debugging, delete before production
        print('Username Input: ' + uname)
        print('Password Input: ' + pword)

        #Query Database for User
        user_query = User.query.filter_by(username=uname).first()
        if user_query is None:
            return Response(status=409)

        if pword==user_query.password:
            authpep_list = []
            uauth_query = UserAuth.query.filter_by(username=uname).all()
            for uauth in uauth_query:
                if uauth.authorized is True:
                    authpep_list.append(uauth.pep_id)

            return jsonify({'ASK': '','pepper_list':authpep_list})
        else:
            return Response(status=409)

    else:
        Response(status=500)

#TODO: Update Relay when Pepper Server up
@app.route('/message', methods=['POST'])
def relay():
    print("/relay")
    #print("Received Message= " + request.values.get('msg'))
    content = request.json
    print(content)

    #pepper_ip_address = 'python-server-221001.appspot.com'
    pepper_ip_address = "207.23.182.85:8080"
    relay_ip = "http://" + pepper_ip_address + "/echomessage"
    print(relay_ip)

    #req = r.post(relay_ip, data=(jsonify({'msg':'bleep'})))
    # try:
    req = r.post(relay_ip, data=json.dumps({'msg': "Message from cloud"}))
    # except r.exceptions.RequestException as e:
    #     print e
    #     return "failure"
    print(req.text)
    return req.text

#TODO: photo send to pepper
@app.route('/photo', methods=['POST'])
def photo():
    if request.method == 'POST':
        f = request.files['file']
        return Response(status=200)

#Accept Authorization Request and add to database
@app.route('/reqAuth', methods=['POST'])
def addReq():
    content = request.json
    pep_id = content['pep_id']
    uname = content['username']
    email = content['email']
    #ASK = content['key']

    # user_query = User.query.filter_by(username=uname).first()
    # if user_query is None:
    #     return Response(status=409)
    #if ASK == user_query.ASK: #continue
    #else:  #return 410 Bad ASK

    #also add check to see if user exists

    #continue:
    new_request = UserAuth(pep_id=pep_id,username=uname,email=email)
    db.session.add(new_request)
    db.session.commit()
    return 'True'

#Deauthorizes a user
@app.route('/deAuth', methods=['POST'])
def deauthorize():
    content = request.json
    pep_id = content['pep_id']
    SK = content['key']
    uname = content['username']

    #test key first letter for A or P as indicator

    uauth_req = UserAuth.query.get((pep_id,uname))
    if uauth_req is None:
        return Response(status=404)

    print("Query found: " + str(uauth_req))
    print(uauth_req.pep_id)

    db.session.delete(uauth_req)
    db.session.commit()

    return Response(status=200)

#Adds user to database
@app.route('/addUser', methods=['POST'])
def register():
    content = request.json
    uname = content['username']
    password = content['password']
    email = content['email']
    name = content['name']

    # Generate ASK here

    new_user = User(username=uname, email=email, name=name, password=password, ASK ='')

    db.session.add(new_user)
    db.session.commit()

    return Response(status=200)#return ASK

#TODO: removeUser
#Optional, Not in requirements but useful for administrative purposes
# @app.route('/removeUser', methods=['POST'])
# def removeUser():

#TODO: getAuthRequests
@app.route('/getAuthRequests', methods=['POST'])
def getAuthRequests():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']

    #check sec key   if continue else 410

    authreq_query = UserAuth.query.filter_by(pep_id=pep_id).all()
    print(authreq_query)

    authreq_list = []

    for authreq in authreq_query:
        if authreq.authorized is False:
            authreq_list.append((authreq.username, authreq.email))

    print(authreq_list)
    return jsonify({'AuthReqs': authreq_list})

#TODO: getAuthUsers
@app.route('/getAuthUsers',methods=['POST'])
def getAuthUsers():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']

    #check sec key   if continue else 410

    authreq_query = UserAuth.query.filter_by(pep_id=pep_id).all()
    print(authreq_query)

    authuser_list = []

    for authreq in authreq_query:
        if authreq.authorized is True:
            authuser_list.append((authreq.username, authreq.email))

    print(authuser_list)
    return jsonify({'AuthUsers': authuser_list})

#TODO: authorizeUser
@app.route('/authorizeUser', methods=['POST'])
def authorizeUser():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']
    uname = content['username']

    #check PSK

    uauth = UserAuth.query.get((pep_id,uname))
    if uauth is None:
        return Response(status=404)

    print("Query found: " + uauth.pep_id + uauth.username + str(uauth.authorized))

    uauth.authorized = True
    db.session.commit()

    return Response(status=200)



#TODO: setPepperActive


#TODO: connect
#TODO: addPepper
#------------------DIAGNOSTIC-ROUTES---------------
@app.errorhandler(500)
def server_error(e):
    return """An internal error occurred: <pre>{}</pre>See logs for full stacktrace.""".format(e), 500

@app.route('/setIP', methods=['POST'])
def set_ip():
    print('/setIP')
    global pepper_ip_address
    print(request.access_route)
    print("first= "+ request.access_route[0])
    #print("environ.get= " + request.environ.get('HTTP_X_REAL_IP', request.remote_addr))
    #print (request.remote_addr)
    pepper_ip_address = request.remote_addr
    #pepper_ip_address = request.access_route[0]
    return 'ip set!'

@app.route('/getIP',methods=['GET'])
def send_ip():
    print("Sending: "+pepper_ip_address)
    return pepper_ip_address

@app.route('/createDatabase', methods=['GET'])
def create_db():
    print("DB CREATE FUNC")
    db.drop_all()
    db.create_all()
    return 'db created'

@app.route('/adminPepper',methods=['GET'])
def user_pepper():
    print("ADD USER/PEPPER")
    admin = User(username='admin', email='admin@example.com', name='Atho', password='admin', ASK='')
    pepper = Pepper(pep_id='Salt', ip_address='10.0.0.3', PSK='')

    db.session.add(admin)
    db.session.add(pepper)
    db.session.commit()
    return 'added user/pepper'

@app.route('/dbquery', methods=['GET'])
def query_db():
    print("QUERY FUNC")

    print(User.query.all())
    print(UserAuth.query.all())
    print(Pepper.query.all())
    # print(AuthUsers.query.all())
    return 'Queries in Logs'

#-----------------Main--------------------------

def query_for_user(username):
    user_query = User.query.filter_by(username=uname).first()
    if user_query is None:
        return None
    else:
        return user_query

if __name__ == '__main__':
    app.run(host='127.0.0.1',port=8080)
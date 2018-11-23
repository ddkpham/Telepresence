from flask import Flask, request, jsonify, Blueprint, Response
from flask_sqlalchemy import SQLAlchemy
import os, config, json
import sqlalchemy
import requests as r
import random, string
from werkzeug.security import generate_password_hash, check_password_hash

import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging

app = Flask(__name__)

app.config.from_object(config)
db = SQLAlchemy(app)

# TODO: Delete this with setIP, getIP
pepper_ip_address = ""

# F = open('C:/Users/Atho/Desktop/python-server-221001-firebase-adminsdk-zfrb5-8ee102d34a.json','r')
# print F.read()

cred = credentials.Certificate('C:/Users/antho/Desktop/python-server-221001-firebase-adminsdk-zfrb5-8ee102d34a.json')
default_app = firebase_admin.initialize_app(cred)
# default_app = firebase_admin.initialize_app()

reg_token = 'eMT8G9Cw1mw:APA91bF7U_TJPvDtwz3FN78itXRTf96P0BwR4QZh6yEOh0F17SdhTgHeltfbKA_v2pEq75OvTMu_y9SneIHyU6nXoO-2e8G8FGcT9cAmKVD3E09qDrYOq06YvNUE7R3_8TdggZ5jgBox'

notif = messaging.Notification("Title", "Hooba hooba")
message = messaging.Message(
    data={
        "blank": "hello",

    },
    notification=notif,
    token=reg_token,
)

response = messaging.send(message)
print ("Fire Response: ", response)


# -----------------ENTITY-MODELS-----
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


# -------------------ROUTES------------------------

# Login route for User to Login on Android
# Return 200, and Android Security Key on Success
# Code 409, invalid username/password
@app.route('/login', methods=['POST'])
def login():
    content = request.json
    print (content)

    uname = content['username']
    pword = content['password']

    # For debugging, delete before production
    print('Username Input: ' + uname)
    print('Password Input: ' + pword)

    # Query Database for User
    user_query = User.query.filter_by(username=uname).first()
    if user_query is None:
        return Response(status=409)

    if pword == user_query.password:

        # Gen ASK and update Database
        ASK = generate_random_string()
        user_query.ASK = ASK
        db.session.commit()

        authpep_list = []
        uauth_query = UserAuth.query.filter_by(username=uname).all()
        for uauth in uauth_query:
            if uauth.authorized is True:
                authpep_list.append(uauth.pep_id)

        req_list = []
        req_query = UserAuth.query.filter_by(username=uname).all()
        for req in req_query:
            if req.authorized is False:
                req_list.append(req.pep_id)

        # Hash ASK
        hashed_ASK = generate_password_hash(ASK)
        return jsonify(
            {'ASK': hashed_ASK, 'pepper_list': authpep_list, 'request_list': req_list, 'email': user_query.email})
    else:
        return Response(status=409)


# TODO: Test Relay with Pepper Server
@app.route('/message', methods=['POST'])
def message():
    print("/Message")

    content = request.json
    username = content['username']
    pep_id = content['pep_id']
    ASK = content['ASK']
    message = content['message']

    # Check ASK
    if check_sk('A', ASK, username) is False:
        return Response(status=410)

    # Check Authorization
    uauth_req = UserAuth.query.get((pep_id, username))
    if uauth_req is None:
        return Response(status=410)

    # Get Pepper Entity
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=404)

    relay_ip = "http://" + pepper.ip_address + ":8080/message"
    print("Relay ip: " + relay_ip)

    # try:
    req = r.post(relay_ip, data=json.dumps({'msg': message}))
    # except r.exceptions.RequestException as e:
    #     print e
    #     return "failure"

    print(req.text)
    if req.status_code == 200:
        return req.text
    else:
        return Response(status=req.status_code)


# TODO:Test photo when Pepper Server Up
@app.route('/photo', methods=['POST'])
def photo():
    content = request.form
    username = content['username']
    pep_id = content['pep_id']
    ASK = content['ASK']

    photo = request.files['file']

    # Check ASK
    if check_sk('A', ASK, username) is False:
        return Response(status=410)

    # Check Authorization
    uauth_req = UserAuth.query.get((pep_id, username))
    if uauth_req is None:
        return Response(status=410)

    # Get Pepper Entity
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=404)

    relay_ip = "http://" + pepper.ip_address + "/message"
    print("Relay ip: " + relay_ip)

    req = r.post(relay_ip, files=photo)

    return Response(status=req.status_code)


# Accept Authorization Request and add to database
@app.route('/reqAuth', methods=['POST'])
def request_auth():
    content = request.json
    pep_id = content['pep_id']
    uname = content['username']
    email = content['email']
    ASK = content['ASK']

    # Check ASK
    if check_sk('A', ASK, uname) is False:
        return Response(status=410)

    # Check Pepper Exists
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=404)

    # continue:
    new_request = UserAuth(pep_id=pep_id, username=uname, email=email)
    db.session.add(new_request)
    db.session.commit()
    return Response(status=200)


# Deauthorizes a user
@app.route('/deAuth', methods=['POST'])
def deauthorize():
    content = request.json
    pep_id = content['pep_id']
    ASK = content['ASK']
    PSK = content['PSK']
    uname = content['username']

    # One of ASK or PSK is blank, NOT blank one is the sender

    if PSK == '':
        # Check ASK
        if check_sk('A', ASK, uname) is False:
            return Response(status=410)
    # else:
    # Check PSK

    uauth_req = UserAuth.query.get((pep_id, uname))
    if uauth_req is None:
        return Response(status=404)

    print("Query found: " + str(uauth_req))
    print(uauth_req.pep_id)

    db.session.delete(uauth_req)
    db.session.commit()

    return Response(status=200)


# Adds user to database
@app.route('/addUser', methods=['POST'])
def addUser():
    content = request.json
    uname = content['username']
    password = content['password']
    email = content['email']
    name = content['name']

    # Generate ASK
    ASK = generate_random_string()

    user_query = User.query.filter_by(username=uname).first()
    if user_query is not None:
        return Response(jsonify({'Error:': 'Username already used.'}), status=409)
    user_query = User.query.filter_by(email=email).first()
    if user_query is not None:
        return Response(jsonify({'Error:': 'Email already used.'}), status=409)

    new_user = User(username=uname, email=email, name=name, password=password, ASK=ASK)

    db.session.add(new_user)
    db.session.commit()

    hashed_ASK = generate_password_hash(ASK)
    return jsonify({'ASK': hashed_ASK})


@app.route('/getAuthRequests', methods=['POST'])
def getAuthRequests():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']

    # Check PSK

    authreq_query = UserAuth.query.filter_by(pep_id=pep_id).all()
    print(authreq_query)

    authreq_list = []

    for authreq in authreq_query:
        if authreq.authorized is False:
            authreq_list.append((authreq.username, authreq.email))

    print(authreq_list)
    return jsonify({'AuthReqs': authreq_list})


@app.route('/getAuthUsers', methods=['POST'])
def getAuthUsers():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']

    # check PSK  if continue else 410

    authreq_query = UserAuth.query.filter_by(pep_id=pep_id).all()
    print(authreq_query)

    authuser_list = []

    for authreq in authreq_query:
        if authreq.authorized is True:
            authuser_list.append((authreq.username, authreq.email))

    print(authuser_list)
    return jsonify({'AuthUsers': authuser_list})


@app.route('/authorizeUser', methods=['POST'])
def authorizeUser():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']
    uname = content['username']

    # check PSK

    uauth = UserAuth.query.get((pep_id, uname))
    if uauth is None:
        return Response(status=404)

    print("Query found: " + uauth.pep_id + uauth.username + str(uauth.authorized))

    uauth.authorized = True
    db.session.commit()

    return Response(status=200)


# TODO:Test setPepperActive again when Pepper Server Online
@app.route('/setPepperActive', methods=['POST'])
def setPepperActive():
    content = request.json
    pep_id = content['pep_id']
    # ip_address = content['ip_address']
    ip = request.access_route[0]
    PSKs = ['PSK']

    # Check PSK

    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=404)
        # new_pepper = Pepper(pep_id=pep_id, ip_address=ip, PSK='')
        # db.session.add(new_pepper)
        # db.session.commit()
        # return Response(status=200)
    else:
        pepper.ip_address = ip
        db.session.commit()
    return Response(status=200)


# TODO:Test addPepper again with Pepper Server
@app.route('/addPepper', methods=['POST'])
def addPepper():
    content = request.json
    pep_id = content['pep_id']
    PSK = content['PSK']
    ip = request.access_route[0]

    # Check PSK

    new_pepper = Pepper(pep_id=pep_id, ip_address=ip, PSK='')
    db.session.add(new_pepper)
    db.session.commit()
    return Response(status=200)


# TODO: Test with Pepper Server
# Login for Pepper Tablet
@app.route('/pepperLogin', methods=['POST'])
def pepperLogin():
    content = request.json
    print (content)

    pep_id = content['pep_id']
    uname = content['username']
    pword = content['password']

    # For debugging, delete before production
    print('Username Input: ' + uname)
    print('Password Input: ' + pword)

    # Query Database for User
    user_query = User.query.filter_by(username=uname).first()
    if user_query is None:
        return Response(status=409)

    if pword == user_query.password:
        authpep_list = []
        uauth_query = UserAuth.query.filter_by(username=uname).all()
        for uauth in uauth_query:
            if uauth.pep_id == pep_id:
                return Response(status=200)
        return Response(status=410)
    else:
        return Response(status=409)


# TODO: removeUser  #Optional, Not in requirements but useful for administrative purposes
# @app.route('/removeUser', methods=['POST'])
# def removeUser():

@app.errorhandler(500)
def server_error(e):
    return """An internal error occurred: <pre>{}</pre>See logs for full stacktrace.""".format(e), 500


# -------------------GAME-ROUTES--------------------

@app.route('/startgame', methods=['POST'])
def start_game():
    # send to firebase

    # send object then check for error
    return 200
    # else error
    # return not 200


@app.route('/endgame', methods=['POST'])
def end_game():
    # send to firebase

    # send object then check for error
    return 200
    # else error
    # return not 200


# ------------------DIAGNOSTIC-ROUTES---------------
@app.route('/setIP', methods=['POST'])
def set_ip():
    print('/setIP')
    global pepper_ip_address
    print(request.access_route)
    print("first= " + request.access_route[0])
    # print("environ.get= " + request.environ.get('HTTP_X_REAL_IP', request.remote_addr))
    # print (request.remote_addr)
    pepper_ip_address = request.remote_addr
    # pepper_ip_address = request.access_route[0]
    return 'ip set!'


@app.route('/sendToIP', methods=['GET'])
def get_IP():
    print("Sending to: " + pepper_ip_address + ":8080")
    req = r.post("http://" + pepper_ip_address + ":8080/echomessage")
    return pepper_ip_address


@app.route('/adminPepper', methods=['GET'])
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


# TODO: Disable or add authentication
@app.route('/wipeDatabase', methods=['GET'])
def wipe_db():
    print("DB CREATE FUNC")
    db.drop_all()
    db.create_all()
    return 'db created'


# TODO: ONLY use this route for debugging, disable or add authentication in production environment
@app.route('/showDB', methods=['GET'])
def showDB():
    users = User.query.all()
    uauths = UserAuth.query.all()
    peppers = Pepper.query.all()

    result = '<h3> Users: </h3><br>'

    for user in users:
        result = result + 'Username: ' + user.username + '| Email: ' + user.email + '| Name: ' + user.name + '| Password: ' + user.password + '<br>'
    result += '<h3> UserAuths: </h3><br>'
    for uauth in uauths:
        result = result + 'pep_id: ' + uauth.pep_id + '| username: ' + uauth.username + '| email: ' + uauth.email + '| Authorized: ' + str(
            uauth.authorized) + '<br>'
    result += '<h3> Peppers: </h3><br>'
    for pepper in peppers:
        result = result + 'pep_id: ' + pepper.pep_id + '| ip_address: ' + pepper.ip_address + '<br>'

    return result


@app.route('/')
def Telepresence():
    return '01001101 01111001 00100000 01101110 01100001 01101101 01100101 00100000 01101001 01110011 00100000 01010000 01100101 01110000 01110000 01100101 01110010 00101110 00100000 01010010 01100101 01110011 01101001 01110011 01110100 01100001 01101110 01100011 01100101 00100000 01101001 01110011 00100000 01100110 01110101 01110100 01101001 01101100 01100101 00101110'


@app.route('/echomessage', methods=['POST'])
def echo():
    print("/echo")

    content = request.json
    print(content)
    # print(content['msg'])
    # print("Received Message= "+request.values.get('msg'))
    # print("Request.headers= "+ str(request.headers.values))
    # print("Request.remote_addr= " + request.remote_addr)
    return jsonify({'msg': 'Echo echo echo echo echo.'})


# -----------------Functions--------------------------

# check_sk usage:
# Pepper:
# if check_sk('P',PSK,pep_id) is False:
#     return Response(status=410)

# Example Hash and Check:
# print("Password: asdf")
# abs = generate_password_hash("asdf")
# print(abs)
# bbs = generate_password_hash("asdf")
# print(bbs)
# print(check_password_hash(abs,"asdf"))
# print(check_password_hash(abs,bbs))

def check_sk(type, key, id):
    if type == 'P':
        query = Pepper.query.filter_by(pep_id=id).first()
        if query is None:
            return False
        cloud_key = query.PSK
    elif type == 'A':
        query = User.query.filter_by(username=id).first()
        if query is None:
            return False
        cloud_key = query.ASK
    else:
        return False

    # if key == cloud_key:
    #     return True
    # else:
    #     return False
    return check_password_hash(key, cloud_key)


def generate_random_string():
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(15))


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080)

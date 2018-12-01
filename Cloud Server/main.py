from flask import Flask, request, jsonify, Response
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

import hashlib, binascii
import base64
import requests as r

import os, config, json, random, string

import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging


app = Flask(__name__)

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config.from_object(config)
db = SQLAlchemy(app)

#cred = credentials.Certificate('<path for local credentials>')
#default_app = firebase_admin.initialize_app(cred)
default_app = firebase_admin.initialize_app()

#For local instance
# local_ip = 'http://10.0.0.3:8165'

# -------------------MODELS------------------------


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

    def __init__(self, pep_id, username, email, authorized):
        self.pep_id = pep_id
        self.username = username
        self.email = email
        self.authorized = authorized


class User(db.Model):
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100), unique=True)
    name = db.Column(db.String(100))
    password = db.Column(db.String(100))
    ASK = db.Column(db.String(100))
    FBToken = db.Column(db.String(200))

    def __init__(self, username, email, name, password, ASK, FBToken):
        self.username = username
        self.email = email
        self.name = name
        self.password = password
        self.ASK = ASK
        self.FBToken = FBToken

# -------------------ROUTES------------------------

#Login Route for Pepper Tablet and Android
def login():
    try:
        content = request.json
        username = content['username']
        password = content['password']
    except:
        print ("Missing Data")
        return Response(status=400)

    #Check for FBToken
    if 'FBToken' in content:
        FBToken = content['FBToken']
    else:
        FBToken = False

    if request.path == '/pepperLogin':
        try:
            pep_id = content['pep_id']
        except:
            print ("Missing Data")
            return Response(status=400)
        pLogin = True
    else:
        pLogin = False

    # Query Database for User
    user_query = User.query.filter_by(username=username).first()
    if user_query is None:
        print('User not found in Database')
        return Response(status=409)

    #Check if password matches
    if check_password_hash(user_query.password, password):
        authpep_list = []
        uauth_query = UserAuth.query.filter_by(username=username).all()
        for uauth in uauth_query:
            if uauth.authorized is True:
                if pLogin:
                    if uauth.pep_id == pep_id:
                        return Response(status=200)
                else:
                    authpep_list.append(uauth.pep_id)

        if pLogin:
            return Response(status=403)

        #Update FBToken
        user_query.FBToken = FBToken

        # Gen ASK and update Database
        ASK = generate_random_string()
        hashed_ASK = hash_ASK(ASK)
        user_query.ASK = hashed_ASK
        db.session.commit()

        req_list = []
        req_query = UserAuth.query.filter_by(username=username).all()
        for req in req_query:
            if req.authorized is False:
                req_list.append(req.pep_id)

        return jsonify(
            {'ASK': hashed_ASK, 'pepper_list': authpep_list, 'request_list': req_list, 'email': user_query.email})
    else:
        return Response(status=401)


app.add_url_rule('/login', 'Login', login, methods=['POST'])
app.add_url_rule('/pepperLogin', 'pLogin', login, methods=['POST'])


#Used to relay a message and response between Pepper and Android
@app.route('/message', methods=['POST'])
def message():
    print("/Message")

    try:
        content = request.json
        username = content['username']
        pep_id = content['pep_id']
        ASK = content['ASK']
        message = content['message']
    except:
        print ("Missing Data")
        return Response(status=400)

    print(content)

    # Check ASK
    check = check_ASK(ASK, username)
    # print (check)
    if check is None:
        return Response(status=403)
    elif check is False:
        return Response(status=403)

    # Check Authorization
    uauth_query = UserAuth.query.get((pep_id, username))
    if uauth_query is None:
        return Response(status=401)
    else:
        if not uauth_query.authorized:
            return Response(status=401)

    # Get Pepper Entity
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=409)

    #Check if Pepper Active
    if pepper.ip_address == '':
        return Response(status=410)

    relay_ip = "http://" + pepper.ip_address + ":8080/message"
    print("Sending message to: " + relay_ip)

    #Hash PSK from database
    new_PSK = hash_PSK(pepper.PSK)

    try:
        #Send request to Pepper
        req = r.post(relay_ip, data=json.dumps({'msg': message,'PSK':new_PSK,'username':username}))

        #For local instance
        # req = r.post(local_ip + '/message', data=json.dumps({'msg': message,'PSK':new_PSK,'username':username}))
    except r.exceptions.ConnectionError as error:
        #Set database Pepper as "not active"
        print('Error: ' + str(error))
        pepper.ip_address = ''
        db.session.commit()
        return Response(status=410)

    print('test')

    print(req.text)
    if req.status_code == 200:
        #Update PSK in database
        pepper.PSK = new_PSK
        db.session.commit()
        return req.text
    else:
        return Response(status=req.status_code)


#Relays photo from Sender to Pepper
@app.route('/photo', methods=['POST'])
def photo():
    try:
        content = request.form
        username = content['username']
        pep_id = content['pep_id']
        ASK = content['ASK']
        photo = request.files['file']
    except:
        print ("Missing Data")
        return Response(status=400)

    print(content)
    print('Photo: ')
    print(photo)

    temp = photo.read()
    encoded_photo = temp.encode('base64')

    # Check ASK
    check = check_ASK(ASK, username)
    print (check)
    if check is None:
        return Response(status=403)
    elif check is False:
        return Response(status=403)

    # Check Authorization
    uauth_query = UserAuth.query.get((pep_id, username))
    if uauth_query is None:
        return Response(status=401)
    else:
        if not uauth_query.authorized:
            return Response(status=401)

    # Get Pepper Entity
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=409)

    # Check if Pepper Active
    if pepper.ip_address == '':
        return Response(status=410)

    relay_ip = "http://" + pepper.ip_address + ":8080/photo"
    print("Sending photo to: " + relay_ip)

    new_PSK = hash_PSK(pepper.PSK)
    try:
        # req = r.post(relay_ip, files=photo)
        req = r.post(relay_ip, json={'PSK': new_PSK, 'photo': encoded_photo})
        req = r.post(local_ip + '/photo', json={'PSK':new_PSK, 'photo':encoded_photo})
    except r.exceptions.ConnectionError:
        # pepper.ip_address = ''
        # db.session.commit()
        return Response(status=410)

    # pepper.PSK = new_PSK
    # db.session.commit()
    return Response(status=req.status_code)


# Accept Authorization Request and add to database
@app.route('/reqAuth', methods=['POST'])
def request_auth():
    try:
        content = request.json
        pep_id = content['pep_id']
        username = content['username']
        email = content['email']
        ASK = content['ASK']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check ASK
    check = check_ASK(ASK, username)
    print (check)
    if check is None:
        return Response(status=403)
    elif check is False:
        return Response(status=403)

    # Check Pepper Exists
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=409)

    # continue:
    new_request = UserAuth(pep_id=pep_id, username=username, email=email, authorized=False)
    db.session.add(new_request)
    db.session.commit()
    return Response(status=200)


# Deauthorizes a user
@app.route('/deAuth', methods=['POST'])
def deauthorize():
    try:
        content = request.json
        pep_id = content['pep_id']
        PSK = content['PSK']
        username = content['username']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check PSK
    check = check_PSK(PSK, pep_id)
    if check is None:
        return Response(status=409)
    elif check is False:
        return Response(status=403)

    uauth_req = UserAuth.query.get((pep_id, username))
    if uauth_req is None:
        return Response(status=409)

    print("Query found: " + str(uauth_req))
    print(uauth_req.pep_id)

    db.session.delete(uauth_req)
    db.session.commit()

    return Response(status=200)


# Adds user to database
@app.route('/addUser', methods=['POST'])
def addUser():
    try:
        content = request.json
        username = content['username']
        password = content['password']
        email = content['email']
        name = content['name']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Generate ASK
    ASK = generate_random_string()

    user_query = User.query.filter_by(username=username).first()
    if user_query is not None:
        # resp = jsonify({'Error:': 'Username already used.'})
        # resp.status_code = 409
        return Response(status=409)

    user_query = User.query.filter_by(email=email).first()
    if user_query is not None:
        # resp = jsonify({'Error:': 'Email already used.'})
        # resp.status_code = 409
        return Response(status=412)

    hash_pw = generate_password_hash(password)
    new_user = User(username=username, email=email, name=name, password=hash_pw, ASK=ASK, FBToken='')

    db.session.add(new_user)
    db.session.commit()

    hashed_ASK = hash_ASK(ASK)
    return jsonify({'ASK': hashed_ASK})


@app.route('/getAuthRequests', methods=['POST'])
def getAuthRequests():
    try:
        content = request.json
        pep_id = content['pep_id']
        PSK = content['PSK']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check PSK
    check = check_PSK(PSK, pep_id)
    if check is None:
        return Response(status=409)
    elif check is False:
        return Response(status=403)


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
    try:
        content = request.json
        pep_id = content['pep_id']
        PSK = content['PSK']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check PSK
    check = check_PSK(PSK, pep_id)
    if check is None:
        return Response(status=409)
    elif check is False:
        return Response(status=403)

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
    try:
        content = request.json
        pep_id = content['pep_id']
        PSK = content['PSK']
        username = content['username']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check PSK
    check = check_PSK( PSK, pep_id)
    if check is None:
        return Response(status=409)
    elif check is False:
        return Response(status=403)

    uauth = UserAuth.query.get((pep_id, username))
    if uauth is None:
        return Response(status=409)

    # print("Query found: " + uauth.pep_id + uauth.username + str(uauth.authorized))

    uauth.authorized = True
    db.session.commit()

    return Response(status=200)


@app.route('/addPepper', methods=['POST'])
def add_update_Pepper():
    try:
        content = request.json
        pep_id = content['pep_id']
        ip = request.access_route[0]
        PSK = content['PSK']
        username = content['username']
    except:
        print ("Missing Data")
        return Response(status=400)

    print (content)

    #Check if user exists
    if username != '':
        user_query = User.query.filter_by(username=username).first()
        if user_query is None:
            return Response(status=410)

    #Check for existing Pepper entity with pep_id
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        if username == '':
            #Pepper does not exist in database but Pepper App assumed it was
            return Response(status=410)

        #Add new pepper
        new_pepper = Pepper(pep_id=pep_id, ip_address=ip, PSK=PSK)
        db.session.add(new_pepper)

        #Make sure database is clean to guarantee correct authorization
        uauth_query = UserAuth.query.get((pep_id, username))
        while uauth_query is not None:
            db.session.delete(uauth_query)
            uauth_query = UserAuth.query.get((pep_id, username))

        #Add new authorized user
        new_userAuth = UserAuth(pep_id=pep_id, username=username, email=user_query.email, authorized=True)
        db.session.add(new_userAuth)

        db.session.commit()
        return Response(status=200)
    else:
        #When pep_id is already in use
        if username != '':
            return Response(status=409)

        #Pepper already exists so update info
        pepper.ip_address = ip
        pepper.PSK = PSK
        db.session.commit()

        #Get all userAuth entities for pep_id
        uauth_query = UserAuth.query.filter_by(pep_id=pep_id).all()

        #Make sure at least one authorized user
        # for uauth in uauth_query:
        #     if uauth.authorized:
        #         return Response(status=200)

        return Response(status=200)

@app.route('/proactive', methods=['POST'])
def proactive():
    try:
        content = request.json
        message = content['msg']
        PSK = content.pop('PSK')
        username = content.pop('android_username')
        pep_id = content.pop('pep_id')
    except:
        print ("Missing Data")
        return Response(status=400)

    print('Content: ' + str(content))

    #add path to content
    content.update({'path': request.path[1:]})

    #Check PSK
    check = check_PSK(PSK, pep_id)
    if check is None:
        return Response(status=409)
    elif check is False:
        return Response(status=403)

    #Find User from Database
    user_query = User.query.filter_by(username=username).first()
    if user_query is None:
        return Response(status=409)

    notif = messaging.Notification("Pepper Alert!", message)

    fb_message = messaging.Message(
        data=content,
        notification=notif,
        token=user_query.FBToken,
    )

    try:
        response = messaging.send(fb_message)
    except:
        print ("Message failed to send.")
        return Response(status=410)
    return Response(status=200)


@app.errorhandler(500)
def server_error(e):
    return """An internal error occurred: <pre>{}</pre>See logs for full stacktrace.""".format(e), 500


# --------------ANDROID-GAME-ROUTES-----------------

def relay_to_pepper():
    try:
        content = request.json
        pep_id = content.pop('pep_id')
    except:
        print ("Missing Data")
        return Response(status=400)
    print (pep_id)
    print (content)

    if request.path == '/startgame':
        #Update FBToken for User in Database
        try:
            username = content['android_username']
            FBToken = content.pop('FBToken')
        except:
            print ("Missing Data")
            return Response(status=400)

        user_query = User.query.filter_by(username=username).first()
        if user_query is None:
            return Response(status=409)

        user_query.FBToken = FBToken
        db.session.commit()

    print('Content: ' + str(content))

    #Get IP Address from Database
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=406)

    # Check if Pepper is Active
    if pepper.ip_address == '':
        return Response(status=410)

    relay_ip = "http://" + pepper.ip_address + ":8080"
    print("Relay ip: " + relay_ip)

    #Send to Pepper

    try:

        # req = r.post(local_ip + request.path, json=content)  # Local Instance
        req = r.post(relay_ip + request.path, json=content)
    except r.exceptions.ConnectionError:
        pepper.ip_address = ''
        db.session.commit()
        return Response(status=410)

    return Response(status=req.status_code)


app.add_url_rule('/startgame', 'Start', relay_to_pepper, methods=['POST'])
app.add_url_rule('/sendresults', 'Results', relay_to_pepper, methods=['POST'])
app.add_url_rule('/pepperanimation', 'PAnimation', relay_to_pepper, methods=['POST'])

# --------------PEPPER-GAME-ROUTES------------------


def relay_to_android():
    try:
        content = request.json
        username = content.pop('android_username')
    except:
        print ("Missing Data")
        return Response(status=400)

    print('Content: ' + str(content))
    print('Username: ' + username)

    content.update({'path': request.path[1:]})

    #Find User from Database
    user_query = User.query.filter_by(username=username).first()
    if user_query is None:
        return Response(status=409)

    fb_message = messaging.Message(
        data=content,
        token=user_query.FBToken,
    )
    try:
        response = messaging.send(fb_message)
    except:
        print ("Message failed to send.")
        return Response(status=410)
    return Response(status=200)


app.add_url_rule('/acceptgame', 'Accept', relay_to_android, methods=['POST'])
app.add_url_rule('/endgame', 'End', relay_to_android, methods=['POST'])
app.add_url_rule('/androidanimation', 'AAnimation', relay_to_android, methods=['POST'])
app.add_url_rule('/deny','Deny',relay_to_android, methods=['POST'])

# ------------------DIAGNOSTIC-ROUTES---------------


# test_ip = ""
#
#
# @app.route('/setIP', methods=['POST'])
# def set_ip():
#     print('/setIP')
#     global test_ip
#     print(request.access_route)
#     print("first= " + request.access_route[0])
#     test_ip = request.remote_addr
#
#     print ('Test IP = ' + test_ip)
#     return 'ip set!'
#
#
# @app.route('/sendToIP', methods=['GET'])
# def send_to_ip():
#     print("Sending to: " + test_ip + ":8080")
#     req = r.post("http://" + test_ip + ":8080/")
#     return req.text


#Used to remove a user from the database
# @app.route('/removeUser', methods=['POST'])
# def removeUser():
#     content = request.json
#     username = content['username']
#
#     user_query = User.query.filter_by(username=username).first()
#     if user_query is None:
#         return Response(status=409)
#     db.session.delete(user_query)
#     db.session.commit()
#     return Response(status=200)

#Wipe database then insert set of values into the database
# @app.route('/testDB', methods=['GET'])
# def create_TestSet():
#     print("Create Test DB")
#
#     # model_sqlalchemy.wipeDB()
#     db.drop_all()
#     db.create_all()
#
#     admin = User(username='admin', email='admin@example.com', name='Atho', password='admin', ASK='', FBToken='')
#     subin = User(username='subin', email='subin@example.com', name='S', password='subin', ASK='', FBToken='')
#     david = User(username='david', email='david@example.com', name='D', password='david', ASK='', FBToken='')
#     kass = User(username='kass', email='kass@example.com', name='K', password='kass', ASK='', FBToken='')
#
#     adminSalt = UserAuth(pep_id='salt', username='admin', email='admin@example.com', authorized=True)
#     subinSalt = UserAuth(pep_id='salt', username='subin', email='subin@example.com', authorized=True)
#     davidSalt = UserAuth(pep_id='salt', username='david', email='david@example.com', authorized=True)
#     kassSalt = UserAuth(pep_id='salt', username='kass', email='kass@example.com', authorized=True)
#
#     adminFraser = UserAuth(pep_id='fraser', username='admin', email='admin@example.com', authorized=False)
#     subinFraser = UserAuth(pep_id='fraser', username='subin', email='subin@example.com', authorized=False)
#     davidFraser = UserAuth(pep_id='fraser', username='david', email='david@example.com', authorized=False)
#     kassFraser = UserAuth(pep_id='fraser', username='kass', email='kass@example.com', authorized=False)
#
#
#     salt = Pepper(pep_id='Salt', ip_address='10.0.0.3', PSK='')
#     fraser = Pepper(pep_id='fraser', ip_address='10.0.0.4', PSK='')
#     simon = Pepper(pep_id='simon', ip_address='10.0.0.5', PSK='')
#     fan = Pepper(pep_id='fan', ip_address='10.0.0.6', PSK='')
#     window = Pepper(pep_id='window', ip_address='10.0.0.7', PSK='')
#
#     db.session.add_all([admin, subin, david, kass])
#     db.session.add_all([adminSalt, adminFraser, subinSalt, davidSalt, subinFraser, davidFraser, kassFraser, kassSalt])
#     db.session.add_all([salt, fraser, simon, fan, window])
#     db.session.commit()
#
#     return 'Database Wiped and replaced with Test Set'


#Wipes the Database
# @app.route('/wipeDatabase', methods=['GET'])
# def wipe_db():
#     # model_sqlalchemy.wipeDB()
#     db.drop_all()
#     db.create_all()
#     return 'DB Wiped'



#Show database, used HTML syntax for to make it easy to view in a browser
# @app.route('/showDB', methods=['GET'])
# def showDB():
#     users = User.query.all()
#     uauths = UserAuth.query.all()
#     peppers = Pepper.query.all()
#
#     result = '<h3> Users: </h3><br>'
#
#     for user in users:
#         result = result + 'Username: ' + user.username + '| Email: ' + user.email + '| Name: ' + user.name + '| Password: ' + user.password + '<br>|' + 'FBT: ' + user.FBToken + '<br>|' + 'ASK: ' + user.ASK + '<br><br>'
#     result += '<h3> UserAuths: </h3><br>'
#     for uauth in uauths:
#         result = result + 'pep_id: ' + uauth.pep_id + '| username: ' + uauth.username + '| email: ' + uauth.email + '| Authorized: ' + str(
#             uauth.authorized) + '<br><br>'
#     result += '<h3> Peppers: </h3><br>'
#     for pepper in peppers:
#         result = result + 'pep_id: ' + pepper.pep_id + '| ip_address: ' + pepper.ip_address + '<br>| PSK: '+ pepper.PSK + '<br><br>'
#
#     return result

#Home page with binary text to test Get requests
# def Telepresence():
#     return '01001101 01111001 00100000 01101110 01100001 01101101 01100101 00100000 01101001 01110011 00100000 01010000 01100101 01110000 01110000 01100101 01110010 00101110 00100000 01010010 01100101 01110011 01101001 01110011 01110100 01100001 01101110 01100011 01100101 00100000 01101001 01110011 00100000 01100110 01110101 01110100 01101001 01101100 01100101 00101110'
# app.add_url_rule('/', 'TP', Telepresence)

#Echo message to test Post requests
# @app.route('/echomessage', methods=['POST'])
# def echo():
#     print("/echo")
#
#     content = request.json
#     print(content)
#     # print(content['msg'])
#     # print("Received Message= "+request.values.get('msg'))
#     # print("Request.headers= "+ str(request.headers.values))
#     # print("Request.remote_addr= " + request.remote_addr)
#     return jsonify({'msg': 'Echo echo echo echo echo.'})


# -----------------Functions--------------------------


def check_PSK(key,id):
    #Find Pepper Entity
    query = Pepper.query.filter_by(username=id).first()
    if query is None:
        return None
    cloud_key = query.PSK

    # derived_key = hashlib.pbkdf2_hmac('sha256', cloud_key, 'HcU8jhcPFG', 1000)
    # result = binascii.hexlify(derived_key)

    # hash database PSK and check with input key
    next_cloud_key = hash_PSK(cloud_key)

    if next_cloud_key == key:
        #Update Database
        query.PSK = next_cloud_key
        db.session.commit()
        return True
    else:
        return False


def hash_PSK(key):
    salt = 'HcU8jhcPFG'
    key += salt
    result = hashlib.sha256(key).hexdigest()
    return result


def check_ASK(key, id):
    # Find Pepper Entity
    query = User.query.filter_by(username=id).first()
    if query is None:
        return None
    cloud_key = query.ASK

    next_cloud_key = hash_ASK(cloud_key)

    print(next_cloud_key)

    if next_cloud_key == key:
        # Update Database
        query.ASK = next_cloud_key
        db.session.commit()
        return True
    else:
        return False

def hash_ASK(key):
    salt = 'HcU8jhcPFG'
    key += salt
    key.encode('utf-8')
    result = hashlib.md5(key).hexdigest()
    return result


def generate_random_string():
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(15))


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)

from flask import Flask, request, jsonify, Response
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

import requests as r

import os, config, json, random, string

import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging

import model_sqlalchemy

User = model_sqlalchemy.User
Pepper = model_sqlalchemy.Pepper
UserAuth = model_sqlalchemy.UserAuth

app = Flask(__name__)

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config.from_object(config)
db = SQLAlchemy(app)

# TODO: Delete before Production
TEST_BOOL = True

#TODO: Delete before Production
cred = credentials.Certificate('C:/Users/Atho/Desktop/python-server-221001-firebase-adminsdk-zfrb5-8ee102d34a.json')
default_app = firebase_admin.initialize_app(cred)
#default_app = firebase_admin.initialize_app()

#TODO: Get rid of all instances of atho_token before Production
#My phone's FBToken
atho_token = 'eMT8G9Cw1mw:APA91bF7U_TJPvDtwz3FN78itXRTf96P0BwR4QZh6yEOh0F17SdhTgHeltfbKA_v2pEq75OvTMu_y9SneIHyU6nXoO-2e8G8FGcT9cAmKVD3E09qDrYOq06YvNUE7R3_8TdggZ5jgBox'

#TODO: Delete Firebase Send Tester before Production
#FIREBASE SEND TESTER
# notif = messaging.Notification("True Facts", "Bubbles")
# message = messaging.Message(
#     data={
#         "blank": "hello",
#
#     },
#     notification=notif,
#     token=atho_token,
# )
# response = messaging.send(message)
# print ("Fire Response: ", response)

# -------------------ROUTES------------------------

#Login Route for Pepper Tablet and Android
def login():
    try:
        content = request.json
        uname = content['username']
        pword = content['password']
    except:
        print ("Missing Data")
        return Response(status=400)

    if request.path == '/pepperLogin':
        pep_id = content['pep_id']
        pLogin = True
    else:
        pLogin = False

    # Query Database for User
    user_query = User.query.filter_by(username=uname).first()
    if user_query is None:
        return Response(status=409)

    if pword == user_query.password:
        authpep_list = []
        uauth_query = UserAuth.query.filter_by(username=uname).all()
        for uauth in uauth_query:
            if uauth.authorized is True:
                if pLogin:
                    if uauth.pep_id == pep_id:
                        return Response(status=200)
                else:
                    authpep_list.append(uauth.pep_id)

        if pLogin:
            return Response(status=410)

        # Gen ASK and update Database
        ASK = generate_random_string()
        user_query.ASK = ASK
        db.session.commit()

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

app.add_url_rule('/login', 'Login', login, methods=['POST'])
app.add_url_rule('/pepperLogin', 'pLogin', login, methods=['POST'])


# TODO: Test Relay with Pepper Server
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

    # Check ASK
    if check_sk(ASK, username) is False:
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

    if TEST_BOOL:
        return Response(status=200)

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
    try:
        content = request.form
        username = content['username']
        pep_id = content['pep_id']
        ASK = content['ASK']

        photo = request.files['file']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check ASK
    if check_sk(ASK, username) is False:
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

    if TEST_BOOL:
        return Response(status=200)

    req = r.post(relay_ip, files=photo)

    return Response(status=req.status_code)


# Accept Authorization Request and add to database
@app.route('/reqAuth', methods=['POST'])
def request_auth():
    try:
        content = request.json
        pep_id = content['pep_id']
        uname = content['username']
        email = content['email']
        ASK = content['ASK']
    except:
        print ("Missing Data")
        return Response(status=400)

    # Check ASK
    if check_sk(ASK, username) is False:
        return Response(status=410)

    # Check Pepper Exists
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=409)

    # continue:
    new_request = UserAuth(pep_id=pep_id, username=uname, email=email)
    db.session.add(new_request)
    db.session.commit()
    return Response(status=200)


# Deauthorizes a user
@app.route('/deAuth', methods=['POST'])
def deauthorize():
    try:
        content = request.json
        pep_id = content['pep_id']
        ASK = content['ASK']
        PSK = content['PSK']
        uname = content['username']
    except:
        print ("Missing Data")
        return Response(status=400)

    if check_sk(ASK, username) is False:
        return Response(status=410)

    uauth_req = UserAuth.query.get((pep_id, uname))
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
        uname = content['username']
        password = content['password']
        email = content['email']
        name = content['name']
    except:
        print ("Missing Data")
        return Response(status=400)
    # Generate ASK
    ASK = generate_random_string()

    user_query = User.query.filter_by(username=uname).first()
    if user_query is not None:
        resp = jsonify({'Error:': 'Username already used.'})
        resp.status_code = 409
        return resp
    user_query = User.query.filter_by(email=email).first()

    if user_query is not None:
        resp = jsonify({'Error:': 'Email already used.'})
        resp.status_code = 409
        return resp

    new_user = User(username=uname, email=email, name=name, password=password, ASK=ASK, FBToken='')

    db.session.add(new_user)
    db.session.commit()

    hashed_ASK = generate_password_hash(ASK)
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
        uname = content['username']
    except:
        print ("Missing Data")
        return Response(status=400)

    # check PSK

    uauth = UserAuth.query.get((pep_id, uname))
    if uauth is None:
        return Response(status=404)

    print("Query found: " + uauth.pep_id + uauth.username + str(uauth.authorized))

    uauth.authorized = True
    db.session.commit()

    return Response(status=200)


@app.route('/setPepperActive', methods=['POST'])
def setPepperActive():
    try:
        content = request.json
        pep_id = content['pep_id']
        ip = request.access_route[0]
    except:
        print ("Missing Data")
        return Response(status=400)

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


@app.route('/addPepper', methods=['POST'])
def addPepper():
    try:
        content = request.json
        pep_id = content['pep_id']
        PSK = content['PSK']
        ip = request.access_route[0]
    except:
        print ("Missing Data")
        return Response(status=400)

    new_pepper = Pepper(pep_id=pep_id, ip_address=ip, PSK='')
    db.session.add(new_pepper)
    db.session.commit()
    return Response(status=200)

# # TODO: removeUser  #Optional, Not in requirements but useful admin tool
# @app.route('/removeUser', methods=['POST'])
# def removeUser():
#     content = request.json

@app.errorhandler(500)
def server_error(e):
    return """An internal error occurred: <pre>{}</pre>See logs for full stacktrace.""".format(e), 500


# --------------ANDROID-GAME-ROUTES-----------------

def relay_to_pepper():
    # if request.method == 'POST':
    # print (request.path)

    content = request.json
    pep_id = content.pop('pep_id')
    print (pep_id)
    print (content)

    if request.path == '/startgame':
        uname = content['android_username']
        FBToken = content.pop('FBToken')

        user_query = User.query.filter_by(username=uname).first()
        if user_query is None:
            return Response(status=409)

        user_query.FBToken = FBToken
        db.session.commit()

    #Get IP Address from Database
    pepper = Pepper.query.filter_by(pep_id=pep_id).first()
    if pepper is None:
        return Response(status=409)

    relay_ip = "http://" + pepper.ip_address + ":8080"
    print("Relay ip: " + relay_ip)

    # if TEST_BOOL:
    #     return Response(status=200)

    #Send to Pepper
    #TODO: change this back to relay_ip before Production
    req = r.post('http://10.0.0.3:8082' + request.path, json=content) #Local Test
    #req = r.post(relay_ip + request.path, json=content) #TODO: or data=json.dumps(content)

    return Response(status=req.status_code)


app.add_url_rule('/startgame', 'Start', relay_to_pepper, methods=['POST'])
app.add_url_rule('/sendresults', 'Results', relay_to_pepper, methods=['POST'])
app.add_url_rule('/pepperanimation', 'PAnimation', relay_to_pepper, methods=['POST'])

# --------------PEPPER-GAME-ROUTES------------------


def relay_to_android():
    content = request.json

    content.update({'path':request.path[1:]})

    print (request.path)

    uname = content.pop('android_username')

    user_query = User.query.filter_by(username=uname).first()
    if user_query is None:
        return Response(status=409)


    notif = messaging.Notification("RTA Test Notif", "Relay To Android")
    message = messaging.Message(
        data=content,
        notification=notif,
        token=atho_token, #TODO: Replace with user_query.FBToken
    )

    response = messaging.send(message)
    # if TEST_BOOL:
    return Response(status=200)


app.add_url_rule('/acceptgame', 'Accept', relay_to_android, methods=['POST'])
app.add_url_rule('/endgame', 'End', relay_to_android, methods=['POST'])
app.add_url_rule('/androidanimation', 'AAnimation', relay_to_android, methods=['POST'])

# ------------------DIAGNOSTIC-ROUTES---------------

# TODO: Delete this with setIP, getIP
pepper_ip_address = ""

@app.route('/setIP', methods=['POST'])
def set_ip():
    print('/setIP')
    global pepper_ip_address
    print(request.access_route)
    print("first= " + request.access_route[0])
    pepper_ip_address = request.remote_addr

    print ('Test IP = ' + pepper_ip_address)
    return 'ip set!'


@app.route('/sendToIP', methods=['GET'])
def send_to_ip():
    print("Sending to: " + pepper_ip_address + ":8080")
    req = r.post("http://" + pepper_ip_address + ":8080/")
    return req.text

#TODO: Disable before Production
@app.route('/testDB', methods=['GET'])
def create_TestSet():
    print("Create Test DB")

    model_sqlalchemy.wipeDB()

    admin = User(username='admin', email='admin@example.com', name='Atho', password='admin', ASK='', FBToken='')
    subin = User(username='subin', email='subin@example.com', name='S', password='subin', ASK='', FBToken='')
    david = User(username='david', email='david@example.com', name='D', password='subin', ASK='', FBToken='')
    kass = User(username='kass', email='kass@example.com', name='K', password='subin', ASK='', FBToken='')

    adminSalt = UserAuth(pep_id='salt', username='admin', email='admin@example.com')
    adminFraser = UserAuth(pep_id='fraser', username='admin', email='admin@example.com')
    subinSalt = UserAuth(pep_id='salt', username='subin', email='subin@example.com')
    davidSalt = UserAuth(pep_id='salt', username='david', email='david@example.com')

    salt = Pepper(pep_id='Salt', ip_address='10.0.0.3', PSK='')
    fraser = Pepper(pep_id='fraser', ip_address='10.0.0.4', PSK='')
    simon = Pepper(pep_id='simon', ip_address='10.0.0.5', PSK='')
    fan = Pepper(pep_id='fan', ip_address='10.0.0.6', PSK='')
    window = Pepper(pep_id='window', ip_address='10.0.0.7', PSK='')

    db.session.add_all([admin, subin, david, kass])
    db.session.add_all([adminSalt, adminFraser, subinSalt, davidSalt])
    db.session.add_all([salt, fraser, simon, fan, window])
    db.session.commit()

    saltauth = UserAuth.query.get(('salt', 'admin'))
    fraserauth = UserAuth.query.get(('fraser', 'admin'))
    saltauth.authorized = True
    fraserauth.authorized = True
    db.session.commit()

    return 'Database Wiped and replaced with Test Set'


# TODO: Disable or add authentication before Production
@app.route('/wipeDatabase', methods=['GET'])
def wipe_db():
    model_sqlalchemy.wipeDB()
    return 'DB Wiped'


# TODO: Disable or add authentication before Production
@app.route('/showDB', methods=['GET'])
def showDB():
    users = User.query.all()
    uauths = UserAuth.query.all()
    peppers = Pepper.query.all()

    result = '<h3> Users: </h3><br>'

    for user in users:
        result = result + 'Username: ' + user.username + '| Email: ' + user.email + '| Name: ' + user.name + '| Password: ' + user.password + '|' + '<br>' + 'FBT: ' + user.FBToken + '|' + 'ASK: ' + user.ASK + '<br><br>'
    result += '<h3> UserAuths: </h3><br>'
    for uauth in uauths:
        result = result + 'pep_id: ' + uauth.pep_id + '| username: ' + uauth.username + '| email: ' + uauth.email + '| Authorized: ' + str(
            uauth.authorized) + '<br><br>'
    result += '<h3> Peppers: </h3><br>'
    for pepper in peppers:
        result = result + 'pep_id: ' + pepper.pep_id + '| ip_address: ' + pepper.ip_address + '<br><br>'

    return result


def Telepresence():
    return '01001101 01111001 00100000 01101110 01100001 01101101 01100101 00100000 01101001 01110011 00100000 01010000 01100101 01110000 01110000 01100101 01110010 00101110 00100000 01010010 01100101 01110011 01101001 01110011 01110100 01100001 01101110 01100011 01100101 00100000 01101001 01110011 00100000 01100110 01110101 01110100 01101001 01101100 01100101 00101110'
app.add_url_rule('/', 'TP', Telepresence)


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

def check_sk(key, id):
    query = Pepper.query.filter_by(pep_id=id).first()
    if query is None:
        return False
    cloud_key = query.PSK

    return check_password_hash(key, cloud_key)


def generate_random_string():
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(15))


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)

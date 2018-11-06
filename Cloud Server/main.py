from flask import Flask,request,jsonify,Blueprint
from flask import Response
import requests as r

import os
import config
import json
from flask_sqlalchemy import SQLAlchemy
import sqlalchemy

app = Flask(__name__)
# crud = Blueprint('crud',__name__)
# app.register_blueprint(crud,url_prefix='/db')

pepper_ip_address = ""

app.config.from_object(config)
#app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['SQLALCHEMY_DATABASE_URI']
#app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

#-----------------ENTITY-DEFINITIONS-----
class Pepper(db.Model):
    pep_id = db.Column(db.String(100), primary_key=True)
    ip_address = db.Column(db.String(100))

    def __init__(self, pep_id, ip_address):
        self.pep_id = pep_id
        self.ip_address = ip_address

class AuthUsers(db.Model):
    pep_id = db.Column(db.String(100), primary_key=True)
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100))

    def __init__(self, pep_id, username, email):
        self.pep_id = pep_id
        self.username = username
        self.email = email

class User(db.Model):
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100), unique=True)
    name = db.Column(db.String(100))
    password = db.Column(db.String(100))

    def __init__(self, username, email, name, password):
        self.username = username
        self.email = email
        self.name = name
        self.password = password

class AuthRequest(db.Model):
    pep_id = db.Column(db.String(100), primary_key=True)
    username = db.Column(db.String(100), primary_key=True)
    email = db.Column(db.String(100))

    def __init__(self, pep_id, username, email):
        self.pep_id = pep_id
        self.username = username
        self.email = email

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
        # print("Received Message= "+request.values.get('msg'))
        # #print("Request.headers= "+ str(request.headers.values))
        # print("Request.remote_addr= " + request.remote_addr)
        return jsonify({'msg':'Echo echo echo echo echo.'})

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

@app.route('/photo', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['file']
        return Response(status=200)


#------------------DIAG-ROUTES---------------
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
    db.create_all()
    return 'db created'

@app.route('/adminPepper',methods=['GET'])
def user_pepper():
    print("ADD USER/PEPPER")
    admin = User(username='admin', email='admin@example.com', name='Atho', password='admin')
    pepper = Pepper(pep_id='Salt', ip_address='10.0.0.3')

    db.session.add(admin)
    db.session.add(pepper)
    db.session.commit()
    return 'added user/pepper'

@app.route('/dbquery', methods=['GET'])
def query_db():
    print("QUERY FUNC")

    print(User.query.all())
    print(AuthRequest.query.all())
    print(Pepper.query.all())
    print(AuthUsers.query.all())
    return 'Queries in Logs'

if __name__ == '__main__':
    app.run(host='127.0.0.1',port=8080)
#Tester to see if all requests on Cloud server function as needed
#Check tests through interpreter and final result with
# ip/showDB in a browser
import requests
import json
from flask import jsonify

#ip = 'http://python-server-221001.appspot.com'
ip = 'http://localhost:8080'

r = requests.get(ip + '/wipeDatabase')

r = requests.post(ip + '/echomessage',json={'msg': "data"})
print(r.text)

#-------------------------------------------------------------------
print 'Adding users, admin,admin2,admin3'
r = requests.post(ip + '/addUser',json={'username':'admin','email':'admin@example.com','password':'admin','name':'atho'})
print r.text
adminASK = json.loads(r.text)['ASK']
print adminASK
r = requests.post(ip + '/addUser', json={'username':'admin2','password':'admin','email':'admin2@example.com','name':'bob'})
print r.text
admin2ASK = json.loads(r.text)['ASK']
r = requests.post(ip + '/addUser', json={'username':'admin3','password':'admin','email':'admin3@example.com','name':'asdf'})
print r.text
admin3ASK = json.loads(r.text)['ASK']

#-------------------------------------------------------------------
print 'Adding peppers: salt,salty,bob'
r = requests.post(ip + '/addPepper',json={'pep_id':'salt','PSK':''})
r = requests.post(ip + '/addPepper',json={'pep_id':'salty','PSK':''})
r = requests.post(ip + '/addPepper',json={'pep_id':'bob','PSK':''})

#-------------------------------------------------------------------
print 'Adding Auth Requests'
r = requests.post(ip + '/reqAuth',json={'username':'admin','email':'admin@example.com','pep_id':'salt','ASK':adminASK})
print "salt,admin"
print str(r) + "\nContent: " + r.text

#r = requests.post(ip + '/reqAuth',json={'username':'admin1','email':'admin@example.com','pep_id':'salt'})
#print "salt,admin1"
#print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/reqAuth',json={'username':'admin2','email':'admin@example.com','pep_id':'salt','ASK':admin2ASK})
print "salt,admin2"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/reqAuth',json={'username':'admin3','email':'admin@example.com','pep_id':'salt','ASK':admin3ASK})
print "salt,admin3"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/reqAuth',json={'username':'admin3','email':'admin@example.com','pep_id':'salty','ASK':admin3ASK})
print "salty,admin3"
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print 'Authorize User Test'
r = requests.post(ip + '/authorizeUser', json={'username':'admin2','pep_id':'salt','PSK':''})
print "salt,admin2 authorized"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/authorizeUser',json={'pep_id':'salt','PSK':'','username':'admin3'})
print "salt,admin3 authorized"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/authorizeUser',json={'pep_id':'salty','PSK':'','username':'admin3'})
print "salty,admin3 authorized"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/authorizeUser', json={'username':'admin2','pep_id':'salt','PSK':''})
print "Authorize admin2,salt"
print r.text
#-------------------------------------------------------------------
print 'Adding Peppers, salt,salty,bob'
r = requests.post(ip + '/addPepper',json={'pep_id':'salt','PSK':''})
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/addPepper',json={'pep_id':'salty','PSK':''})
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/addPepper',json={'pep_id':'bob','PSK':''})
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print("Login Test")
r = requests.post(ip + '/login',json={'username': "admin",'password':'admin'})
print "Valid Creds"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'notadmin','password':''})
print "Blank Password"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'','password':'asdf'})
print "Blank User"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'','password':''})
print "Both Blank"
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print('getAuthRequests Test')
r = requests.post(ip + '/getAuthRequests',json={'pep_id':'salt','PSK':''})
print "Getting AuthRequests for salt"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/getAuthUsers',json={'pep_id':'salt','PSK':''})
print "Getting Authorized users for salt"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/getAuthUsers',json={'pep_id':'salty','PSK':''})
print "Getting Authorized users for salty"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/getAuthUsers',json={'pep_id':'bob','PSK':''})
print "Getting Authorized users for bob"
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print ("Deauthorize Test")
r = requests.post(ip + '/deAuth',json={'username':'admin','pep_id':'salt','PSK':'','ASK':adminASK})
print "deauth admin,salt"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/deAuth',json={'username':'asdf','pep_id':'salt','PSK':'','ASK':adminASK})
print "deauth not existing UserAuth"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/deAuth',json={'username':'admin3','pep_id':'salt','PSK':'','ASK':admin3ASK})
print "deauth admin3salt"
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print ('setPepperActive Test')
print ('Send from diff computer or change networks')
r = requests.post(ip + '/setPepperActive',json={'pep_id':'salt','PSK':''})
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/setPepperActive',json={'pep_id':'salty','PSK':''})
print str(r) + "\nContent: " + r.text

#-------------------------------------------------------------------
print ('PepperLogin Test')
r = requests.post(ip + '/login',json={'username': "admin",'password':'admin'})
print "Valid Creds"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'notadmin','password':''})
print "Blank Password"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'','password':'asdf'})
print "Blank User"
print str(r) + "\nContent: " + r.text

r = requests.post(ip + '/login',json={'username':'','password':''})
print "Both Blank"
print str(r) + "\nContent: " + r.text





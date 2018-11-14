# server to handle requests from user

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import json 
import urllib2
import urlparse
import qi
import sys


class handleHTTPRequest(BaseHTTPRequestHandler):
	def do_POST(s):
		request_path = s.path 
		request_headers = s.headers 
		path = urlparse.urlparse(request_path).path
		content_length = request_headers.getheaders('content-length')
		length = int(content_length[0]) if content_length else 0
		content = s.rfile.read(length)
		# data = {'firstName':'firstName','lastName':'lastName','email':'email','password':'password'}
		# jobj = json.loads(content)
		if path == '/message':
			# print("request_headers : %s" % request_headers)
	  		#self.logger.debug("content : %s" % s.rfile.read(length))
	  		# 	request_path = s.path 
			# request_headers = s.headers 
			# content_length = request_headers.getheaders('content-length')
			# length = int(content_length[0]) if content_length else 0
	  		print("OUTPUT %s" % content)   #s.rfile.read(length)
	  		s.send_response(200)
	  		s.send_header("Set-Cookie", "foo=bar")
	  		s.end_headers()
	  		s.wfile.write(json.dumps('received message'))
			return content
	  	elif path == '/photo': 
	  		filedata = urllib2.urlopen(request_path)
	  		s.send_response(200)
	  		s.send_header("Set-Cookie", "foo=bar")
	  		s.end_headers()
	  		datatowrite = filedata.read()
	  		with open(s.grabPath(),'wb') as s:
	  			s.write(datatowrite)
	  		s.wfile.write(json.dumps('received photo'))

	

		# Pepper to cloud post requests
		# not WORKING 
		# ON SUCCESS: returns nothing
		# ON FAILURE : string '404' error code
	@staticmethod
	def deAuth(username,pep_id):  
		try:
			url = 'http://python-server-221001.appspot.com/deAuth'
			data = {'pep_id':pep_id,'username':username,'ASK':'','PSK':'232'}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'}) #data,{'Content-Type': 'application/json'}
			# stat_code = urllib2.urlopen(req)
			# req.add_header('Content-Type','application/json')
			response = urllib2.urlopen(req) #json.dumps(data)
			html = response.read()
			print html
			return html
		except urllib2.HTTPError, e:
			return str(e.code)
	
	def deAuthResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response == '404':
				print ("Error")
			#Rerender deauth html page with updated info
			

	#test = deAuth('admin', 'bob')
	#deAuthResponseProcess(test)
		#WORKING 
		#RETURN TYPE: JSON 
		# ON SUCCESS: RETURNS ASK.  Doesn't allow to enter the same email and username twice. 
		# ON FAILURE : string '500' error code
	@staticmethod
	def addUser(username,password,email,firstName,lastName): 
		try:
			name = firstName + ' ' + lastName 
			url = 'http://python-server-221001.appspot.com/addUser'
			data = {'username':username,'password':password,'email':email,'name':name}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return html
		except urllib2.HTTPError, e:
			return str(e.code)

	@staticmethod
	def addUserResponseProcess(server_response):
		print("server_response is of type: ")
		print(type(server_response))
		print("processing response..." + server_response)
		if isinstance(server_response, str):
			print("string response")
			if server_response == '500':
				#input into ALmemory 

				#Handle error request here
				print ("error code")
				return server_response
			else:
				#Handle Success request here
				#input into ALmemory
				print ("success! generating ASK")
				return server_response

	#Example code... Working
	#test = addUser("tombradyfootball", "hotpockets55", "tombrady@hotmail.com", "marlon5", "bravo5")
	#addUserResponseProcess(test)
	
	

		#WORKING 
		#ON SUCCESS: RETURNS string '200'
		#ON FAILURE: RETURNS string '404'
	def authorizeUser(pep_id,PSK,username): 
		try:
			url = 'http://python-server-221001.appspot.com/authorizeUser'
			data = {'pep_id':pep_id,'PSK':PSK,'username':username}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return str(response.getcode())
		except urllib2.HTTPError,e:
			return str(e.code)

	#test = authorizeUser("kas", "FFFFFFFFFFFFFFF", "kassym")
	#print(test)

	def authorizeUserResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response=='200':
				#re-render authuser page with updated info
				print "Success! User authorized!"
			if server_response=='404':
				print "Failure!"



		#WORKING 
		#ON SUCCESS: RETURNS STRING '200'
		#ON FAILURE: RETURNS STRING '404'
	def setPepperActive(pep_id,PSK):
		try:
			url = 'http://python-server-221001.appspot.com/setPepperActive'
			data = {'pep_id':pep_id,'PSK':PSK}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return str(response.getcode())
		except urllib2.HTTPError, e:
			return str(e.code)

	#test = setPepperActive("Odell", "FFFFFFFFFFFFFFF")
	#print(test)

	def setPepperActiveResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response=='200':
				#go to home.html page
				print "Success!"
			if server_response=='404':
				#stay on current page
				print "Failure!"

	#WORKING 
	#ON SUCCESS: RETURNS STRING '200'
	#ON FAILURE: RETUNS STRING '500'
	@staticmethod
	def addPepper(pep_id):
		try:
			url = 'http://python-server-221001.appspot.com/addPepper'
			data = {'pep_id':pep_id,'PSK':'232'}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return str(response.getcode())
		except urllib2.HTTPError, e:
			return str(e.code)
	@staticmethod
	def addPepperResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response=='200': 
				print "Success!"
				return server_response
			if server_response=='500':
				print "Failure! Try another name"
				return server_response

	#Example code... Working
	#test = addPepper('JMayer')
	#addPepperResponseProcess(test)


		#WORKING 
		#ON SUCCESS: RETUNS { "AuthReqs": []  }
		#ON FAILRUE: RETURNS STRING '500'
	@staticmethod
	def getAuthRequests(pep_id,PSK): 
		try:
	 		url = 'http://python-server-221001.appspot.com/getAuthRequests'
			data = {'pep_id':pep_id,'PSK':PSK}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return html
		except urllib2.HTTPError, e:
			return str(e.code)
	@staticmethod
	def getAuthRequestsResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response=='500':
				print "Failure!"
				return server_response
			else:
				print "Success!"
				print server_response
				return server_response


	#test = getAuthRequests('salt', '127.0.0.1')
	#getAuthRequestsResponseProcess(test)

# deserealize it to list or string
#{   "AuthUsers": [     [       "admin",        "admin@example.com"     ],      [       "admin2",        "admin2@example.com"     ]   ] }  
		#WORKING
		#ON SUCCESS: RETURNS { "AuthUsers": [] }
		#ON FAILURE: RETURNS '500'
	def getAuthUsers(pep_id,PSK):
		try:
			url = 'http://python-server-221001.appspot.com/getAuthUsers'
			data = {'pep_id':pep_id,'PSK':PSK}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return html
		except urllib2.HTTPError, e:
			return str(e.code)


	def getAuthUsersResponseProcess(server_response):
		if isinstance(server_response, str):
			if server_response == '500':
				print("FAILURE")
			else:
				print("SUCCESS")
				print(server_response)
				#put into ALMemory

	#Example code... Works
	#test = getAuthUsers('salt', '127.0.0.1')
	#getAuthUsersResponseProcess(test)



	#ON SUCCESS: RETURNS 200 
	#ON FAILURE: RETURNS 410 when pep_id is wrong or user is not authorised; 409 when username or password is wrong
	@staticmethod
	def pepperLogin(username,password,pep_id):
		try:
			url = 'http://python-server-221001.appspot.com/pepperLogin'
			data = {'username':username,'password':password,'pep_id':pep_id}
			data = json.dumps(data)
			req = urllib2.Request(url,data,{'Content-Type': 'application/json'})
			response = urllib2.urlopen(req)
			html = response.read()
			return str(response.getcode())
		except urllib2.HTTPError, e:
			return str(e.code)

	@staticmethod
	def pepperLoginResponse(server_response):
		if isinstance(server_response, str):
			if server_response== '200':
				print("SUCCESS LOGIN COMPLETE!")
				return server_response
			if server_response== '410':
				print("PEPID WRONG OR USER NOT AUTHORIZED")
				return server_response
			if server_response == '409':
				print("Wrong name / not authorized")
				return server_response
		
	#Example code... Works
	#test = pepperLogin('admin2','admin2', 'bob')
	#pepperLoginResponse(test)

	def grabPath():
		#BehvaiorPath BP: grabs the absolute path and normalizes it
		behaviorPath = os.path.normpath(self.frameManager.getBehaviorPath(self.behaviorId))
		self.logger.info("behaviour path is "+ behaviorPath)
		#BP: checks if the directory is valid
		if os.path.isdir(behaviorPath):
			behaviorPath = os.path.join(behaviorPath, "")
			#BP: creates path PackageManager/apps which we can use to grab part of path that we want
			#appsFolderFragment = os.path.join("PackageManager", "apps")
			#Change if we change our behaviour name 
			endOfPath = "behavior_1"
			if not (endOfPath in behaviorPath):
				self.logger.error("behaviour1 is not in behaviorPath")
				return None
			#BP splits path via PackageManager/apps and grabs the second half which is our desired path
			fragment = behaviorPath.split(endOfPath, 1)[0]
			self.logger.info("This should be the beggining of the path: " + fragment)
			fragment = os.path.join(fragment, "html/images")
			fragment = fragment.split("/")[1]
			self.logger.info("This is the path that we grabbed: " + fragment)
			return fragment.lstrip("\\/")
	  				



	
            


if __name__ == '__main__':
	port = 8085
	print('Listening on localhost:%s' % port)
	server = HTTPServer(('10.0.0.3',port),handleHTTPRequest)
	server.serve_forever()
	
	




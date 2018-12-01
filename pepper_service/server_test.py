from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import json 
import urllib2
import urlparse
from pepper_server_service import *

# THIS IS USED TO TEST PEPPER_SERVICE FUNCTIONS 

####################################ADD USER ROUTE
#test = handleHTTPRequest.addUser('jackjohnson255','qwerty','bananapancakes55@gmail.com','john','michael')
#test = handleHTTPRequest.pepperLogin('admin2','admin', 'bob')
#handleHTTPRequest.pepperLoginResponse(test)

#GET AUTHORIZED USERS
test = handleHTTPRequest.deAuth('salt', 'david', 'psk')



################################ADD PEPPER ROUTE 
#test = handleHTTPRequest.addPepper('Jackie_Chan', 'admin', 'hashed_psk')
#test = handleHTTPRequest.getAuthRequests('salt', '127.0.0.123')
#handleHTTPRequest.getAuthRequestsResponseProcess(test)

#test = handleHTTPRequest.addPepper('newpepper', 'david', '67121b2ce709291b9ccf0da4fb69a98c9e8d7c83aab77d15a9abcad6cfea4b04')

####################################GAME TESTING 
#test = handleHTTPRequest.send_accept("jupiter", "a planet", "android_user", "pepper_user" )
#test = handleHTTPRequest.send_endgame("TIE", "android_user")
#test = handleHTTPRequest.send_android_animation("dance", "android_user")



#test = handleHTTPRequest.do_POST()
print(test)



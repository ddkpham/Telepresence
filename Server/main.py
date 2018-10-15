from flask import Flask
from naoqi import ALProxy

app = Flask(__name__)

@app.route("/", methods=['GET'])
def connect():
	tts = ALProxy("ALTextToSpeech", "10.0.0.5", 9559)
	tts.say("Pepper is succesfully connected!")
	return Response(status=200)

@app.route("/speak")
def speak():
	return "Speak!"

@app.route("/game")
def speak():
	return "Game!"

if __name__ == '__main__':
	app.run()

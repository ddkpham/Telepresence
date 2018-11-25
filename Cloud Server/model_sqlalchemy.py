from flask_sqlalchemy import SQLAlchemy
db = SQLAlchemy()

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
    FBToken = db.Column(db.String(200))

    def __init__(self, username, email, name, password, ASK, FBToken):
        self.username = username
        self.email = email
        self.name = name
        self.password = password
        self.ASK = ASK
        self.FBToken = FBToken

def wipeDB():
    db.drop_all()
    db.create_all()
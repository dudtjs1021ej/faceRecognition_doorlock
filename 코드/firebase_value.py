import firebase_admin
from firebase_admin import credentials,db
from firebase_admin import firestore
from firebase_admin import storage

PROJECT_ID="fir-connjava"
cred = credentials.Certificate("fir-connjava-firebase-adminsdk-qnktd-8f492c5c5a.json")
default_app = firebase_admin.initialize_app(cred, {
    'storageBucket': 'fir-connjava.appspot.com',
    'databaseURL':'https://fir-connjava-default-rtdb.firebaseio.com/'
})

#firebase_admin.initialize_app(cred)
def get_name():
	ref_name=db.reference('name')
	name=ref_name.get()
	return name

def get_door():
	ref_door=db.reference()
	door=ref_door.get()
	ref_door.update({'door':'haha'})
	return door

def get_startValue():
	ref_startValue=db.reference('start_value')
	startValue=ref_startValue.get()
	return startValue

def startValue_update():
	ref_startValue=db.reference()
	ref_startValue.update({'start_value':'False'})
	startValue=ref_startValue.get()
	return startValue

def get_Image():
	bucket=storage.bucket()
	blob=bucket.blob("images/"+ get_name() +"1.jpg")
	blob.download_to_filename("OriginData/"+get_name()+"1.jpg")

def get_detectValue():
	ref_detectValue=db.reference('detect_value')
	detectValue = ref_detectValue.get()
	return detectValue

def detectValue_update():
	ref_detectValue=db.reference()
	ref_detectValue.update({'detect_value':'True'})
	detectValue=ref_detectValue.get()
	return detectValue

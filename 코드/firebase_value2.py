import firebase_admin
import pyrebase
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import firestore
from firebase_admin import storage
from datetime import datetime
from uuid import uuid4

PROJECT_ID="fir-connjava"
cred = credentials.Certificate("fir-connjava-firebase-adminsdk-qnktd-8f492c5c5a.json")
default_app = firebase_admin.initialize_app(cred, {
    'storageBucket': 'fir-connjava.appspot.com',
    'databaseURL':'https://fir-connjava-default-rtdb.firebaseio.com/'
})

config ={
        "apiKey":"AIzaSyDSG9bwd3FFvjfTodwoW38awyEva729LCc",
        "authDomain":"fir-connjava.firebaseapp.com",
        "databaseURL":"https://fir-connjava.firebaseio.com/",
        "storageBucket":"fir-connjava.appspot.com"
}

firebase=pyrebase.initialize_app(config)
storage2=firebase.storage()

bucket=storage.bucket('fir-connjava.appspot.com')

def  getDoorAccess():
    ref_doorAccess=db.reference('doorAccess')
    doorAccess=ref_doorAccess.get()
    return str(doorAccess)

def uploadDoorAccess(x):
    ref=db.reference()
    doorAccess=ref.get()
    ref.update({'doorAccess':x})


def getVisitorsNum():
    ref_visitors=db.reference('Visitors')
    ref_num=db.reference('Num')
    ref_visitorsNum=db.reference('Num/visitorsNum')
    visitorsNum=ref_visitorsNum.get()
    return visitorsNum

def uploadVisitor():
    ref_visitors=db.reference('Visitors')
    ref_num=db.reference('Num')
    visitorsNum=getVisitorsNum()
    visitorsNum+=1
    ref_addVisitors=db.reference('Visitors/Visitor_'+str(visitorsNum))
    path='visitors/visitor'+str(visitorsNum)+'.jpg'
    metadata=bucket.get_blob(path).metadata #token
    for value in metadata.values():
        
        token=value

    imgUrl=storage2.child("visitors/visitor"+str(visitorsNum)+".jpg").get_url(token)
    #print(imgUrl)
    time=datetime.today().strftime("%Y/%m/%d %H:%M:%S")
    a= ref_addVisitors.update({
        'time':time,
        'profile':imgUrl
        #'name':name
    })
    
    ref_num.update({'visitorsNum':visitorsNum})
    return True
    
def uploadImage():
    bucket=storage.bucket()
    visitorNum=getVisitorsNum()
    visitorNum+=1
    blob = bucket.blob("visitors/visitor"+str(visitorNum)+".jpg")
    new_token = uuid4()
    metadata = {"firebaseStorageDownloadTokens": new_token}
    blob.metadata = metadata
    blob.upload_from_filename("visitors/visitor"+str(visitorNum)+".jpg")
    
def uploadAlarm(x):
    ref=db.reference()
    alarm=ref.get()
    ref.update({'alarm':x})

def get_detectValue():
        ref_detectValue=db.reference('detect_value')
        detectValue = ref_detectValue.get()
        return detectValue

def detectValue_update():
        ref_detectValue=db.reference()
        ref_detectValue.update({'detect_value':'True'})
        detectValue=ref_detectValue.get()
        return detectValue

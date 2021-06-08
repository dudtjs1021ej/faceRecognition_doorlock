import firebase_value2
import dlib
import cv2
import numpy as np
import os
from math import hypot
import time
import face_doorlock_test

def midpoint(p1, p2):
    
    return int((p1.x + p2.x)/2), int((p1.y + p2.y)/2)

def get_blinking_ratio(eye_points, facial_landmarks):
    
    left_point = (facial_landmarks.part(eye_points[0]).x, facial_landmarks.part(eye_points[0]).y)

    right_point = (facial_landmarks.part(eye_points[3]).x, facial_landmarks.part(eye_points[3]).y)

    center_top = midpoint(facial_landmarks.part(eye_points[1]), facial_landmarks.part(eye_points[2]))

    center_bottom = midpoint(facial_landmarks.part(eye_points[5]), facial_landmarks.part(eye_points[4]))
 
    hor_line_lenght = hypot((left_point[0] - right_point[0]), (left_point[1] - right_point[1]))
    
    ver_line_lenght = hypot((center_top[0] - center_bottom[0]), (center_top[1] - center_bottom[1]))

    ratio = hor_line_lenght / ver_line_lenght

    return ratio

def main():
    
    detector = dlib.get_frontal_face_detector()

    predictor = dlib.shape_predictor("shape_predictor_68_face_landmarks.dat")

    face_recog = dlib.face_recognition_model_v1("dlib_face_recognition_resnet_model_v1.dat")

    faceDescs_list = os.listdir('faceDescs/')#faceDescs안에 있는 데이터 전부 가져오기

    r_eye_points = [42, 43, 44, 45, 46, 47]

    l_eye_poits = [36, 37, 38, 39, 40, 41]    

    stream_cap = cv2.VideoCapture(0)
    
    cnt=0
    not_found=0

    while True:
        count_blinking = 0
        
        detect_value = firebase_value2.get_detectValue()
        
        ret, img_bgr = stream_cap.read()

        img_bgr = cv2.flip(img_bgr, -1)

        if not ret:

            break

        img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
        
        faces = detector(img_rgb, 1)
        
        if faces:
            print('얼굴을 감지중입니다.')
            if (cnt%10)==0:
                firebase_value2.uploadDoorAccess(True)
            cnt+=1
            doorAccess=firebase_value2.getDoorAccess()
            if doorAccess == "True" :
                visitorsNum=firebase_value2.getVisitorsNum()
                cv2.imwrite("visitors/visitor"+str(visitorsNum+1) +".jpg", img_bgr)
                firebase_value2.uploadImage()
                if firebase_value2.uploadVisitor()==True:
                    firebase_value2.uploadAlarm(True)
                    firebase_value2.uploadDoorAccess(False)
        else:
            #print("face not found")
            not_found+=1
            if(not_found == 10):
                detect_value = False
                detect_value = firebase_value2.detectValue_update()
                break

        last_found = {'name': 'unknown', 'dist': 0.4, 'color': (0,0,255)}

        for k, d in enumerate(faces):
            #사각형위치 찾아놓기
            rect = ( (d.left(), d.top()), (d.right(), d.bottom()) )
        
            landmarks = predictor(img_rgb, d)

            left_eye_ratio = get_blinking_ratio(l_eye_poits, landmarks)

            right_eye_ratio = get_blinking_ratio(r_eye_points, landmarks)
            
            face_descriptor = face_recog.compute_face_descriptor(img_rgb, landmarks)

            blinking_ratio = (left_eye_ratio + right_eye_ratio) / 2
                     
            if blinking_ratio >= 5.0:

                count_blinking += 1
            
            for i in range(len(faceDescs_list)):
                descs = np.load('faceDescs/' + faceDescs_list[i], allow_pickle=True)[()]

                for name, saved_desc in descs.items():

                    dist = np.linalg.norm([face_descriptor] - saved_desc, axis=1)#유클리디안 거리계산
                    
                    if dist >= 0.4:
                        last_found = {'name': name, 'dist': dist, 'color': (255,255,255)}
                        
                    if dist < 0.4:
                        if(count_blinking >= 1):
                            print("등록된 사용자입니다.")
                            face_doorlock_test.main()
                            last_found = {'name': name, 'dist': dist, 'color': (255,255,255)}
                            cv2.rectangle(img_bgr, pt1=(d.left(), d.top()), pt2=(d.right(), d.bottom()), color=last_found['color'], thickness=2)
                            cv2.putText(img_bgr, last_found['name'], org=(d.left(), d.top()), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=last_found['color'], thickness=2)
                            detect_value = False
        
        if(detect_value == False):
            detect_value = firebase_value2.detectValue_update()
            time.sleep(5)
            return

    stream_cap.release()
    
    cv2.destroyAllWindows()

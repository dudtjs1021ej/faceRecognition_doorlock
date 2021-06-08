import firebase_value
import dlib
import cv2
import numpy as np
import openface


def find_faces (image) :

    faces = detector(image, 1)

    if len(faces) == 0:

        return np.empty(0)

    rects, lands = [],[]

    lands_np = np.zeros((len(faces), 68, 2), dtype=np.int)

    for k, d in enumerate(faces):

        rect = ( (d.left(), d.top()), (d.right(), d.bottom()) )

        rects.append(rect)

        land = predictor(image, d)

        lands.append(land)

        for i in range(0, 68):

            lands_np[k][i] = (land.part(i).x, land.part(i).y)

    return rects, lands, lands_np

def encode_faces(image, lands):

    face_descriptors = []

    for land in lands:

        face_descriptor = face_recog.compute_face_descriptor(image, land)

        face_descriptors.append(np.array(face_descriptor))

    return np.array(face_descriptors)

while(True):
    start_value = firebase_value.get_startValue()
    if(start_value == 'True'):
        print("얼굴데이터 등록 중 입니다....")
        firebase_value.get_Image()
        detector = dlib.get_frontal_face_detector()

        predictor = dlib.shape_predictor("shape_predictor_68_face_landmarks.dat")

        face_recog = dlib.face_recognition_model_v1("dlib_face_recognition_resnet_model_v1.dat")

        userName=firebase_value.get_name()
        print("사용자이름 : " + userName)

        face_aligner = openface.AlignDlib("shape_predictor_68_face_landmarks.dat")

        file_name = "OriginData/" + userName + "1.jpg"

        image = cv2.imread(file_name)

        detected_faces = detector(image, 1)

        for i, face_rect in enumerate(detected_faces):
            pose_landmarks = predictor(image, face_rect)
            alignedFace = face_aligner.align(534, image, face_rect, landmarkIndices=openface.AlignDlib.OUTER_EYES_AND_NOSE)
            cv2.imwrite("faceData/" + userName + "{}.jpg".format(i+1), alignedFace)

        #사진을 분석해서 데이터 저장
        image_pathe =(r"faceData/")#분석할 사진이 있는 경로
        image_paths={userName : image_pathe}
        img_num = 1 #학습하는 사진의 갯수 = 1
        descs = {userName : None}#인코딩 결과

        for name, image_path in image_paths.items():#name = userName, image_path = faceData/
            for idx in range (img_num):
                img_p = image_path + userName + str(idx+1) + '.jpg'
                img_bgr = cv2.imread(img_p)
                img_rgb = cv2.cvtColor(img_bgr,cv2.COLOR_BGR2RGB)
                #저장된 사진에서 얼굴 찾기
                _, img_lands, _ = find_faces(img_rgb)
                #userName에 인코딩값을 넣기
                descs[userName] = encode_faces(img_rgb, img_lands)
                print("얼굴등록이 완료되었습니다.")
        #인코딩파일 저장
        np.save('faceDescs/' + userName + '.npy', descs)
        start_value = firebase_value.startValue_update()
    else:
        continue

package com.example.facerecognitionlock;

public class Visitors { //사진과 시간이 있는 방문자 관리 클래스
    private String profile;
    private String time;

    public Visitors(){}

    public String getprofile() {
        return profile;
    }

    public void setprofile(String profile) {
        this.profile = profile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

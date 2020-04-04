package com.example.scoringsystem.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YoKeyword on 16/2/1.
 */
public class Question
//        implements Parcelable
{
    private String subject;
    private String title;
    private String answer;
    private int ID_Q;

    public Question(String subject, String title, String answer, int ID_Q) {
        this.subject = subject;
        this.title = title;
        this.answer = answer;
        this.ID_Q = ID_Q;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setID_Q(int ID_Q) {
        this.ID_Q = ID_Q;
    }

    public int getID_Q() {
        return ID_Q;
    }

}
//    private int imgRes;
//
//    public Question(String subject, int imgRes) {
//        this.subject = subject;
//        this.imgRes = imgRes;
//    }
//    protected Question(Parcel in) {
//        subject = in.readString();
//        title = in.readString();
//        imgRes = in.readInt();
//    }
//    public static final Creator<Question> CREATOR = new Creator<Question>() {
//        @Override
//        public Question createFromParcel(Parcel in) {
//            return new Question(in);
//        }
//
//        @Override
//        public Question[] newArray(int size) {
//            return new Question[size];
//        }
//    };
//    public int getImgRes() {
//        return imgRes;
//    }
//
//    public void setImgRes(int imgRes) {
//        this.imgRes = imgRes;
//    }
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(subject);
//        dest.writeString(title);
//        dest.writeInt(imgRes);
//    }




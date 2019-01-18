package com.ViewPagerContact;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contacts implements Parcelable{

    private String mName, mNickname, mEmail;

    private boolean isAdmin;


    public Contacts(String name, String nickName, String email, Boolean isAdmin) {
        mName = name;
        mNickname = nickName;
        mEmail = email;
        this.isAdmin = isAdmin;
    }

    public Contacts(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);

        this.mName = data[0];
        this.mNickname = data[1];
        this.mEmail = data[2];
        this.isAdmin = false;
    }

    public String getName() {
        return mName;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }


    private static int lastContactId = 0;

    public static ArrayList<Contacts> createContactsList(int numContacts) {
        ArrayList<Contacts> contacts = new ArrayList<>();

        for (int i = 1; i <= numContacts; i++) {
            boolean isAdm = i < numContacts / 10;
            contacts.add(new Contacts(
                    "Person " + ++lastContactId,
                    "Nick "+ lastContactId,
                    "P" + lastContactId + "@email.com",
                    isAdm));
        }

        return contacts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[] {this.mName, this.mNickname, this.mEmail});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contacts createFromParcel(Parcel in) {
            return new Contacts(in);
        }

        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };
}
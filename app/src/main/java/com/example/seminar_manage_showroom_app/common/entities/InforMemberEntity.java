package com.example.seminar_manage_showroom_app.common.entities;

public class InforMemberEntity {
    private String rfid;
    private String type;
    private String name;
    private String member_ID;
    private String gender;
    private String current_membership;
    private String contact;
    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_ID() {
        return member_ID;
    }

    public void setMember_ID(String member_ID) {
        this.member_ID = member_ID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrent_membership() {
        return current_membership;
    }

    public void setCurrent_membership(String current_membership) {
        this.current_membership = current_membership;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "InforBookEntity{" +
                "ShelfCode='" + rfid + '\'' +
                ", Book_title='" + member_ID + '\'' +
                ", Categories='" + name + '\'' +
                ", Author='" + current_membership + '\'' +
                ", ISBN_13='" + gender + '\'' +
                ", RfidCode='" + contact + '\'' +
                '}';
    }
}

package com.sccodesoft.schoolfinder;

public class SearchHistory {
    public String school,address,marks,stype;

    public SearchHistory()
    {

    }

    public SearchHistory(String school, String address, String marks, String stype) {
        this.school = school;
        this.address = address;
        this.marks = marks;
        this.stype = stype;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }
}

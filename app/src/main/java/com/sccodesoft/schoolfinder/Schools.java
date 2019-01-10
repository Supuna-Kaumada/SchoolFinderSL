package com.sccodesoft.schoolfinder;


public class Schools
{

    public String key,address,category,medium,name,phone,rating,religion,type,website,dist;

    public Schools(String key,String address, String category, String medium, String name, String phone, String rating, String religion, String type, String website,String dist) {
        this.key = key;
        this.address = address;
        this.category = category;
        this.medium = medium;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.religion = religion;
        this.type = type;
        this.website = website;
        double result1 = Math.round((Float.valueOf(dist)/1000)* 100.00) / 100.00 ;
        this.dist = String.valueOf(result1);
    }

    public String getAddress() {
        return address;
    }

    public String getDist() {
        return dist;
    }

    public String getCategory() {
        return category;
    }

    public String getMedium() {
        return medium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getKey() {
        return key;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRating() {
        return rating;
    }

    public String getReligion() {
        return religion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }


}

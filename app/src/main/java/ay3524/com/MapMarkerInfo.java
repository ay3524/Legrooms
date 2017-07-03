package ay3524.com;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ashish on 03-07-2017.
 */

public class MapMarkerInfo implements Parcelable {
    private String price, lRules, desc, lTitle, longitude,
            latitude, address, neighborhood,
            city, state, country, email;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getlRules() {
        return lRules;
    }

    public void setlRules(String lRules) {
        this.lRules = lRules;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getlTitle() {
        return lTitle;
    }

    public void setlTitle(String lTitle) {
        this.lTitle = lTitle;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.price);
        dest.writeString(this.lRules);
        dest.writeString(this.desc);
        dest.writeString(this.lTitle);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.address);
        dest.writeString(this.neighborhood);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.country);
        dest.writeString(this.email);
    }

    public MapMarkerInfo() {
    }

    protected MapMarkerInfo(Parcel in) {
        this.price = in.readString();
        this.lRules = in.readString();
        this.desc = in.readString();
        this.lTitle = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.address = in.readString();
        this.neighborhood = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<MapMarkerInfo> CREATOR = new Parcelable.Creator<MapMarkerInfo>() {
        @Override
        public MapMarkerInfo createFromParcel(Parcel source) {
            return new MapMarkerInfo(source);
        }

        @Override
        public MapMarkerInfo[] newArray(int size) {
            return new MapMarkerInfo[size];
        }
    };
}

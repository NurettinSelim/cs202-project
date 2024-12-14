package model;

public class Hotel {
    private int hotelId;
    private String hotelName;
    private String address;
    private String phone;

    // Constructors
    public Hotel() {}

    public Hotel(int hotelId) {
        this.hotelId = hotelId;
    }

    public Hotel(int hotelId, String hotelName, String address, String phone) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", hotelName='" + hotelName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
} 
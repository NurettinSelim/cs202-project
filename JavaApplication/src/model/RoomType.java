package model;

import java.math.BigDecimal;

public class RoomType {
    private int typeId;
    private Hotel hotel;
    private String typeName;
    private BigDecimal basePrice;
    private int capacity;
    private int bedCount;

    // Constructors
    public RoomType() {}

    public RoomType(int typeId, Hotel hotel, String typeName, BigDecimal basePrice, int capacity, int bedCount) {
        this.typeId = typeId;
        this.hotel = hotel;
        this.typeName = typeName;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.bedCount = bedCount;
    }

    public RoomType(int typeId) {
        this.typeId = typeId;
    }

    // Getters and Setters
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getBedCount() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        this.bedCount = bedCount;
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "typeId=" + typeId +
                ", hotel=" + hotel +
                ", typeName='" + typeName + '\'' +
                ", basePrice=" + basePrice +
                ", capacity=" + capacity +
                ", bedCount=" + bedCount +
                '}';
    }
} 
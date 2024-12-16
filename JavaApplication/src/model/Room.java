package model;

public class Room {
    private Hotel hotel;
    private String roomNumber;
    private RoomType roomType;
    private RoomStatus status;

    // Constructors
    public Room() {}

    public Room(Hotel hotel, String roomNumber, RoomType roomType, RoomStatus status) {
        this.hotel = hotel;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    // Getters and Setters
    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "hotel=" + hotel +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType=" + roomType +
                ", status=" + status +
                '}';
    }

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        if (roomNumber != null) {
            sb.append("Room Number: ").append(roomNumber).append("\n");
        }
        if (roomType != null) {
            sb.append("Room Type: ").append(roomType.getTypeName()).append("\n");
            sb.append("Capacity: ").append(roomType.getCapacity()).append("\n");
            sb.append("Bed Count: ").append(roomType.getBedCount()).append("\n");
            sb.append("Base Price: ").append(roomType.getBasePrice()).append("\n");
        }
        if (status != null) {
            sb.append("Status: ").append(status.getStatusName()).append("\n");
        }
        if (hotel != null) {
            sb.append("Hotel: ").append(hotel.getHotelName()).append("\n");
        }
        return sb.toString();
    }
} 

package model;

import java.sql.Date;
import java.sql.Timestamp;

public class HousekeepingSchedule {
    private int scheduleId;
    private Room room;
    private Date scheduledDate;
    private HousekeepingStaff staff;
    private RoomStatus status;
    private Staff createdBy;
    private Timestamp updatedAt;

    // Constructors
    public HousekeepingSchedule() {}

    public HousekeepingSchedule(int scheduleId, Room room, Date scheduledDate,
                               HousekeepingStaff staff, RoomStatus status,
                               Staff createdBy, Timestamp updatedAt) {
        this.scheduleId = scheduleId;
        this.room = room;
        this.scheduledDate = scheduledDate;
        this.staff = staff;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public HousekeepingStaff getStaff() {
        return staff;
    }

    public void setStaff(HousekeepingStaff staff) {
        this.staff = staff;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public Staff getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Staff createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "HousekeepingSchedule{" +
                "scheduleId=" + scheduleId +
                ", room=" + room +
                ", scheduledDate=" + scheduledDate +
                ", staff=" + staff +
                ", status=" + status +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 
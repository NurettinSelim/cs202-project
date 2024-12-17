package service;

import model.RoomType;
import java.util.ArrayList;

public interface RoomTypeService {
    ArrayList<RoomType> findByHotel(int hotelId);
} 
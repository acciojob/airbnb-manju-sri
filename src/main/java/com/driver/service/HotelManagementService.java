package com.driver.service;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import com.driver.repository.HotelManagementRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotelManagementService {

    HotelManagementRepository hotelManagementRepository = new HotelManagementRepository();

    public String addHotel(Hotel hotel) {

        if(hotel.getHotelName()==null || hotel==null){
            return "FAILURE";
        }
        if(hotelManagementRepository.isHotelExist(hotel)){
            return "FAILURE";
        }
        hotelManagementRepository.saveHotel(hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {
       return hotelManagementRepository.saveUser(user);
    }

    public String getHotelWithMostFacilities() {

     //   List<Hotel> hotelList =  hotelManagementRepository.getListOfHotel();

        HashMap<String, Hotel> hotelDb = hotelManagementRepository.getListOfHotel();
        List<String> hotelList = new ArrayList<>();

        for (String hotel : hotelDb.keySet()){
            hotelList.add(hotel);
        }

        int maxFacilities =0;
        String hotelWithMostFacilities= "";

        for (String hotel: hotelDb.keySet() ){
           int numberOfFacilities = hotelDb.get(hotel).getFacilities().size();
           if(numberOfFacilities>maxFacilities){
               maxFacilities = numberOfFacilities;
               hotelWithMostFacilities = hotelDb.get(hotel).getHotelName();
           }
           else if (numberOfFacilities==maxFacilities && hotelDb.get(hotel).getHotelName().compareTo(hotelWithMostFacilities)<0){
               hotelWithMostFacilities= hotelDb.get(hotel).getHotelName();
           }
        }
        return hotelWithMostFacilities.isEmpty()?"":hotelWithMostFacilities;
    }

    public int bookARoom(Booking booking) {
        Hotel hotel = hotelManagementRepository.getHotelByBooking(booking);
        int totalAmountPaid = booking.getNoOfRooms()*hotel.getPricePerNight();
        UUID uuid = UUID.randomUUID();
        String id = String.valueOf(uuid);
        int availableRooms = hotelManagementRepository.availableRoomsInHotel(booking);

        if (booking.getNoOfRooms()<=availableRooms){
            booking.setBookingId(id);
            booking.setAmountToBePaid(totalAmountPaid);
            hotelManagementRepository.bookARoomAndSave(booking);
            return totalAmountPaid;
        }
        return -1;
    }

    public int getBookingDetails(Integer aadharCard) {
       List<Booking> bookingsList = hotelManagementRepository.getBookingsList(aadharCard);
       int bookingByPerson =0;
       for (Booking booking: bookingsList){
           if (booking.getBookingAadharCard()==aadharCard){
               bookingByPerson++;
           }
       }
       return bookingByPerson;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
       // hotelManagementRepository.addFacilitiesToHotel(newFacilities, hotelName);
       Hotel hotel = hotelManagementRepository.getHotelById(hotelName);
     /*  if(hotel==null){
           return null;
       } */
       // Set<Facility> existingFacilities = new HashSet<>(hotel.getFacilities());
        List<Facility> ans = hotel.getFacilities();

        if(Objects.nonNull(hotel)){
            for( Facility name : newFacilities){
                if(ans.contains(name)) continue;
                else ans.add(name);
          }
       }

       hotel.setFacilities(ans);
       hotelManagementRepository.saveHotel(hotel);
       return hotel;
    }
}

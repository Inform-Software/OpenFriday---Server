package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.Room;
import com.syncrotess.openfriday.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/room")
public class RoomController {

    @Autowired
    RoomRepository roomRepository;

    // used to send messages to the websocket
    @Autowired
    SimpMessagingTemplate template;

    /**
     * Adds a new room. If the given room is already present in the repo, it is updated.
     * @param room the room to be added
     */
    @RequestMapping("/add")
    public ResponseEntity<Void> addRoom(@RequestBody Room room) {
        roomRepository.save(room);
        template.convertAndSend("/topic/rooms", roomRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a room.
     * @param id the id of the room to delete
     */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        roomRepository.deleteById(id);
        template.convertAndSend("/topic/rooms", roomRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all saved rooms
     * @return All rooms
     */
    @RequestMapping("/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Room>> getRooms() {
        return new ResponseEntity<>(roomRepository.findAll(), HttpStatus.OK);
    }
}

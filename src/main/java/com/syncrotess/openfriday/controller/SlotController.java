package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.Timeslot;
import com.syncrotess.openfriday.repository.TimeslotRepository;
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
@RequestMapping("/rest/slot")
public class SlotController {

    @Autowired
    TimeslotRepository timeslotRepository;

    // used to send messages to the websocket
    @Autowired
    SimpMessagingTemplate template;

    /**
     * Adds a new timeslot. If the given timeslot is already present in the repo, it is updated.
     * @param timeslot the timeslot to be added
     */
    @RequestMapping("/add")
    public ResponseEntity<Void> addSlot(@RequestBody Timeslot timeslot) {
        timeslotRepository.save(timeslot);
        template.convertAndSend("/topic/slots", timeslotRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a slot.
     * @param id the id of the slot to delete
     */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSlot(@PathVariable("id") Long id) {
        timeslotRepository.deleteById(id);
        template.convertAndSend("/topic/slots", timeslotRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all saved slots
     * @return All slots
     */
    @RequestMapping("/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Timeslot>> getSlots() {
        return new ResponseEntity<>(timeslotRepository.findAll(), HttpStatus.OK);
    }
}

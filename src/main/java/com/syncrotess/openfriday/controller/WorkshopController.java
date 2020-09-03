package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.Workshop;
import com.syncrotess.openfriday.repository.VoteRepository;
import com.syncrotess.openfriday.repository.WorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/rest/workshop")
public class WorkshopController {

    @Autowired
    WorkshopRepository workshopRepository;
    @Autowired
    VoteRepository voteRepository;

    // used to send messages to the websocket
    @Autowired
    SimpMessagingTemplate template;

    /**
     * Adds a new workshop
     * @param workshop the workshop to add
     */
    @Transactional
    @RequestMapping("/add")
    public ResponseEntity<Void> addWorkshop(@RequestBody Workshop workshop) {

        if (workshop.getId() != null && workshopRepository.findById(workshop.getId()).isPresent()) {
            int votesHigh = voteRepository.findAllByWorkshopAndPriority(workshop, 2).size();
            int votesLow = voteRepository.findAllByWorkshopAndPriority(workshop, 1).size();
            workshop.setVotesHigh(votesHigh);
            workshop.setVotesLow(votesLow);
            workshop.setTotalVotes(votesHigh + votesLow);
        }

        workshopRepository.save(workshop);
        template.convertAndSend("/topic/workshops", workshopRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes the workshop with the given id
     * @param id the id of the workshop to delete
     */
    @RequestMapping("/delete/{workshopID}")
    public ResponseEntity<Void> deleteWorkshop(@PathVariable("workshopID") Long id) {
        workshopRepository.deleteById(id);

        template.convertAndSend("/topic/workshops", workshopRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all workshops.
     * @return all workshops
     */
    @RequestMapping("/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Workshop>> getWorkshops() {
        return new ResponseEntity<>(workshopRepository.findAll(), HttpStatus.OK);
    }
}

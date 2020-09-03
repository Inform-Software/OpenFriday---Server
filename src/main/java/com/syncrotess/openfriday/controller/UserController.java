package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.*;
import com.syncrotess.openfriday.repository.TimeslotRepository;
import com.syncrotess.openfriday.repository.UserRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rest/user")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TimeslotRepository timeslotRepository;
    @Autowired
    WorkshopRepository workshopRepository;

    // used to send messages to the websocket
    @Autowired
    SimpMessagingTemplate template;

    /**
     * Checks if the user with the given id is admin
     * @param id the id of the user to test
     * @return true if user is admin, else false
     */
    @RequestMapping("/isadmin/{userID}")
    @ResponseBody
    public ResponseEntity<Boolean> isAdmin(@PathVariable("userID") Long id) {
        Optional<User> user = userRepository.findById(id);
        return new ResponseEntity<>(user.isPresent() && user.get() instanceof Admin, HttpStatus.OK);
    }

    /**
     * Sets the timeslots of the user with the given id to the given timeslots array
     * @param id the id of the user to update
     * @param timeslots the new timeslots
     */
    @RequestMapping("/setslots/{userID}")
    public ResponseEntity<Void> setUserSlots(@PathVariable ("userID") Long id, @RequestBody Set<Timeslot> timeslots) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        user.get().setTimeslots(timeslots);
        userRepository.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns the slots of the user with the given id
     * @param userID the id of the user
     * @return the slots of the user
     */
    @RequestMapping("/getslots/{userID}")
    @ResponseBody
    public ResponseEntity<Set<Timeslot>> getUserSlots(@PathVariable ("userID") Long userID) {
        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(user.get().getTimeslots(), HttpStatus.OK);
    }

    /**
     * Returns the votes of the user with the given id
     * @param userId the id of the user
     * @return the votes of the user
     */
    @RequestMapping("/getvotes/{userId}")
    @ResponseBody
    public ResponseEntity<Set<Vote>> getUserVotes(@PathVariable ("userId") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(user.get().getVotes(), HttpStatus.OK);
    }

    /**
     * Deletes the user with the given id
     * @param id the id of the user to be deleted
     */
    @RequestMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("id") Long id) {
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Adds a new admin. If a user with the given name is already existing, it is upgraded to an admin so votes and timeslot dont change
     * TODO: existing user cant be overridden
     * @param admin the admin to be added
     */
    @RequestMapping("/addadmin")
    public ResponseEntity<Void> addAdmin(@RequestBody Admin admin) {
        // Check if user with this name already exists as normal user
        Optional<User> userOpt = userRepository.findByName(admin.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Admin newAdmin = new Admin(user.getId(), admin.getName(), admin.getPassword(), user.getTimeslots(), user.getVotes());
            userRepository.save(newAdmin);
        }
        else {
            List<Timeslot> timeslotList = new ArrayList<>();
            timeslotRepository.findAll().forEach(timeslotList::add);
            admin.setTimeslots(new HashSet<>(timeslotList));
            userRepository.save(admin);
        }
        template.convertAndSend("/topic/admins", userRepository.findAll().stream().filter(u -> u instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Changes the password of the admin with the given id if the current password is correct.
     * @param id the id of the admin to be updated
     * @param passwords Array with the current password [0] and the new password [1]
     * @return false if the current password is not correct, else true
     */
    @RequestMapping("/changePassword/{userID}")
    @ResponseBody
    public ResponseEntity<Boolean> changePassword(@PathVariable ("userID") Long id, @RequestBody String[] passwords) {
        Optional<User> adminOpt = userRepository.findById(id);
        if (adminOpt.isPresent() && ((Admin) adminOpt.get()).comparePassword(passwords[0])) {
            Admin admin = (Admin) adminOpt.get();
            admin.setPassword(passwords[1]);
            userRepository.save(admin);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

    /**
     * Deletes an admin. The user is kept as a normal user.
     * TODO: existing user cant be overridden
     * @param id the id of the admin to delete
     */
    @RequestMapping("/deleteAdmin/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAdmin(@PathVariable ("id") Long id) {
        Optional<User> adminOpt = userRepository.findById(id);

        if (!adminOpt.isPresent() || !(adminOpt.get() instanceof Admin)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Admin admin = (Admin) adminOpt.get();
        if (admin.getName().equals("admin")) {                      // Prevent standard admin from being deleted
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = new User(admin.getId(), admin.getName(), admin.getTimeslots(), admin.getVotes());
        userRepository.save(user);
        template.convertAndSend("/topic/admins", userRepository.findAll().stream().filter(u -> u instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns a Collection with all admins
     * @return Collection with all admins
     */
    @RequestMapping("/getAdmins")
    @ResponseBody
    public ResponseEntity<Collection<User>> getAllAdmins() {
        return new ResponseEntity<>(userRepository.findAll().stream().filter(user -> user instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Evaluates the vote of the given user for the given workshop.
     * @param userId the id of the voting user
     * @param workshopId the id of the workshop the user is voting for
     * @return all votes of the user
     */
    @RequestMapping("/vote/{userId}/{workshopId}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Set<Vote>> vote(@PathVariable ("userId") Long userId, @PathVariable ("workshopId") Long workshopId) {
        Optional<Workshop> workshopOpt = workshopRepository.findById(workshopId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (!workshopOpt.isPresent() || !userOpt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Workshop workshop = workshopOpt.get();
        User user = userOpt.get();

        user.vote(workshop);
        userRepository.save(user);

        template.convertAndSend("/topic/workshops", workshopRepository.findAll());
        return new ResponseEntity<>(user.getVotes(), HttpStatus.OK);
    }
}

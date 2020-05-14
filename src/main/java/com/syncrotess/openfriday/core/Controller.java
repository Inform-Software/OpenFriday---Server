package com.syncrotess.openfriday.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.syncrotess.openfriday.nodes.*;
import com.syncrotess.openfriday.repository.SlotRepository;
import com.syncrotess.openfriday.repository.UserRepository;
import com.syncrotess.openfriday.repository.WorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private SimpMessagingTemplate template;

    final
    UserRepository userRepository;
    final
    SlotRepository slotRepository;
    final
    WorkshopRepository workshopRepository;

    /**
     * Constructor for Controller. Links the repositories and creates the standard admin user.
     * Parameters are automatically filled by spring
     */
    public Controller() {
        userRepository = new UserRepository("userrepository.ofa");

        User user = userRepository.findUserByName("admin");
        if (user == null) {
            Admin admin = new Admin("admin", "c52191036b200df797e638e7cf18ae5e", new ArrayList<>());
            this.userRepository.addUser(admin);
        }

        slotRepository = new SlotRepository("slotrepository.ofa");

        if (slotRepository.isEmpty()) {
            this.slotRepository.addSlot(new Slot("10:30 - 12:00"));
            this.slotRepository.addSlot(new Slot("12:30 - 14:00"));
            this.slotRepository.addSlot(new Slot("14:15 - 15:30"));
        }

        this.workshopRepository = new WorkshopRepository("workshoprepository.ofa");
    }

    /**
     * Redirects to the login page
     */
    @RequestMapping("/")
    @ResponseBody
    public ResponseEntity<Void> root (HttpServletResponse response) throws IOException {
        response.sendRedirect("/web/login.html");                   // redirects to the login page
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates a new user if no user with this name if present. Used to login the user on the page.
     * @param userInput the user containing the username
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/rest/login/user/")
    @ResponseBody
    public ResponseEntity<User> loginUser(@RequestBody User userInput){

        userInput.setName(userInput.getName().toLowerCase());

        if (userInput.getName().isEmpty())
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        User user = userRepository.findUserByName(userInput.getName());

        if (user instanceof Admin) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        if (user == null) {
            userInput.setSlots(slotRepository.getAllSlots().toArray(new Slot[slotRepository.getAllSlots().size()]));
            userRepository.addUser(userInput);
            return new ResponseEntity<>(userInput, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    }

    /**
     * Checks if an admin with the given name and password is present. If yes, the admin is logged in on the page.
     * @param passedAdmin the admin object containing the username and the password
     * @return true if data was correct, false if not
     */
    @RequestMapping("/rest/login/admin")
    @ResponseBody
    public ResponseEntity<Admin> loginAdmin(@RequestBody Admin passedAdmin) {
        if (passedAdmin.getName().isEmpty() || passedAdmin.isPasswordEmpty())
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        Admin admin = (Admin) userRepository.findUserByName(passedAdmin.getName().toLowerCase());
        if (admin!= null && admin.comparePassword(passedAdmin)){
            return new ResponseEntity<>(admin, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }

    /**
     * Checks if the user with the given id is admin
     * @param id the id of the user to test
     * @return true if user is admin, else false
     */
    @RequestMapping("/rest/user/isadmin/{userID}")
    @ResponseBody
    public ResponseEntity<Boolean> isAdmin(@PathVariable ("userID") UUID id) {
        User user = userRepository.findUser(id);
        return new ResponseEntity<>(user instanceof Admin, HttpStatus.OK);
    }

    /**
     * Sets the slots of the user with the given id to the given slots array
     * @param id the id of the user to update
     * @param slotIds the new slots
     */
    @RequestMapping("/rest/user/setslots/{userID}")
    public ResponseEntity<Void> setUserSlots(@PathVariable ("userID") UUID id, @RequestBody UUID[] slotIds) {

        Slot[] slots = new Slot[slotIds.length];
        for (int i = 0; i < slotIds.length; i++) {
            slots[i] = slotRepository.findSlot(slotIds[i]);
        }

        User user = userRepository.findUser(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        user.setSlots(slots);
        userRepository.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns the slots of the user with the given id
     * @param userID the id of the user
     * @return the slots of the user
     */
    @RequestMapping("/rest/user/getslots/{userID}")
    @ResponseBody
    public ResponseEntity<List<Slot>> getUserSlots(@PathVariable ("userID") UUID userID) {
        User user = userRepository.findUser(userID);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(user.getSlots(), HttpStatus.OK);
    }

    /**
     * Returns the votes of the user with the given id
     * @param userId the id of the user
     * @return the votes of the user
     */
    @RequestMapping("/rest/user/getvotes/{userId}")
    @ResponseBody
    public ResponseEntity<HashMap<UUID, Integer>> getUserVotes(@PathVariable ("userId") UUID userId) {
        User user = userRepository.findUser(userId);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(user.getVotes(), HttpStatus.OK);
    }

    /**
     * Deletes the user with the given id
     * @param id the id of the user to be deleted
     */
    @RequestMapping("/rest/user/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("id") UUID id) {
        userRepository.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Adds a new admin. If a user with the given name is already existing, it is upgraded to an admin so votes and timeslot dont change
     * @param admin the admin to be added
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/rest/user/addadmin")
    public ResponseEntity<Void> addAdmin(@RequestBody Admin admin) {
        // Check if user with this name already exists as normal user
        User user = userRepository.findUserByName(admin.getName());
        if (user != null) {
            Admin newAdmin = new Admin(user.getId(), admin.getName(), admin.getPassword(), user.getSlots(), user.getVotes());
            userRepository.updateUser(user.getId(), newAdmin);
        }
        else {
            admin.setSlots(slotRepository.getAllSlots().toArray(new Slot[0]));
            userRepository.addUser(admin);
        }
        template.convertAndSend("/topic/admins", userRepository.getAllUsers().stream().filter(u -> u instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Changes the password of the admin with the given id if the current password is correct.
     * @param id the id of the admin to be updated
     * @param passwords Array with the current password [0] and the new password [1]
     * @return false if the current password is not correct, else true
     */
    @RequestMapping("/rest/user/changePassword/{userID}")
    @ResponseBody
    public ResponseEntity<Boolean> changePassword(@PathVariable ("userID") UUID id, @RequestBody String[] passwords) {
        Admin admin = (Admin) userRepository.findUser(id);
        if (admin.comparePassword(passwords[0])) {
            admin.setPassword(passwords[1]);
            userRepository.updateUser(id, admin);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

    /**
     * Deletes an admin. The user is kept as a normal user.
     * @param id the id of the admin to delete
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/rest/user/deleteAdmin/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAdmin(@PathVariable ("id") UUID id) {
        Admin admin = (Admin) userRepository.findUser(id);
        if (admin.getName().equals("admin")) {                      // Prevent standard admin from being deleted
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = new User(admin.getId(), admin.getName(), admin.getSlots(), admin.getVotes());
        userRepository.updateUser(id, user);
        template.convertAndSend("/topic/admins", userRepository.getAllUsers().stream().filter(u -> u instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns a Collection with all admins
     * @return Collection with all admins
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/rest/user/getAdmins")
    @ResponseBody
    public ResponseEntity<Collection<User>> getAllAdmins() {
        return new ResponseEntity<>(userRepository.getAllUsers().stream().filter(user -> user instanceof Admin).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Evaluates the vote of the given user for the given workshop.
     * @param userId the id of the voting user
     * @param workshopId the id of the workshop the user is voting for
     * @return all votes of the user
     */
    @RequestMapping("/rest/user/vote/{userId}/{workshopId}")
    @ResponseBody
    public ResponseEntity<HashMap<UUID, Integer>> vote(@PathVariable ("userId") UUID userId, @PathVariable ("workshopId") UUID workshopId) {
        Workshop workshop = workshopRepository.findWorkshop(workshopId);
        if (workshop.isLocked())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User user = userRepository.findUser(userId);
        int prio = user.vote(workshopId);
        userRepository.updateUser(userId, user);
        if (prio == 2) {
            workshop.setVotesHigh(workshop.getVotesHigh() + 1);
        }
        else if (prio == 1) {
            workshop.setVotesHigh(workshop.getVotesHigh() - 1);
            workshop.setVotesLow(workshop.getVotesLow() + 1);
        }
        else {
            workshop.setVotesLow(workshop.getVotesLow() - 1);
        }
        workshopRepository.updateWorkshop(workshopId, workshop);
        template.convertAndSend("/topic/workshops", workshopRepository.getAllWorkshops());
        return new ResponseEntity<>(user.getVotes(), HttpStatus.OK);
    }

    /**
     * Adds a new timeslot
     * @param slot the slot to be added
     */
    @RequestMapping("/rest/slot/add")
    public ResponseEntity<Void> addSlot(@RequestBody Slot slot) {
        slotRepository.addSlot(slot);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Updates the slot with the given id to the new slot. The id of the given slot is not changed, so it should be the same as the given id
     * @param id the id of the slot to be updated
     * @param newSlot the new slot
     * @return true
     */
    @RequestMapping("/rest/slot/edit/{slotID}")
    @ResponseBody
    public ResponseEntity<Boolean> editSlot(@PathVariable ("slotID") UUID id, @RequestBody Slot newSlot) {
        slotRepository.updateSlot(id, newSlot);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * Deletes a slot.
     * @param id the id of the slot to delete
     */
    @RequestMapping("/rest/slot/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSlot(@PathVariable ("id") UUID id) {
        slotRepository.deleteSlot(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all saved slots
     * @return All slots
     */
    @RequestMapping("/rest/slot/getall")
    @ResponseBody
    public ResponseEntity<Collection<Slot>> getSlots() {
        return new ResponseEntity<>(slotRepository.getAllSlots(), HttpStatus.OK);
    }

    /**
     * Adds a new workshop
     * @param workshop the workshop to add
     */
    @RequestMapping("/rest/workshop/add")
    public ResponseEntity<Void> addWorkshop(@RequestBody Workshop workshop) {
        workshopRepository.addWorkshop(workshop);
        template.convertAndSend("/topic/workshops", workshopRepository.getAllWorkshops());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Updates the workshop with the given id to the new workshop. The id of the given workshop is not changed, so it should be the same as the given id
     * @param id the id of the workshop to update
     * @param workshop the new workshop
     */
    @RequestMapping("/rest/workshop/edit/{workshopID}")
    public ResponseEntity<Void> editWorkshop(@PathVariable("workshopID") UUID id, @RequestBody Workshop workshop) {
        lockWorkshop(id); // workshop gets locked so the votes dont change during edit operations
        Workshop old = workshopRepository.findWorkshop(id);
        workshop.setVotesLow(old.getVotesLow());
        workshop.setVotesHigh(old.getVotesHigh());
        workshopRepository.updateWorkshop(id, workshop);
        unlockWorkshop(id);
        template.convertAndSend("/topic/workshops", workshopRepository.getAllWorkshops());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes the workshop with the given id
     * @param id the id of the workshop to delete
     */
    @RequestMapping("/rest/workshop/delete/{workshopID}")
    public ResponseEntity<Void> deleteWorkshop(@PathVariable ("workshopID") UUID id) {
        workshopRepository.deleteWorkshop(id);
        template.convertAndSend("/topic/workshops", workshopRepository.getAllWorkshops());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all workshops.
     * @return all workshops
     */
    @RequestMapping("/rest/workshop/getall")
    @ResponseBody
    public ResponseEntity<Collection<Workshop>> getWorkshops() {
        return new ResponseEntity<>(workshopRepository.getAllWorkshops(), HttpStatus.OK);
    }



    // PRIVATE HELPER METHODS

    private void lockWorkshop(UUID workshopId) {
        Workshop workshop = workshopRepository.findWorkshop(workshopId);
        workshop.setLocked(true);
        workshopRepository.updateWorkshop(workshopId, workshop);
    }

    private void unlockWorkshop(UUID workshopId) {
        Workshop workshop = workshopRepository.findWorkshop(workshopId);
        workshop.setLocked(false);
        workshopRepository.updateWorkshop(workshopId, workshop);
    }


    // DEBUG METHODS ------ REMOVE BEFORE DEPLOY!

    @JsonView(Views.Public.class)
    @RequestMapping("/rest/debug/getallusers")
    @ResponseBody
    public ResponseEntity<Collection<User>> getAllUsers() {
        return new ResponseEntity<>(userRepository.getAllUsers(), HttpStatus.OK);
    }
}

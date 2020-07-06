package com.syncrotess.openfriday.core;

import com.syncrotess.openfriday.entities.*;
import com.syncrotess.openfriday.repository.*;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalIsPresent")
@Controller
public class MainController {

    private Timetable timetable;

    private final SimpMessagingTemplate template;

    private final UserRepository userRepository;
    private final TimeslotRepository timeslotRepository;
    private final WorkshopRepository workshopRepository;
    private final RoomRepository roomRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SolverManager<Timetable, Long> solverManager;

//    @Autowired
//    EntityManager entityManager;

    /**
     * Constructor for Controller. Links the repositories and creates the standard admin user.
     * Parameters are automatically filled by spring
     */
    public MainController(SimpMessagingTemplate template, UserRepository userRepository, TimeslotRepository timeslotRepository, WorkshopRepository workshopRepository, RoomRepository roomRepository, VoteRepository voteRepository) {
        this.template = template;
        this.userRepository = userRepository;
        this.timeslotRepository = timeslotRepository;
        this.workshopRepository = workshopRepository;
        this.roomRepository = roomRepository;

        // create default admin if not present
        Optional<User> user = userRepository.findByName("admin");
        if (!user.isPresent()) {
            Admin admin = new Admin("admin", "c52191036b200df797e638e7cf18ae5e", new HashSet<>());
            this.userRepository.save(admin);
        }

        // create default timeslots if no timeslots present
        if (timeslotRepository.count() <= 0) {
            this.timeslotRepository.save(new Timeslot("10:30 - 12:00"));
            this.timeslotRepository.save(new Timeslot("12:30 - 14:00"));
            this.timeslotRepository.save(new Timeslot("14:15 - 15:30"));
        }
    }

    /**
     * Redirects to the login page
     */
    @RequestMapping("/")
    @ResponseBody
    public ResponseEntity<Void> root (HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // disable caching
        response.sendRedirect("/web/login.html");                   // redirects to the login page
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates a new user if no user with this name if present. Used to login the user on the page.
     * @param userInput the user containing the username
     */
    @RequestMapping("/rest/login/user/")
    @ResponseBody
    public ResponseEntity<User> loginUser(@RequestBody User userInput){

        userInput.setName(userInput.getName().toLowerCase());

        if (userInput.getName().isEmpty())
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        Optional<User> user = userRepository.findByName(userInput.getName());

        if (user.isPresent() && user.get() instanceof Admin) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        if (!user.isPresent()) {
            List<Timeslot> timeslotList = new ArrayList<>();
            timeslotRepository.findAll().forEach(timeslotList::add);
            userInput.setTimeslots(new HashSet<>(timeslotList));
            userRepository.save(userInput);
            return new ResponseEntity<>(userInput, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
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

        Optional<User> adminOpt = userRepository.findByName(passedAdmin.getName().toLowerCase());

        if (!adminOpt.isPresent() || !(adminOpt.get() instanceof Admin) || !((Admin) adminOpt.get()).comparePassword(passedAdmin)) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
        else {
            return new ResponseEntity<>((Admin) adminOpt.get(), HttpStatus.OK);
        }
    }

    /**
     * Checks if the user with the given id is admin
     * @param id the id of the user to test
     * @return true if user is admin, else false
     */
    @RequestMapping("/rest/user/isadmin/{userID}")
    @ResponseBody
    public ResponseEntity<Boolean> isAdmin(@PathVariable ("userID") Long id) {
        Optional<User> user = userRepository.findById(id);
        return new ResponseEntity<>(user.isPresent() && user.get() instanceof Admin, HttpStatus.OK);
    }

    /**
     * Sets the timeslots of the user with the given id to the given timeslots array
     * @param id the id of the user to update
     * @param timeslots the new timeslots
     */
    @RequestMapping("/rest/user/setslots/{userID}")
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
    @RequestMapping("/rest/user/getslots/{userID}")
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
    @RequestMapping("/rest/user/getvotes/{userId}")
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
    @RequestMapping("/rest/user/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("id") Long id) {
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Adds a new admin. If a user with the given name is already existing, it is upgraded to an admin so votes and timeslot dont change
     * TODO: existing user cant be overridden
     * @param admin the admin to be added
     */
    @RequestMapping("/rest/user/addadmin")
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
    @RequestMapping("/rest/user/changePassword/{userID}")
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
    @RequestMapping("/rest/user/deleteAdmin/{id}")
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
    @RequestMapping("/rest/user/getAdmins")
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
    @RequestMapping("/rest/user/vote/{userId}/{workshopId}")
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

    /**
     * Adds a new timeslot. If the given timeslot is already present in the repo, it is updated.
     * @param timeslot the timeslot to be added
     */
    @RequestMapping("/rest/slot/add")
    public ResponseEntity<Void> addSlot(@RequestBody Timeslot timeslot) {
        timeslotRepository.save(timeslot);
        template.convertAndSend("/topic/slots", timeslotRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a slot.
     * @param id the id of the slot to delete
     */
    @RequestMapping("/rest/slot/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSlot(@PathVariable ("id") Long id) {
        timeslotRepository.deleteById(id);
        template.convertAndSend("/topic/slots", timeslotRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all saved slots
     * @return All slots
     */
    @RequestMapping("/rest/slot/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Timeslot>> getSlots() {
        return new ResponseEntity<>(timeslotRepository.findAll(), HttpStatus.OK);
    }

    /**
     * Adds a new room. If the given room is already present in the repo, it is updated.
     * @param room the room to be added
     */
    @RequestMapping("/rest/room/add")
    public ResponseEntity<Void> addRoom(@RequestBody Room room) {
        roomRepository.save(room);
        template.convertAndSend("/topic/rooms", roomRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a room.
     * @param id the id of the room to delete
     */
    @RequestMapping("/rest/room/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRoom(@PathVariable ("id") Long id) {
        roomRepository.deleteById(id);
        template.convertAndSend("/topic/rooms", roomRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all saved rooms
     * @return All rooms
     */
    @RequestMapping("/rest/room/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Room>> getRooms() {
        return new ResponseEntity<>(roomRepository.findAll(), HttpStatus.OK);
    }

    /**
     * Adds a new workshop
     * @param workshop the workshop to add
     */
    @Transactional
    @RequestMapping("/rest/workshop/add")
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
    @RequestMapping("/rest/workshop/delete/{workshopID}")
    public ResponseEntity<Void> deleteWorkshop(@PathVariable ("workshopID") Long id) {
        workshopRepository.deleteById(id);

        template.convertAndSend("/topic/workshops", workshopRepository.findAll());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Returns all workshops.
     * @return all workshops
     */
    @RequestMapping("/rest/workshop/getall")
    @ResponseBody
    public ResponseEntity<Iterable<Workshop>> getWorkshops() {
        return new ResponseEntity<>(workshopRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping("/rest/plan/get")
    @ResponseBody
    public ResponseEntity<Timetable> getTimetable() {
        return new ResponseEntity<>(timetableRepository.findById(TimetableRepository.SINGLETON_TIMETABLE_ID), HttpStatus.OK);
    }

    @RequestMapping("/rest/plan/save")
    public ResponseEntity<Void> saveTimetable(@RequestBody Timetable timetable) {
        timetableRepository.save(timetable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/rest/plan/optimize")
    public ResponseEntity<Timetable> optimizeTimetable() throws InterruptedException {
        solverManager.terminateEarly(TimetableRepository.SINGLETON_TIMETABLE_ID);
        solverManager.solveAndListen(TimetableRepository.SINGLETON_TIMETABLE_ID,
                timetableRepository::findById,
                this::handlePlanDone);
//        Thread.sleep(5000);
//        solverManager.terminateEarly(TimetableRepository.SINGLETON_TIMETABLE_ID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void handlePlanDone(Timetable timetable) {
        this.timetable = timetable;
        template.convertAndSend("/topic/plan", timetable);
        System.out.println("DONE");
//        timetableRepository.save(timetable);
//        Timetable tt = timetableRepository.findById(TimetableRepository.SINGLETON_TIMETABLE_ID);
//        template.convertAndSend("/topic/plan", tt);
    }

    @RequestMapping("/rest/plan/debug")
    public ResponseEntity<Timetable> debug() {
        return new ResponseEntity<>(timetable, HttpStatus.OK);
    }
}

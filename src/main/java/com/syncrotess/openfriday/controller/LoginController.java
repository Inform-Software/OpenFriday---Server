package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.Admin;
import com.syncrotess.openfriday.entities.Timeslot;
import com.syncrotess.openfriday.entities.User;
import com.syncrotess.openfriday.repository.TimeslotRepository;
import com.syncrotess.openfriday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rest/login")
public class LoginController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TimeslotRepository timeslotRepository;

    /**
     * Creates a new user if no user with this name if present. Used to login the user on the page.
     * @param userInput the user containing the username
     */
    @RequestMapping("/user")
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
    @RequestMapping("/admin")
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
}

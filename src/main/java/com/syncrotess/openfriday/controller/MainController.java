package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.*;
import com.syncrotess.openfriday.repository.*;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Controller
public class MainController {

    /**
     * Constructor for Controller. Links the repositories and creates the standard admin user and the default time slots.
     * Parameters are automatically filled by spring
     */
    public MainController(UserRepository userRepository, TimeslotRepository timeslotRepository) {

        // create default admin if not present
        Optional<User> user = userRepository.findByName("admin");
        if (!user.isPresent()) {
            Admin admin = new Admin("admin", "c52191036b200df797e638e7cf18ae5e", new HashSet<>());
            userRepository.save(admin);
        }

        // create default timeslots if no timeslots present
        if (timeslotRepository.count() <= 0) {
            timeslotRepository.save(new Timeslot("10:30 - 12:00"));
            timeslotRepository.save(new Timeslot("12:30 - 14:00"));
            timeslotRepository.save(new Timeslot("14:15 - 15:30"));
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
}

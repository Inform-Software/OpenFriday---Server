package com.syncrotess.openfriday.controller;

import com.syncrotess.openfriday.entities.Timetable;
import com.syncrotess.openfriday.repository.TimetableRepository;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/plan")
public class PlanController {

    @Autowired
    TimetableRepository timetableRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SolverManager<Timetable, Long> solverManager;

    // used to send messages to the websocket
    @Autowired
    SimpMessagingTemplate template;

    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<Timetable> getTimetable() {
        return new ResponseEntity<>(timetableRepository.findById(TimetableRepository.SINGLETON_TIMETABLE_ID), HttpStatus.OK);
    }

    @RequestMapping("/save")
    public ResponseEntity<Void> saveTimetable(@RequestBody Timetable timetable) {
        timetableRepository.save(timetable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/optimize")
    public ResponseEntity<SolverStatus> optimizeTimetable() {
        solverManager.terminateEarly(TimetableRepository.SINGLETON_TIMETABLE_ID); // just to ensure the solver is not running. Otherwise solveAndListen would throw exception
        solverManager.solveAndListen(TimetableRepository.SINGLETON_TIMETABLE_ID,
                timetableRepository::findById,
                this::handlePlanDone);
        return new ResponseEntity<>(solverManager.getSolverStatus(TimetableRepository.SINGLETON_TIMETABLE_ID), HttpStatus.OK);
    }

    // When a new solution is found, it is send to the client(s) via websocket
    // Use the commented lines to see the solution in the console
    private void handlePlanDone(Timetable timetable) {
        template.convertAndSend("/topic/plan", timetable);
//        System.out.println("====================================================================");
//        System.out.println(timetable.getScore());
//        System.out.println(solverManager.getSolverStatus(TimetableRepository.SINGLETON_TIMETABLE_ID));
//        System.out.println(timetable);
    }

    @RequestMapping("/stop")
    public ResponseEntity<SolverStatus> stopOptimization() {
        solverManager.terminateEarly(TimetableRepository.SINGLETON_TIMETABLE_ID);
        return new ResponseEntity<>(solverManager.getSolverStatus(TimetableRepository.SINGLETON_TIMETABLE_ID), HttpStatus.OK);
    }

    @RequestMapping("/getStatus")
    public ResponseEntity<SolverStatus> getSolverStatus() {
        return new ResponseEntity<>(solverManager.getSolverStatus(TimetableRepository.SINGLETON_TIMETABLE_ID), HttpStatus.OK);
    }
}

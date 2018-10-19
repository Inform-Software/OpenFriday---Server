package com.syncrotess.openfriday.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.syncrotess.openfriday.manager.VoteRepository;
import com.syncrotess.openfriday.manager.WorkshopRepository;
import com.syncrotess.openfriday.util.Greeting;
import com.syncrotess.openfriday.util.HelloMessage;
import com.syncrotess.openfriday.util.Status;
import com.syncrotess.openfriday.util.Timetable;
import com.syncrotess.openfriday.util.User;
import com.syncrotess.openfriday.util.Vote;
import com.syncrotess.openfriday.util.Workshop;
import com.syncrotess.openfriday.util.WorkshopComparator;

@org.springframework.stereotype.Controller
public class Controller {

  // Algorythmus algorythmus; // Solving algorithm
  @Autowired
  com.syncrotess.openfriday.manager.UserRepository userrepo;
  @Autowired
  WorkshopRepository                               workrepo;
  @Autowired
  VoteRepository                                   voterepo;
  Status                                           status = new Status ();
  Timetable                                        timetable;

  @RequestMapping (value = "/rest/status")
  @ResponseBody
  public ResponseEntity<Status> getStatus () {
    return new ResponseEntity<> (status, HttpStatus.OK);
  }

  @MessageMapping ("/hello")
  @SendTo ("/topic/greetings")
  public Greeting greeting (HelloMessage message) throws Exception {
    return new Greeting (HtmlUtils.htmlEscape (message.getName ()) + "!");
  }

  @RequestMapping (value = "/rest/status", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<Status> editStatus (@RequestBody Status status) {
    this.status = status;
    this.timetable = new Timetable (status.getSlots ().length, status.getRooms ().length);
    return new ResponseEntity<> (this.status, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/timetable", produces = "application/json")
  @ResponseBody
  public ResponseEntity<String> getTimetable () {
    if (timetable == null) {
      timetable = new Timetable (status.getSlots ().length, status.getRooms ().length);
    }
    return new ResponseEntity<> (timetable.toString (), HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/user")
  @ResponseBody
  public ResponseEntity<Iterable<User>> getAllUsers () {
    Iterable<User> users = userrepo.findAll ();
    return new ResponseEntity<> (users, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/user", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<User> addUser (@RequestBody User user) {
    User result;
    if ((userrepo.findByName (user.getName ())).isPresent ()) {
      result = userrepo.findByName (user.getName ()).get ();
    } else {
      result = userrepo.save (new User ().setName (user.getName ()));
    }
    return new ResponseEntity<> (result, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/user", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<Void> deleteAllUsers () {
    userrepo.deleteAll ();
    return new ResponseEntity<> (HttpStatus.OK);

  }

  @RequestMapping (value = "/rest/user/{id}")
  @ResponseBody
  public ResponseEntity<User> getUser (@PathVariable Long id) {
    Optional<User> user = userrepo.findById (id);
    if (user.isPresent ()) {
      return new ResponseEntity<> (user.get (), HttpStatus.OK);
    } else {
      return new ResponseEntity<User> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/user/{id}", method = RequestMethod.PUT)

  @ResponseBody
  public ResponseEntity<User> editUser (@PathVariable long id,
                                        @RequestBody User inUser) {
    Optional<User> result = userrepo.findById (id);
    if (result.isPresent ()) {
      User user = result.get ();
      user.setWorkshops (inUser.getWorkshops ());
      user.setAdmin (inUser.isAdmin ());
      user.setName (inUser.getName ());
      user = userrepo.save (user);
      return new ResponseEntity<> (user, HttpStatus.OK);
    } else {
      return new ResponseEntity<User> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/user/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<Void> deleteUser (@PathVariable long id) {
    Optional<User> result = userrepo.findById (id);
    if (result.isPresent ()) {
      User user = result.get ();
      userrepo.delete (user);
      return new ResponseEntity<> (HttpStatus.OK);
    } else {
      return new ResponseEntity<> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/user/{userId}/enter/{workshopId}")
  @ResponseBody
  public ResponseEntity<String> addUserToWorkshop (@PathVariable ("userId") long userId,
                                                   @PathVariable ("workshopId") long workshopId) {
    Optional<User> user = userrepo.findById (userId);
    Optional<Workshop> workshop = workrepo.findById (workshopId);
    if (user.isPresent () && workshop.isPresent ()) {
      userrepo.save (user.get ().addWorkshop (workshop.get ()));
      return new ResponseEntity<> (workshop.get ().getName (), HttpStatus.OK);
    } else {
      return new ResponseEntity<> ("Workshop: " + workshop.isPresent () + ", User: " + user.isPresent (),
                                   HttpStatus.NOT_FOUND);
    }

  }

  @RequestMapping (value = "/rest/workshop")
  @ResponseBody
  public ResponseEntity<Iterable<Workshop>> getAllWorkshops () {
    Iterable<Workshop> works = workrepo.findAll ();

    return new ResponseEntity<> (works, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/workshop/sorted")
  @ResponseBody
  public ResponseEntity<Iterable<Workshop>> getAllWorkshopsSorted () {
    Iterable<Workshop> it = workrepo.findAll ();
    List<Workshop> workshops = new ArrayList<> ();
    it.forEach (workshops::add);
    // TODO: List is not sorted properly
    Collections.sort (workshops, new WorkshopComparator ());
    return new ResponseEntity<> (workshops, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/workshop", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<Workshop> addWorkshop (@RequestBody Workshop work) {
    Workshop work1 = new Workshop ();
    work1.setDescription (work.getDescription ());
    work1.setName (work.getName ());
    work1.setTopic (work.getTopic ());
    work1.setCreator (work.getCreator ());
    work1.setVotes (work.getVotes ());
    Workshop saved = workrepo.save (work1);
    return new ResponseEntity<Workshop> (saved, HttpStatus.OK);

  }

  @RequestMapping (value = "/rest/workshop", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Workshop[]> editMultipleWorkshops (@RequestBody Workshop[] workshops) {
    for (int i = 0; i < workshops.length; i++) {

      Optional<Workshop> result = (workrepo.findById (workshops[i].getId ()));
      if (result.isPresent ()) {
        Workshop work1 = result.get ();
        work1.setDescription (workshops[i].getDescription ());
        work1.setName (workshops[i].getName ());
        work1.setTopic (workshops[i].getTopic ());
        work1.setCreator (workshops[i].getCreator ());
        work1.setVotes (workshops[i].getVotes ());
        work1.setSelected (false); // Used in the Webinterface
        workrepo.save (work1);
      } else {
      }
    }
    return new ResponseEntity<Workshop[]> (workshops, HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/workshop", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<Void> deleteAllWorkshops () {
    workrepo.deleteAll ();
    return new ResponseEntity<> (HttpStatus.OK);

  }

  @RequestMapping (value = "/rest/workshop/dummy")
  @ResponseBody
  public ResponseEntity<Workshop> getDummyWorkshop () {

    return new ResponseEntity<> (Workshop.getDummy (), HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/workshop/update")
  @ResponseBody
  public ResponseEntity<Workshop[]> updateAllWorkshops () {
    List<Workshop> workshops = new ArrayList<> ();
    workrepo.findAll ().forEach ( (workshop) -> {
      workshops.add (workshop);
    });
    Workshop[] result = new Workshop[workshops.size ()];
    editMultipleWorkshops (workshops.toArray (result));
    return editMultipleWorkshops (result); // Getters are called; if invalid values are present, they are replaced!
  }

  @RequestMapping (value = "/rest/workshop/{id}")
  @ResponseBody
  public ResponseEntity<Workshop> getWorkshopById (@PathVariable long id) {
    Optional<Workshop> works = workrepo.findById (id);
    if (works.isPresent ()) {
      return new ResponseEntity<> (works.get (), HttpStatus.OK);

    } else {
      return new ResponseEntity<> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/workshop/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<Workshop> editWorkshop (@PathVariable long id,
                                                @RequestBody Workshop work) {
    Optional<Workshop> result = (workrepo.findById (id));
    if (result.isPresent ()) {
      Workshop work1 = result.get ();
      work1.setDescription (work.getDescription ());
      work1.setName (work.getName ());
      work1.setTopic (work.getTopic ());
      work1.setCreator (work.getCreator ());
      // Votes are not edited. Use /vote/{user}/{interest} or /vote/clear!
      workrepo.save (work1);
      return new ResponseEntity<Workshop> (work1, HttpStatus.OK);
    } else {
      return new ResponseEntity<Workshop> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/workshop/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<Void> deleteWorkshop (@PathVariable long id) {
    Optional<Workshop> result = workrepo.findById (id);
    if (result.isPresent ()) {
      Workshop work = result.get ();
      workrepo.delete (work);
      return new ResponseEntity<> (HttpStatus.OK);
    } else {
      return new ResponseEntity<> (HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/workshop/{id}/vote/{user}/{interest}")
  @ResponseBody
  public ResponseEntity<Iterable<Workshop>> toggleVoteForWorkshop (@PathVariable ("id") long id,
                                                                   @PathVariable ("user") String user,
                                                                   @PathVariable ("interest") int interest) {
    user = user.toLowerCase ();
    Optional<Workshop> result = workrepo.findById (id);
    if (result.isPresent ()) {
      Workshop work = result.get ();
      if (work.hasVote (user)) {
        voterepo.delete (work.getVote (user));
        work.removeVote (work.getVote (user));
      } else {
        Vote vote = voterepo.save (new Vote ().setName (user).setInterest (interest));
        work.addVote (vote);
      }
      workrepo.save (work);
      return new ResponseEntity<> (workrepo.findAll (), HttpStatus.OK);
    } else {
      return new ResponseEntity<> (workrepo.findAll (), HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping (value = "/rest/votes")
  @ResponseBody
  public ResponseEntity<Iterable<Vote>> getAllVotes () {
    return new ResponseEntity<> (voterepo.findAll (), HttpStatus.OK);
  }

  @RequestMapping (value = "/rest/workshop/{id}/vote/clear")
  @ResponseBody
  public ResponseEntity<Workshop> toggleVoteForWorkshop (@PathVariable ("id") long id) {
    Optional<Workshop> result = workrepo.findById (id);
    if (result.isPresent ()) {
      Workshop work = result.get ();
      work.getVotes ().forEach (vote -> {
        voterepo.delete (vote);
      });
      work.clearVotes ();
      work = workrepo.save (work);
      return new ResponseEntity<> (work, HttpStatus.OK);
    } else {
      return new ResponseEntity<> (HttpStatus.NOT_FOUND);
    }
  }

//  @RequestMapping (value = "/rest/algorythmus")
//  @ResponseBody
//  public void starteAlgorythmus () {
//    Iterable<Workshop> workshops2 = workrepo.findAll ();
//    Iterable<User> users2 = userrepo.findAll ();
//    Workshop[] workshops = Iterables.asArray (Workshop.class, workshops2);
//    User[] users = Iterables.asArray (User.class, users2);
//    algorythmus = new Algorythmus (workshops, users, status);
//    Workshop[] zeitplan = algorythmus.findeLoesung ();
//    for (int i = 0; i < zeitplan.length; i++) {
//      workrepo.save (zeitplan[i]);
//    }
//  }

}

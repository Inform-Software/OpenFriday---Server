package com.syncrotess.openfriday.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Workshop {

  @Id
  @GeneratedValue
  private Long       id;                            // The ID of the Workshop
  private List<Vote> votes    = new ArrayList<> (); // Votes the workshop received
  private String     creator;                       // The User who created the Workshop
  private String     topic;                         // The topic of the Workshop
  private String     name;                          // The name of the Workshop
  private String     description;                   // The description of the Workshop
  private boolean    selected = false;              // Used by Vue.js in the Webinterface

  public Long getId () {
    return id;
  }

  public String getCreator () {
    if (creator == null) {
      creator = "Unbekannt";
    }
    return creator;
  }

  public void setCreator (String creator) {
    this.creator = creator;
  }

  public String getTopic () {
    if (topic == null) {
      topic = "Sonstiges";
    }
    return topic;
  }

  public void setTopic (String topic) {
    this.topic = topic;
  }

  public String getDescription () {
    if (description == null) {
      description = "Keine Beschreibung vorhanden";
    }
    return description;
  }

  public void setDescription (String description) {
    this.description = description;
  }

  public void setId (Long id) {
    this.id = id;
  }

  public String getName () {
    if (name == null) {
      name = "Neuer Workshop";
    }
    return name;
  }

  public void setName (String name) {
    this.name = name;
  }

  private static Workshop dummy;

  public static Workshop getDummy () {
    if (dummy == null) { // Initialize if not done yet
      dummy = new Workshop ();
      dummy.setCreator ("User");
      dummy.setId (0l);
      dummy.setName ("Unbenannter Workshop");
      dummy.setTopic ("Sonstiges");
      // dummy.addVote (5l);
    }
    return dummy;
  }

  @Override
  public String toString () {
    JSONObject data = new JSONObject ();
    try {
      data.put ("id", id);
      data.put ("name", name);
      data.put ("topic", topic);
      data.put ("description", description);
      data.put ("creator", creator);
      // data.put ("votes", votes);
    } catch (Exception e) {
      e.printStackTrace ();
    }

    return data.toString ();
  }

  public List<Vote> getVotes () {
    return votes;
  }

  public void setVotes (List<Vote> votes) {
    this.votes = votes;
  }

  public void addVote (Vote vote) {
    votes.add (vote);
  }

  public void removeVote (Vote vote) {
    votes.remove (vote);
  }

  public void clearVotes () {
    setVotes (new ArrayList<> ());

  }

  public boolean hasVote (String username) {
    return votes.stream ().anyMatch (vote -> vote.getName ().equals (username));
  }

  public Vote getVote (String username) {
    Optional<Vote> vote = votes.stream ().filter (v -> v.getName ().equals (username)).findFirst ();
    return vote.isPresent () ? vote.get () : null;
  }

  public boolean isSelected () {
    return selected;
  }

  public void setSelected (boolean selected) {
    this.selected = selected;
  }

}

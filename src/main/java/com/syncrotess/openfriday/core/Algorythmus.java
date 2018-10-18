
//Due to restructuring of User and Workshop classes not functional

/*
 * package com.syncrotess.openfriday.core;
 *
 * import java.util.HashMap; import java.util.Stack;
 *
 * import com.syncrotess.openfriday.util.Status; import com.syncrotess.openfriday.util.User; import
 * com.syncrotess.openfriday.util.Workshop;
 *
 * public class Algorythmus {
 *
 * Workshop[] workshops; User[] users; Status status; Stack<Version> versions = new Stack<Algorythmus.Version> ();
 *
 * public Algorythmus (Workshop[] workshop, User[] user, Status status) { this.users = user; this.workshops = workshop;
 * this.status = status; }
 *
 * public Workshop[] findeLoesung () {
 *
 * filterWorkshops (); baueVersionen (); System.out.println (versions.size ()); Version version = findebesteVersion ();
 * return konfiguriereZeitplan (version); }
 *
 * private Workshop[] konfiguriereZeitplan (Version version) { Workshop[] works = new Workshop[9];
 *
 * version.Session2.keySet ().toArray (new Workshop[3]); version.Session3.keySet ().toArray (new Workshop[3]);
 *
 * for (int i = 0; i < 3; i++) { works[i] = version.Session1.keySet ().toArray (new Workshop[3])[i];
 * works[i].setSelected (true); works[i].setSession (1); if (works[i].getRaum () != null) { works[i].setRaum
 * (status.getRooms ()[i]); } works[i + 3] = version.Session2.keySet ().toArray (new Workshop[3])[i]; works[i +
 * 3].setSelected (true); works[i + 3].setSession (2); if (works[i + 3].getRaum () != null) { works[i + 3].setRaum
 * (status.getRooms ()[i]); } works[i + 6] = version.Session3.keySet ().toArray (new Workshop[3])[i]; works[i +
 * 6].setSelected (true); works[i + 6].setSession (3); if (works[i + 6].getRaum () != null) { works[i + 6].setRaum
 * (status.getRooms ()[i]); } } return works; }
 *
 * private Version findebesteVersion () { Version version = new Version ();; Version[] versio = versions.toArray (new
 * Version[versions.size ()]);
 *
 * for (int i = 0; i < versions.size (); i++) { versio[i].punkte = bewerteLösung (versio[i]); if (version.punkte <
 * versio[i].punkte) { version = versio[i]; }
 *
 * } return version; }
 *
 * public void filterWorkshops () { Stack<Workshop> geprueft = new Stack<> (); for (int i = 0; i < workshops.length;
 * i++) { if (!(workshops[i].getCountvotes () <= 1)) { geprueft.add (workshops[i]); } } workshops = new
 * Workshop[geprueft.size ()]; workshops = geprueft.toArray (workshops); }
 *
 * public void baueVersionen () { Version version; for (int i0 = 0; i0 < workshops.length; i0++) { version = new Version
 * (); do { if (workshops[i0].getNotime ()[1]) { version.Session1.put (workshops[i0], sucheStimmen (workshops[i0])); }
 * else if (!(workshops[i0].getNotime ()[1])) { i0++; if (workshops[i0].getNotime ()[1]) { version.Session1.put
 * (workshops[i0], sucheStimmen (workshops[i0])); } } } while (!(workshops[i0].getNotime ()[1])); for (int i1 = 0; i1 <
 * workshops.length; i1++) { if (i1 > 0) { version.Session1.remove (workshops[i1 - 1]); } do { if
 * (workshops[i1].getNotime ()[1] && !(version.Session1.containsKey (workshops[i1]))) { version.Session1.put
 * (workshops[i1], sucheStimmen (workshops[i1])); } else { i1++; if (workshops[i1].getNotime ()[1] &&
 * !(version.Session1.containsKey (workshops[i1]))) { version.Session1.put (workshops[i1], sucheStimmen
 * (workshops[i1])); } } } while (!(workshops[i1].getNotime ()[1]) || (version.Session1.containsKey (workshops[i1])) ||
 * (i1 == workshops.length - 1)); for (int i2 = 0; i2 < workshops.length; i2++) { if (i2 > 0) { version.Session1.remove
 * (workshops[i2 - 1]); } do { if (workshops[i2].getNotime ()[1] && !(version.Session1.containsKey (workshops[i2]))) {
 * version.Session1.put (workshops[i2], sucheStimmen (workshops[i2])); } else { i2++; if (workshops[i2].getNotime ()[1]
 * && !(version.Session1.containsKey (workshops[i2]))) { version.Session1.put (workshops[i2], sucheStimmen
 * (workshops[i2])); } } } while (!(workshops[i2].getNotime ()[1]) || (version.Session1.containsKey (workshops[i2])) ||
 * (i2 == workshops.length - 1)); for (int i3 = 0; i3 < workshops.length; i3++) { if (i3 > 0) { version.Session2.remove
 * (workshops[i3 - 1]); } do { if (workshops[i3].getNotime ()[1] && !(version.Session1.containsKey (workshops[i3]))) {
 * version.Session2.put (workshops[i3], sucheStimmen (workshops[i3])); } else { i3++; if (workshops[i3].getNotime ()[1]
 * && !(version.Session1.containsKey (workshops[i3]))) { version.Session2.put (workshops[i3], sucheStimmen
 * (workshops[i3])); } } } while (!(workshops[i3].getNotime ()[1]) || (version.Session1.containsKey (workshops[i3])) ||
 * (i3 == workshops.length - 1)); for (int i4 = 0; i4 < workshops.length; i4++) { if (i4 > 0) { version.Session2.remove
 * (workshops[i4 - 1]); } do { if (workshops[i4].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i4])) &&
 * !(version.Session2.containsKey (workshops[i4])))) { version.Session2.put (workshops[i4], sucheStimmen
 * (workshops[i4])); } else { i4++; if (workshops[i4].getNotime ()[1] && (!(version.Session1.containsKey
 * (workshops[i4])) && !(version.Session2.containsKey (workshops[i4])))) { version.Session2.put (workshops[i4],
 * sucheStimmen (workshops[i4])); } } } while (!(workshops[i4].getNotime ()[1]) || ((version.Session1.containsKey
 * (workshops[i4])) || (version.Session2.containsKey (workshops[i4]))) || (i4 == workshops.length - 1)); for (int i5 =
 * 0; i5 < workshops.length; i5++) { if (i5 > 0) { version.Session2.remove (workshops[i5 - 1]); } do { if
 * (workshops[i5].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i5])) && !(version.Session2.containsKey
 * (workshops[i5])))) { version.Session2.put (workshops[i5], sucheStimmen (workshops[i5])); } else { i5++; if
 * (workshops[i5].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i5])) && !(version.Session2.containsKey
 * (workshops[i5])))) { version.Session2.put (workshops[i5], sucheStimmen (workshops[i5])); } } } while
 * (!(workshops[i5].getNotime ()[1]) || ((version.Session1.containsKey (workshops[i5])) || (version.Session2.containsKey
 * (workshops[i5]))) || (i5 == workshops.length - 1)); for (int i6 = 0; i6 < workshops.length; i6++) { if (i6 > 0) {
 * version.Session3.remove (workshops[i6 - 1]); } do { if (workshops[i6].getNotime ()[1] &&
 * (!(version.Session1.containsKey (workshops[i6])) && !(version.Session2.containsKey (workshops[i6])))) {
 * version.Session3.put (workshops[i6], sucheStimmen (workshops[i6])); } else { i6++; if (workshops[i6].getNotime ()[1]
 * && (!(version.Session1.containsKey (workshops[i6])) && !(version.Session2.containsKey (workshops[i6])))) {
 * version.Session3.put (workshops[i6], sucheStimmen (workshops[i6])); } } } while (!(workshops[i6].getNotime ()[1]) ||
 * ((version.Session1.containsKey (workshops[i6])) || (version.Session2.containsKey (workshops[i6]))) || (i6 ==
 * workshops.length - 1)); for (int i7 = 0; i7 < workshops.length; i7++) { if (i7 > 0) { version.Session3.remove
 * (workshops[i7 - 1]); } do { if (workshops[i7].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i7])) &&
 * !(version.Session2.containsKey (workshops[i7])) && !(version.Session3.containsKey (workshops[i7])))) {
 * version.Session3.put (workshops[i7], sucheStimmen (workshops[i7])); } else { if (i7 < workshops.length - 1) { i7++;
 * if (workshops[i7].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i7])) &&
 * !(version.Session2.containsKey (workshops[i7])) && !(version.Session3.containsKey (workshops[i7])))) {
 * version.Session3.put (workshops[i7], sucheStimmen (workshops[i7])); } } else { break; } } } while
 * (!(workshops[i7].getNotime ()[1]) || ((version.Session1.containsKey (workshops[i7])) || (version.Session2.containsKey
 * (workshops[i7])) || (version.Session3.containsKey (workshops[i7]))) || (i7 == workshops.length - 1)); } for (int i8 =
 * 0; i8 < workshops.length; i8++) { if (i8 > 0) { version.Session3.remove (workshops[i8 - 1]); } do { if
 * (workshops[i8].getNotime ()[1] && (!(version.Session1.containsKey (workshops[i8])) && !(version.Session2.containsKey
 * (workshops[i8])) && !(version.Session3.containsKey (workshops[i8])))) { version.Session3.put (workshops[i8],
 * sucheStimmen (workshops[i8])); } else { if (i8 < workshops.length - 1) { i8++; if (workshops[i8].getNotime ()[1] &&
 * (!(version.Session1.containsKey (workshops[i8])) && !(version.Session2.containsKey (workshops[i8])) &&
 * !(version.Session3.containsKey (workshops[i8])))) { version.Session3.put (workshops[i8], sucheStimmen
 * (workshops[i8])); } } else { break; } } } while (!(workshops[i8].getNotime ()[1]) || ((version.Session1.containsKey
 * (workshops[i8])) || (version.Session2.containsKey (workshops[i8])) || (version.Session3.containsKey (workshops[i8])))
 * || (i8 == workshops.length - 1));
 *
 * Version version1 = new Version (version); versions.add (version1); System.out.println (version1);
 *
 * } } } } } } } } }
 *
 * public String[] sucheStimmen (Workshop workshop) { Stack<String> stack = new Stack<String> (); for (int i = 0; i <
 * users.length; i++) { if (users[i].getWorkshops () != null) { for (int x = 0; x < users[i].getWorkshops ().length;
 * x++) { if (users[i].getWorkshops ()[x] == workshop.getId ()) { stack.add (users[i].getName ()); } } } }
 *
 * String[] namen = new String[stack.size ()]; namen = stack.toArray (namen); return namen; }
 *
 * public int bewerteLösung (Version version) { int punkte = 0; String[] work1; String[] work2; String[] work3; punkte =
 * version.Session1.keySet ().toArray (new Workshop[3])[1].getCountvotes () + punkte; punkte = version.Session1.keySet
 * ().toArray (new Workshop[3])[2].getCountvotes () + punkte; punkte = version.Session1.keySet ().toArray (new
 * Workshop[3])[0].getCountvotes () + punkte; work1 = version.Session1.get (version.Session1.keySet ().toArray (new
 * Workshop[3])[0]); work2 = version.Session1.get (version.Session1.keySet ().toArray (new Workshop[3])[2]); work3 =
 * version.Session1.get (version.Session1.keySet ().toArray (new Workshop[3])[1]); punkte = punkte - stringvergleich
 * (work1, work2); punkte = punkte - stringvergleich (work2, work3); punkte = punkte - stringvergleich (work1, work3);
 * punkte = version.Session2.keySet ().toArray (new Workshop[3])[1].getCountvotes () + punkte; punkte =
 * version.Session2.keySet ().toArray (new Workshop[3])[2].getCountvotes () + punkte; punkte = version.Session2.keySet
 * ().toArray (new Workshop[3])[0].getCountvotes () + punkte; work1 = version.Session2.get (version.Session2.keySet
 * ().toArray (new Workshop[3])[0]); work2 = version.Session2.get (version.Session2.keySet ().toArray (new
 * Workshop[3])[2]); work3 = version.Session2.get (version.Session2.keySet ().toArray (new Workshop[3])[1]); punkte =
 * punkte - stringvergleich (work1, work2); punkte = punkte - stringvergleich (work2, work3); punkte = punkte -
 * stringvergleich (work1, work3); punkte = version.Session3.keySet ().toArray (new Workshop[3])[1].getCountvotes () +
 * punkte; punkte = version.Session3.keySet ().toArray (new Workshop[3])[2].getCountvotes () + punkte; punkte =
 * version.Session3.keySet ().toArray (new Workshop[3])[0].getCountvotes () + punkte; work1 = version.Session3.get
 * (version.Session3.keySet ().toArray (new Workshop[3])[1]); work2 = version.Session3.get (version.Session3.keySet
 * ().toArray (new Workshop[3])[2]); work3 = version.Session3.get (version.Session3.keySet ().toArray (new
 * Workshop[3])[0]); punkte = punkte - stringvergleich (work1, work2); punkte = punkte - stringvergleich (work2, work3);
 * punkte = punkte - stringvergleich (work1, work3); return punkte; }
 *
 * private int stringvergleich (String[] strg1, String[] strg2) { int x = 0; for (int i = 0; i < strg1.length; i++) {
 * for (int j = 0; j < strg2.length; j++) { if (strg1[i].equals (strg2[j])) { x = x + 1; } } } return x; }
 *
 * class Version {
 *
 * HashMap<Workshop, String[]> Session1 = new HashMap<Workshop, String[]> (); HashMap<Workshop, String[]> Session2 = new
 * HashMap<Workshop, String[]> (); HashMap<Workshop, String[]> Session3 = new HashMap<Workshop, String[]> (); int punkte
 * = 0;
 *
 * public Version (Version version) { Session1 = version.Session1; Session1 = version.Session1; Session1 =
 * version.Session1; punkte = version.punkte; }
 *
 * public Version () {
 *
 * } } }
 *
 */

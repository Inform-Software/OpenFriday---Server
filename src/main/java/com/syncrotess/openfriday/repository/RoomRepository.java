package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.nodes.Room;
import com.syncrotess.openfriday.nodes.Slot;

import java.io.*;
import java.util.*;

public class RoomRepository {
    private final String file;
    private HashMap<UUID, Room> repo;

    public RoomRepository(String pathToFile) {
        file = pathToFile;
    }

    private void loadFile() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            repo = (HashMap<UUID, Room>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            repo = new HashMap<>();
            saveFile();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
            os.writeObject(repo);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRoom(Room room) {
        loadFile();
        room.setId();
        repo.put(room.getId(), room);
        saveFile();
    }

    public void updateRoom(UUID id, Room room) {
        loadFile();
        repo.replace(id, room);
        saveFile();
    }

    public void deleteRoom(UUID id) {
        loadFile();
        repo.remove(id);
        saveFile();
    }

    public Collection<Room> getAllRooms() {
        loadFile();
        List<Room> list = new ArrayList<>(repo.values());
        list.sort(Comparator.comparing(Room::getName));
        return list;
    }
}

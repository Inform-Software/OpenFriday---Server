package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.nodes.Slot;

import java.io.*;
import java.util.*;

public class SlotRepository {

    private final String file;
    private HashMap<UUID, Slot> repo;

    public SlotRepository(String pathToFile) {
        file = pathToFile;
    }

    private void loadFile() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            repo = (HashMap<UUID, Slot>) is.readObject();
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

    public void addSlot(Slot slot) {
        loadFile();
        repo.put(slot.getId(), slot);
        saveFile();
    }

    public void updateSlot(UUID id, Slot slot) {
        loadFile();
        repo.replace(id, slot);
        saveFile();
    }

    public void deleteSlot(UUID id) {
        loadFile();
        repo.remove(id);
        saveFile();
    }

    public Slot findSlot(UUID id) {
        loadFile();
        return repo.get(id);
    }

    public boolean isEmpty() {
        loadFile();
        return repo.isEmpty();
    }

    public Collection<Slot> getAllSlots() {
        loadFile();
        List<Slot> list = new ArrayList<>(repo.values());
        list.sort(Comparator.comparing(Slot::getName));
        return list;
    }

}

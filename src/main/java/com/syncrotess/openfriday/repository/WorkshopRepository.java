package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.nodes.Slot;
import com.syncrotess.openfriday.nodes.Workshop;

import java.io.*;
import java.util.*;

public class WorkshopRepository {

    private final String file;
    private HashMap<UUID, Workshop> repo;

    public WorkshopRepository(String pathToFile) {
        file = pathToFile;
    }

    private void loadFile() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            repo = (HashMap<UUID, Workshop>) is.readObject();
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

    public void addWorkshop(Workshop workshop) {
        loadFile();
        workshop.setId();
        repo.put(workshop.getId(), workshop);
        saveFile();
    }

    public void updateWorkshop(UUID id, Workshop workshop) {
        loadFile();
        repo.replace(id, workshop);
        saveFile();
    }

    public void deleteWorkshop(UUID id) {
        loadFile();
        repo.remove(id);
        saveFile();
    }

    public Workshop findWorkshop(UUID id) {
        loadFile();
        return repo.get(id);
    }

    public boolean isEmpty() {
        loadFile();
        return repo.isEmpty();
    }

    public Collection<Workshop> getAllWorkshops() {
        loadFile();
        List<Workshop> list = new ArrayList<>(repo.values());
        list.sort(Comparator.comparing(Workshop::getCreator));
        return list;
    }

}

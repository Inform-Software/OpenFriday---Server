package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.nodes.Plan;

import java.io.*;
import java.time.LocalDateTime;

public class PlanRepository {
    private final String file;
    private Plan repo;

    public PlanRepository(String pathToFile) {
        file = pathToFile;
    }

    private void loadFile() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            repo = (Plan) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            repo = new Plan();
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

    public LocalDateTime savePlan(Plan plan) {
        plan.timestamp = LocalDateTime.now();
        repo = plan;
        saveFile();
        return plan.timestamp;
    }

    public Plan loadPlan() {
        loadFile();
        return repo;
    }
}

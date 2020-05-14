package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.nodes.User;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserRepository {
    private final String file;
    private HashMap<UUID, User> repo;

    public UserRepository(String pathToFile) {
        file = pathToFile;
    }

    private void loadFile() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            repo = (HashMap<UUID, User>) is.readObject();
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

    public void addUser(User user) {
        loadFile();
        user.setId();
        repo.put(user.getId(), user);
        saveFile();
    }

    public void updateUser(UUID id, User user) {
        loadFile();
        repo.replace(id, user);
        saveFile();
    }

    public void deleteUser(UUID id) {
        loadFile();
        repo.remove(id);
        saveFile();
    }

    public User findUser(UUID id) {
        loadFile();
        return repo.get(id);
    }

    // Very bad performance with many users in repo, use with care!
    public User findUserByName(String name) {
        loadFile();
        for(User user : repo.values()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public Collection<User> getAllUsers() {
        loadFile();
        return repo.values();
    }
}

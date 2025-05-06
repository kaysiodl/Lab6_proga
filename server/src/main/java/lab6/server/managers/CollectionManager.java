package lab6.server.managers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lab6.common.models.Person;
import lab6.common.utility.ZonedDateTimeAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class CollectionManager {
    private final Map<Integer, Person> collection = new LinkedHashMap<>();
    public final BackUpManager backUpManager = new BackUpManager(".backUp_file");
    private LocalDateTime lastInitTime;

    Path path;

    public CollectionManager(Path path) {
        this.path = path;
    }

    public void add(Person person){
        Integer id = collection.values().stream()
                .map(Person::getId).max(Integer::compareTo)
                .orElse(0) + 1;
        person.setId(id);
        if (person.getCreationDate() == null) {
            person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        person.validate();
        collection.put(id, person);
        setLastInitTime(LocalDateTime.now());
        try {
            makeBackUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("New element added to collection successfully. ");
    }

    public void add(Person person, Integer id) {
        person.setId(id);
        if (person.getCreationDate() == null) {
            person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        person.validate();
        collection.put(id, person);
        setLastInitTime(LocalDateTime.now());
        try {
            makeBackUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("New element added to collection successfully. ");
    }


    public void saveCollection() throws IOException {
        if (collection.isEmpty()) {
            System.out.println("Collection is empty. Nothing to save.");
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        String jsonCollection = gson.toJson(collection.values());

        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(jsonCollection);
        } catch (IOException e) {
            System.err.printf("Failed when trying to write to %s: %s%n", path.getFileName(), e.getMessage());
            throw e;
        } finally {
            backUpManager.deleteBackUpFile();
        }
    }

    public void loadCollection(Path path) throws NullPointerException {
        try {
            List<Person> personList = new DumpManager(path).jsonFileToList();
            if (personList == null || personList.isEmpty()) {
                throw new NullPointerException();
            }

            for (Person person : personList) {
                if (person == null) {
                    System.err.println("Warning: found null in list.");
                    continue;
                }

                for (Person person1 : personList) {
                    if (person1 == null) {
                        continue;
                    }

                    if (Objects.equals(person.getId(), person1.getId()) && !person.equals(person1)) {
                        Integer id = collection.values().stream()
                                .map(Person::getId)
                                .max(Integer::compareTo)
                                .orElse(0) + 1;
                        person1.setId(id);
                        this.add(person1, person1.getId());
                        saveCollection();
                        System.out.println("id of every person must be unique");
                    }
                }
                this.add(person, person.getId());
            }
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    public List<Integer> sort() {
        List<Integer> sortedList = new ArrayList<>(collection.keySet());
        Collections.sort(sortedList);
        return sortedList;
    }

    public Person getById(Integer id) {
        for (Person person : collection.values()) {
            if (Objects.equals(person.getId(), id)) return person;
        }
        return null;
    }

    public void removeById(Integer id) {
        try {
            collection.remove(id);
            makeBackUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAll() {
        try {
            collection.clear();
            makeBackUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeBackUp() throws IOException {
        try{
            backUpManager.deleteBackUpFile();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();
            String jsonCollection = gson.toJson(collection.values());

            try (FileWriter writer = new FileWriter(backUpManager.fileName())) {
                writer.write(jsonCollection);
            } catch (IOException e) {
                System.err.printf("Failed when trying to write to %s: %s%n", path.getFileName(), e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<Integer, Person> getCollection() {
        return collection;
    }

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public void setLastInitTime(LocalDateTime lastInitTime) {
        this.lastInitTime = lastInitTime;
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            return "Collection is empty.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, Person> entry : collection.entrySet()) {
            int key = entry.getKey();
            Person person = entry.getValue();
            stringBuilder.append(key)
                    .append(": ")
                    .append(person)
                    .append("\n");
        }

        return stringBuilder.toString().trim();
    }

}

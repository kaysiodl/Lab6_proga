package lab6.server.managers;

import lab6.common.utility.IOHandler;
import lab6.common.utility.ZonedDateTimeAdapter;
import com.google.gson.*;
import lab6.common.models.Person;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DumpManager implements IOHandler<JsonElement> {

    protected Path path;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .setPrettyPrinting()
            .create();


    protected BufferedInputStream reader;

    protected BufferedWriter writer;

    public DumpManager(Path path){
        this.path = path;
    }

    /**
     * Converts file json into a list of Person type
     *
     * @return list of person
     */
    public List<Person> jsonFileToList(){
        try {
            JsonElement jsonElement;
            List<Person> personList = new ArrayList<>();

            jsonElement = this.read();

            if (jsonElement == null || jsonElement.isJsonNull()) {
                throw new NullPointerException("File is empty or contains uncorrected data.");
            }

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement element : jsonArray.asList()) {
                    Person person = gson.fromJson(element, Person.class);
                    if (person.getCreationDate() == null)
                        person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
                    personList.add(person);
                }
            } else {
                Person person = gson.fromJson(jsonElement, Person.class);
                if (person.getCreationDate() == null) person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
                personList.add(person);
            }

            return personList;
        } catch (Exception e) {
            throw new NullPointerException("Failed loading collection from file: " + e.getMessage());
        }
    }

    /**
     * Reads collection from file
     *
     * @return JsonObject
     */

    @Override
    public JsonElement read() throws FileNotFoundException {
        reader = new BufferedInputStream(new FileInputStream(path.toFile()));
        StringBuilder stringBuilder = new StringBuilder();
        int nextChar;
        try {
            while ((nextChar = reader.read()) != -1) {
                stringBuilder.append((char) nextChar);
            }
        } catch (IOException e) {
            System.err.println("Can't read file: " + path);
            return null;
        }

        String jsonString = stringBuilder.toString();
        if (jsonString.trim().isEmpty()) {
            System.err.println("File is empty: " + path);
            return null;
        }

        try {
            return JsonParser.parseString(jsonString);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("data in file is not correct");
        }
    }


    /**
     * Writes collection to a file
     *
     */
    @Override
    public void write(String collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null.");
        }

        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        if (!Files.isWritable(path)) {
            System.err.printf("Cannot write to %s%n", path.getFileName());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(gson.toJson(collection));
        } catch (IOException e) {
            System.err.printf("Failed when trying to write to %s: %s%n", path.getFileName(), e.getMessage());
            throw e;
        }
    }


    @Override
    public void close() throws IOException {
        writer.close();
    }
}

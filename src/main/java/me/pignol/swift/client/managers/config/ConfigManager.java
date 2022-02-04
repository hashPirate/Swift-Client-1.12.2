package me.pignol.swift.client.managers.config;

import com.google.gson.*;
import me.pignol.swift.Swift;
import me.pignol.swift.api.util.EnumHelper;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.managers.CommandManager;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.modules.Module;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConfigManager {

    private final static ConfigManager INSTANCE = new ConfigManager();

    private final Executor executor = Executors.newCachedThreadPool();

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public static final String USER_HOME = System.getProperty("user.home");
    private static final String PATH = "Swift/";
    private static final String PATH_MODULES = "Swift/Modules/";
    private static final String PATH_FRIENDS = "Swift/Friends/Friends.json";
    public static final File DIRECTORY = new File(USER_HOME, "Swift");
    private static final JsonParser PARSER = new JsonParser();

    public void load() {
        loadModules();
        loadFriends();
        loadPrefix();
    }

    public void save() {
        saveModules();
        saveFriends();
        savePrefix();
    }

    private void savePrefix() {
        try {
            String name = PATH + "Prefix" + ".json";
            Path outputFile = Paths.get(name);

            if (!Files.exists(outputFile)) {
                Files.createFile(outputFile);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();
            object.addProperty("Prefix", CommandManager.getInstance().getPrefix());
            String json = gson.toJson(object);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPrefix() {
        try {
            String name = PATH + "Prefix" + ".json";
            Path path = Paths.get(name);
            if (!Files.exists(path)) {
                return;
            }

            InputStream stream = Files.newInputStream(path);

            try {
                JsonObject object = PARSER.parse(new InputStreamReader(stream)).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    CommandManager.getInstance().setPrefix(entry.getValue().getAsString());
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        try {
            Path path = Paths.get(PATH + "Friends" + ".json");
            if (!Files.exists(path)) {
                return;
            }

            InputStream stream = Files.newInputStream(path);

            try {
                loadFriends(PARSER.parse(new InputStreamReader(stream)).getAsJsonObject());
            } catch (IllegalStateException e) {
                e.printStackTrace();
                System.out.println("Man how");
            }

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFriends(JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String name = entry.getKey();
            UUID uuid = UUID.fromString(entry.getValue().getAsString());
            FriendManager.getInstance().addFriend(name, uuid);
        }
    }

    public void loadModules() {
        for (Module module : ModuleManager.getInstance().getModules()) {
            try {
                String name = PATH_MODULES + module.getName() + ".json";
                Path path = Paths.get(name);
                if (!Files.exists(path)) {
                    continue;
                }

                InputStream stream = Files.newInputStream(path);

                try {
                    loadObject(PARSER.parse(new InputStreamReader(stream)).getAsJsonObject(), module);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    loadObject(new JsonObject(), module);
                }

                stream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        ClickGUI.getInstance().initWindows();
    }

    public void loadObject(JsonObject directory, Module module) {
        for (Map.Entry<String, JsonElement> entry : directory.entrySet()) {
            switch (entry.getKey()) {
                case "Key":
                    module.setKey(entry.getValue().getAsInt());
                    continue;
                case "Enabled":
                    if (!(module.isEnabled() && entry.getValue().getAsBoolean()) && !(!module.isEnabled() && !entry.getValue().getAsBoolean()))
                        module.setEnabled(entry.getValue().getAsBoolean());
                    continue;
                case "Drawn":
                    module.setDrawn(entry.getValue().getAsBoolean());
                    continue;
            }

            for (Value val : module.getValues()) {
                try {
                    if (val.getName().equalsIgnoreCase(entry.getKey())) {
                        Object value = val.getValue();
                        if (value instanceof Boolean) {
                            val.setValue(entry.getValue().getAsBoolean());
                        } else if (value instanceof Number) {
                            if (value.getClass() == Float.class) {
                                val.setValue(entry.getValue().getAsFloat());
                            } else if (value.getClass() == Double.class) {
                                val.setValue(entry.getValue().getAsDouble());
                            } else if (value.getClass() == Integer.class) {
                                val.setValue(entry.getValue().getAsInt());
                            }
                        } else if (value instanceof String) {
                            val.setValue(entry.getValue().getAsString());
                        } else if (value instanceof Enum) {
                            val.setValue(EnumHelper.fromString((Enum<?>) value, entry.getValue().getAsString()));
                        }
                    }
                } catch (Exception ex) {
                    val.setValue(val.getDefaultValue());
                }
            }
        }
    }

    private void saveFriends()
    {
        try
        {
            String name = PATH + "Friends" + ".json";
            Path outputFile = Paths.get(name);

            if (!Files.exists(outputFile))
            {
                Files.createFile(outputFile);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            for (Map.Entry<String, UUID> entry : FriendManager.getInstance().getFriends().entrySet())
            {
                object.add(entry.getKey(), PARSER.parse(entry.getValue().toString()));
            }

            String json = gson.toJson(object);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
            writer.write(json);
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveModules() {
        for (Module module : ModuleManager.getInstance().getModules()) {
            try {
                String name = PATH_MODULES + module.getName() + ".json";
                Path outputFile = Paths.get(name);

                if (!Files.exists(outputFile)) {
                    Files.createFile(outputFile);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = new JsonObject();

                object.addProperty("Key", module.getKey());
                object.addProperty("Enabled", module.isEnabled());
                object.addProperty("Drawn", module.isDrawn());
                for (Value val : module.getValues()) {
                    object.addProperty(val.getName(), val.getValue().toString());
                }
                String json = gson.toJson(object);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
                writer.write(json);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(File delete) {
        if (!delete.delete()) {
            Swift.LOGGER.info("File couldn't be deleted.");
        }
    }

}

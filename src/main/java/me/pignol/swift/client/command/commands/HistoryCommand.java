package me.pignol.swift.client.command.commands;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.util.UUIDTypeAdapter;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.minecraft.advancements.AdvancementManager.GSON;

public class HistoryCommand extends Command {

    private static JsonParser PARSER = new JsonParser();

    public HistoryCommand() {
        super("History", new String[]{"h", "namehistory"});
    }

    @Override
    public void run(String[] args) {
        if (args.length <= 1) {
            return;
        }

        UUID uuid = getUUIDFromName(args[1]);
        if (uuid != null) {
            List<String> pastNames = getHistoryOfNames(uuid);
            if (pastNames == null) {
                ChatUtil.sendMessage("An error occured while trying to get UUID of player.");
                return;
            }
            StringBuilder names = new StringBuilder(ChatFormatting.BOLD + ChatFormatting.DARK_AQUA.toString());
            for (String pastName : pastNames) {
                try {
                    boolean isLast = pastName == pastNames.get(pastNames.size() - 1);
                    names
                            .append(pastName)
                            .append(isLast ? "." : ", ");
                } catch (Exception ex) {

                }
            }
            ChatUtil.sendMessage(names.toString());
        }
    }

    public static List<String> getHistoryOfNames(UUID id) {
        try {
            JsonArray array = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            Map<String, Long> map = new HashMap<>();
            for (JsonElement e : array) {
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                map.put(name, changedAt);
            }
            return Lists.newArrayList(sortByValue(map, true).keySet());
        } catch (Exception ignored) {
            return null;
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        if (descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static UUID getUUIDFromName(String name) {
        try {
            LookUpUUID process = new LookUpUUID(name);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return getResources(url, request, (JsonElement)null);
    }

    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(GSON.toJson(element));
                output.close();
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();

            while(scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }

            scanner.close();
            String json = builder.toString();
            return PARSER.parse(json);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static class LookUpUUID implements Runnable {

        private volatile UUID uuid;
        private final String name;

        public LookUpUUID(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            NetworkPlayerInfo profile;
            try {
                ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<NetworkPlayerInfo>(Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                assert profile != null;
                uuid = profile.getGameProfile().getId();
            } catch (Exception e) {
                profile = null;
            }

            if (profile == null) {
                String s = requestIDs("[\"" + name + "\"]");
                if (s != null && !s.isEmpty()) {
                    JsonElement element = new JsonParser().parse(s);
                    if (element.getAsJsonArray().size() != 0) {
                        try {
                            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            uuid = UUIDTypeAdapter.fromString(id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }


}

package me.pignol.swift.client.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.managers.lookup.LookUp;
import me.pignol.swift.client.managers.lookup.LookUpManager;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FriendManager {

    private static final FriendManager INSTANCE = new FriendManager();

    public static FriendManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<String, UUID> friends = new ConcurrentHashMap<>();

    public boolean isFriend(String name) {
        return ManageModule.INSTANCE.friends.getValue() && friends.containsKey(name);
    }

    public void addFriend(String name, UUID uuid)
    {
        friends.put(name, uuid);
    }

    public void addFriend(EntityPlayer player) {
        friends.put(player.getName(), player.getUniqueID());
    }

    public void addFriend(String name) {
        LookUpManager.getInstance().doLookUp(new LookUp(LookUp.Type.UUID, name)
        {
            @Override
            public void onSuccess()
            {
                friends.put(name, uuid);
                ChatUtil.sendMessage(ChatFormatting.GREEN + "Added " + name + " to friends.");
            }

            @Override
            public void onFailure()
            {
                ChatUtil.sendMessage(ChatFormatting.RED + "Something went wrong with the UUID lookup!");
            }
        });
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public Map<String, UUID> getFriends() {
        return friends;
    }


}

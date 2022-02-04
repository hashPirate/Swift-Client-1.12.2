package me.pignol.swift.client.command.commands;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.FriendManager;

import java.util.Iterator;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("Friend", new String[]{"f"});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            if (args.length == 1) {
                StringBuilder friends = new StringBuilder();
                friends.append("Your friends are: ");
                Iterator<String> iterator = FriendManager.getInstance().getFriends().keySet().iterator();
                while (iterator.hasNext()) {
                    friends.append(iterator.next());
                    if (!iterator.hasNext()) {
                        friends.append(".");
                    } else {
                        friends.append(", ");
                    }
                }
                ChatUtil.sendMessage(friends.toString());
            }
            return;
        }

        switch (args[1].toUpperCase()) {
            case "ADD":
                if (FriendManager.getInstance().isFriend(args[2])) {
                    ChatUtil.sendMessage("That player is a friend already.");
                } else {
                    FriendManager.getInstance().addFriend(args[2]);
                }
                break;
            case "DEL":
            case "REMOVE":
            case "DELETE":
                if (FriendManager.getInstance().isFriend(args[2])) {
                    FriendManager.getInstance().removeFriend(args[2]);
                    ChatUtil.sendMessage("Removed " + args[2] + " from friends.");
                } else {
                    ChatUtil.sendMessage("That user isnt a friend.");
                }
                break;
        }
    }

}

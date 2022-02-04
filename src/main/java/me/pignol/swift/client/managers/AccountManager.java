package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;

public class AccountManager implements Globals
{


    public static class Account
    {
        String name;
        String password;

        public Account(String name, String password)
        {
            this.name = name;
            this.password = password;
        }

        public String getName()
        {
            return name;
        }

        public String getPassword()
        {
            return password;
        }

    }

}

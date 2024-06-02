package com.jetoff.logic;

import java.net.NetPermission;
import java.net.PasswordAuthentication;
import java.time.LocalDate;

/**
 * Created by Alain on 27/04/2017.
 */
public class Individual
{
    static int ID_ATOM;
    public enum Gender { MALE, FEMALE };

    String Name;
    LocalDate DateBirth;
    Gender Sex;
    String Pseudo;
    String Password;
    PasswordAuthentication passAuthentifier;
    NetPermission netPermission;

    public int GetAge() { return 0; }
    public void GetInfo( int id_atom ) {}

    public Gender getGender()
    {
        return this.Sex;
    }
}

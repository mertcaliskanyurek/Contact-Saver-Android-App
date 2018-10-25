package com.mcaliskanyurek.contactsaver;

import com.mcaliskanyurek.TextFileHelper.ITextDataParser;

import java.util.ArrayList;

public final class ContactParser implements ITextDataParser<Contact> {

    @Override
    public ArrayList<Contact> parseData(ArrayList<String> lines) {
        ArrayList<Contact> contacts = new ArrayList<>();
        for (String line:lines) {
            String[] contactData = line.split(":");
            contacts.add(new Contact(contactData[0],contactData[1]));
        }
        return contacts;
    }
}

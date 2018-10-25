package com.mcaliskanyurek.contactsaver;

public final class Contact {

    private String Name;
    private String Number;

    public Contact(String name, String number) {
        Name = name;
        Number = number;
    }

    public String getNumber() {
        return Number;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return Name+":"+Number;
    }
}

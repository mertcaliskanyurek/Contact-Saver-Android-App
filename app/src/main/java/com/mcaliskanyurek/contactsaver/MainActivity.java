package com.mcaliskanyurek.contactsaver;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mcaliskanyurek.TextFileHelper.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends RuntimePermissionActivity {

    private static final int FILE_DIALOG_REQUEST_CODE=1;
    private static final int CONTACT_SAVE_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE=3;

    private boolean fileReadSuccess;
    private boolean permissionOK;

    private ArrayList<Contact> contacts;
    private FileHelper fileHelper;

    private int contactIndexToSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionOK=false;
        fileReadSuccess = false;

        contactIndexToSave = 0;
        izinIste(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},"Dosya Okumak İçin Gerekli",PERMISSION_REQUEST_CODE);
    }

    @Override
    public void izinVerildi(int requestCode) {
        if(requestCode==PERMISSION_REQUEST_CODE)
            permissionOK = true;
    }

    public void showFileDialog(View v)
    {
        if(!permissionOK)
        {
            Toast.makeText(this,R.string.err_permission,Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_DIALOG_REQUEST_CODE);
    }
    private void readNumbersFromTxt()
    {
        try {
            contacts = new ArrayList<>();
            contacts = fileHelper.readTextFile();
            fileReadSuccess = true;
            showContactsInListView(contacts);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,R.string.err_file_reading,Toast.LENGTH_SHORT).show();
        }

    }

    private void saveContact(Contact c)
    {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, c.getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE,c.getNumber());

        startActivityForResult(intent, CONTACT_SAVE_REQUEST_CODE);
    }

    private void showContactsInListView(ArrayList<Contact> contacts)
    {
        ListView listView = findViewById(R.id.list_view_contacts);
        ArrayList<String> list = new ArrayList<>();
        for (Contact c: contacts)
            list.add(c.getName() + " " +c.getNumber());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        listView.setAdapter(arrayAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_DIALOG_REQUEST_CODE:
                Uri fileUri = data.getData();
                final String docId = DocumentsContract.getDocumentId(fileUri);
                final String[] split = docId.split(":");
                File file;
                if(split[0].equals("primary"))
                    file = new File(Environment.getExternalStorageDirectory()+"/"+split[1]);
                else
                    file = new File(split[1]);

                fileHelper = new FileHelper(getApplicationContext(),file,new ContactParser());
                readNumbersFromTxt();
                break;

            case CONTACT_SAVE_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                    Toast.makeText(getApplicationContext(),R.string.info_save_canceled,Toast.LENGTH_SHORT).show();
                else {
                    contactIndexToSave++;
                    try {
                        saveContact(contacts.get(contactIndexToSave));
                    }
                    catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(),R.string.info_save_success,Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveContacts(View view) {
        if(!fileReadSuccess){
            Toast.makeText(this,R.string.err_file_not_load,Toast.LENGTH_SHORT).show();
            return;
        }
        saveContact(contacts.get(contactIndexToSave));
    }
}

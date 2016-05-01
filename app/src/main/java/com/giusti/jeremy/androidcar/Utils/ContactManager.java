package com.giusti.jeremy.androidcar.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;

import java.util.HashMap;

/**
 * Created by jgiusti on 28/10/2015.
 * allow action on contact list
 */
public class ContactManager extends ContentObserver {
    private Context mContext;
    private HashMap<String,String> mContactList = new HashMap<>();

    public ContactManager(Context context){
        super(null);
        this.mContext =context;
        context.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, this);
        loadContactList();
    }

    private void loadContactList() {
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mContactList.put(name.toUpperCase(), phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }

    public boolean contactExist(String name){
        return mContactList.containsKey(name.toUpperCase());
    }

    public String getNumberFromName(String name){
        return mContactList.get(name.toUpperCase());
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        System.out.println(" Calling onChange");
        mContactList.clear();
        loadContactList();
    }

}

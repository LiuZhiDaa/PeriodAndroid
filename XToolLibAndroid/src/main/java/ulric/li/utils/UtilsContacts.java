package ulric.li.utils;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;

public class UtilsContacts {
    public static boolean addContacts(Context context, String strName, String strPhoneNumber) {
        if (null == context || TextUtils.isEmpty(strName) || TextUtils.isEmpty(strPhoneNumber))
            return false;

        try {
            ArrayList<ContentProviderOperation> listOperation = new ArrayList<ContentProviderOperation>();

            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentProviderOperation cpoAccount = ContentProviderOperation
                    .newInsert(uri).withValue("account_name", null).build();
            listOperation.add(cpoAccount);

            uri = Uri.parse("content://com.android.contacts/data");
            ContentProviderOperation cpoName = ContentProviderOperation.newInsert(uri)
                    .withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/name")
                    .withValue("data2", strName)
                    .build();
            listOperation.add(cpoName);

            ContentProviderOperation cpoPhoneNumber = ContentProviderOperation.newInsert(uri)
                    .withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                    .withValue("data2", "2")
                    .withValue("data1", strPhoneNumber)
                    .build();
            listOperation.add(cpoPhoneNumber);

            context.getContentResolver().applyBatch("com.android.contacts", listOperation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean removeContacts(Context context, String strName) {
        if (null == context || TextUtils.isEmpty(strName))
            return false;

        try {
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{strName}, null);
            if (cursor.moveToFirst()) {
                context.getContentResolver().delete(uri, "display_name=?", new String[]{strName});
                uri = Uri.parse("content://com.android.contacts/data");
                context.getContentResolver().delete(uri, "raw_contact_id=?", new String[]{String.valueOf(cursor.getInt(0))});
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

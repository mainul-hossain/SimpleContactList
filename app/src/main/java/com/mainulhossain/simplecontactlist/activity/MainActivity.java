package com.mainulhossain.simplecontactlist.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mainulhossain.simplecontactlist.R;
import com.mainulhossain.simplecontactlist.adapter.ContactAdapter;
import com.mainulhossain.simplecontactlist.db.MyDBHelper;
import com.mainulhossain.simplecontactlist.listener.OnLoadMoreListener;
import com.mainulhossain.simplecontactlist.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnLoadMoreListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private TextView myName, myNumber;
    private RecyclerView recyclerView;
    private DividerItemDecoration dividerItemDecoration;

    private ContentResolver cr;
    private Cursor cur;
    private String contactName, contactNumber;

    private MyDBHelper myDBHelper;

    private List<Contact> mContactList = new ArrayList<>();
    private List<Contact> limitedList = new ArrayList<>();

    private ContactAdapter contactAdapter;

    private int lastVisibleItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myName = (TextView) findViewById(R.id.myName);
        myNumber = (TextView) findViewById(R.id.myNumber);
        //Set my details
        myName.setText(getString(R.string.my_name));
        myNumber.setText(getString(R.string.my_number));

        recyclerView = (RecyclerView) findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);//Set LinearLayout to RecyclerView
        recyclerView.setItemAnimator(new DefaultItemAnimator()); //

        //Create an instance of DividerItemDecoration
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration); //Set RecyclerView Item Divider

        //Initialise MyDBHelper
        myDBHelper = new MyDBHelper(this);

        //Check SDK version for Runtime Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestContactPermission();
        }
        else
        {
            //Method calling
            getAllContacts();
        }

        // Getting all contacts from database
        mContactList = myDBHelper.getContacts();

        //Looping for first 10 data
        for (int i = 0; i < 10; i++)
        {
            //Added those data to Limited ArrayList
            limitedList.add(mContactList.get(i));
        }

        // Create and instance of ContactAdapter and initialized
        contactAdapter = new ContactAdapter(limitedList);
        contactAdapter.setOnLoadMoreListener(this);// Set Listener
        recyclerView.setAdapter(contactAdapter); // Set adapter to RecyclerView
    }

    //Getting contacts from phone
    private void getAllContacts()
    {
        cr = getContentResolver();
        cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cur != null)
        {
            while (cur.moveToNext())
            {
                contactName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("Contact List", "Name: "+ contactName + "Number: "+ contactNumber);

                //Insert contacts to Database
                myDBHelper.addContacts( new Contact(contactName, contactNumber) );
            }
            //Close Cursor
            cur.close();
        }
    }

    @Override
    public void onLoadMore() {

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition(); //Get the last item position

        int end = mContactList.size() - lastVisibleItem;
        if (end < 10)
        {
            for (int i = lastVisibleItem; i < mContactList.size(); i++)
            {
                limitedList.add(mContactList.get(i));
            }
            //Pass Boolean value to this method
            contactAdapter.setWithFooter(true);
            Toast.makeText(this, R.string.empty_msg, Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (int i = lastVisibleItem; i < lastVisibleItem + 10; i++)
            {
                limitedList.add(mContactList.get(i));
            }
        }
        //Notify adapter that data has been added
        contactAdapter.notifyDataSetChanged();
    }

    private void requestContactPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
            {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                        });
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else
        {
            //Method calling
            getAllContacts();
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    //Method calling
                    getAllContacts();
                }
                else {

                    // permission denied
                    Toast.makeText(this, "No permission for contacts", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

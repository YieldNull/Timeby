package com.nectar.timeby.gui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.nectar.timeby.R;

public class AddFriendActivity extends ActionBarActivity {

    private ImageButton mAddFromContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mAddFromContact = (ImageButton) findViewById(R.id.imageButton_addfriend_mobilecommunication);
        mAddFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddFriendActivity.this, AddFriendsFromContact.class));
            }
        });
    }

}

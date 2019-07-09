package com.mosmallowz.helloworldtalk.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.fragment.FriendsListFragment;
import com.mosmallowz.helloworldtalk.fragment.GroupsListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    DatabaseReference myRef;
    FirebaseUser user;

    TextView nameProfile;
    ImageView imgProfile;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_friends:
                    setTitle("Friends");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_container, FriendsListFragment.newInstance())
                            .commit();
                    return true;

                case R.id.navigation_group:
                    setTitle("Groups");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_container, GroupsListFragment.newInstance())
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init(){
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friends);

        nameProfile = (TextView) findViewById(R.id.name_profile);
        imgProfile = (ImageView) findViewById(R.id.img_profile);

        if (user != null) {
            nameProfile.setText(user.getDisplayName());
        }


    }

}

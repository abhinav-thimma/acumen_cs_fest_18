package com.example.saiprasadgarimella.navigation;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class events extends Fragment {
    private FirebaseAuth mAuth;


    private ArrayList<Integer> logos;
    private ArrayList<String> titles;
    private ArrayList<String> prices;
    private ArrayList<String> teams;

    private CoordinatorLayout activity_events;


    private View view;

    /*private RelativeLayout activity_events;

    private Button paynow1,paylater1;
    private ImageView i;
    */
    public events() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_events, container, false);

        // paynow1=(Button)view.findViewById(R.id.paynow1);
        //paylater1=(Button)view.findViewById(R.id.paylater1);
        activity_events = (CoordinatorLayout) view.findViewById(R.id.activity_events);

        mAuth = FirebaseAuth.getInstance();

        logos = new ArrayList<>();
        titles = new ArrayList<>();
        prices = new ArrayList<>();
        teams = new ArrayList<>();

        logos.add(R.drawable.paper);
        /*logos.add(R.drawable.logo2);
        logos.add(R.drawable.logo3);
        logos.add(R.drawable.logo4);
        logos.add(R.drawable.logo5);
        logos.add(R.drawable.logo6);
        logos.add(R.drawable.logo7);
        logos.add(R.drawable.logo8);
        logos.add(R.drawable.logo9);
        logos.add(R.drawable.logo10);
        logos.add(R.drawable.logo11);
        logos.add(R.drawable.logo12);
        logos.add(R.drawable.logo13);
        logos.add(R.drawable.logo14);
        logos.add(R.drawable.logo15);
        logos.add(R.drawable.logo16);
        logos.add(R.drawable.logo17);
        logos.add(R.drawable.logo18);
        logos.add(R.drawable.logo19);
           */
        titles.add("fss");
        prices.add("fss");
        teams.add("fss");

       /* titles.add(getString(R.string.event2));
        prices.add(getString(R.string.cost2));
        teams.add(getString(R.string.team2));

        titles.add(getString(R.string.event3));
        prices.add(getString(R.string.cost3));
        teams.add(getString(R.string.team3));

        titles.add(getString(R.string.event4));
        prices.add(getString(R.string.cost4));
        teams.add(getString(R.string.team4));

        titles.add(getString(R.string.event5));
        prices.add(getString(R.string.cost5));
        teams.add(getString(R.string.team5));

        titles.add(getString(R.string.event6));
        prices.add(getString(R.string.cost6));
        teams.add(getString(R.string.team6));

        titles.add(getString(R.string.event7));
        prices.add(getString(R.string.cost7));
        teams.add(getString(R.string.team7));

        titles.add(getString(R.string.event8));
        prices.add(getString(R.string.cost8));
        teams.add(getString(R.string.team8));

        titles.add(getString(R.string.event9));
        prices.add(getString(R.string.cost9));
        teams.add(getString(R.string.team9));

        titles.add(getString(R.string.event10));
        prices.add(getString(R.string.cost10));
        teams.add(getString(R.string.team10));

        titles.add(getString(R.string.event11));
        prices.add(getString(R.string.cost11));
        teams.add(getString(R.string.team11));

        titles.add(getString(R.string.event12));
        prices.add(getString(R.string.cost12));
        teams.add(getString(R.string.team12));

        titles.add(getString(R.string.event13));
        prices.add(getString(R.string.cost13));
        teams.add(getString(R.string.team13));

        titles.add(getString(R.string.event14));
        prices.add(getString(R.string.cost14));
        teams.add(getString(R.string.team14));

        titles.add(getString(R.string.event15));
        prices.add(getString(R.string.cost15));
        teams.add(getString(R.string.team15));

        titles.add(getString(R.string.event16));
        prices.add(getString(R.string.cost16));
        teams.add(getString(R.string.team16));

        titles.add(getString(R.string.event17));
        prices.add(getString(R.string.cost17));
        teams.add(getString(R.string.team17));

        titles.add(getString(R.string.event18));
        prices.add(getString(R.string.cost18));
        teams.add(getString(R.string.team18));

        titles.add(getString(R.string.event19));
        prices.add(getString(R.string.cost19));
        teams.add(getString(R.string.team19));
        */
      /*  final ListView listView = (ListView) view.findViewById(R.id.listView);
        assert listView != null;
*/
        //EventsAdapter eventsAdapter = new EventsAdapter();
        //listView.setAdapter(eventsAdapter);


        return view;

    }
}
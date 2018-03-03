package com.example.saiprasadgarimella.navigation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WebChatFragment extends Fragment {


    View view;
    DatabaseReference mDatabase;
    String urlVal;
    WebView webView;


    public WebChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_web_chat, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        webView = view.findViewById(R.id.webView);


        mDatabase.child("webLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                urlVal = (String) dataSnapshot.getValue();


                //setting up the webview
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("https://csechatbot.azurewebsites.net/venus");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

}

package com.example.saiprasadgarimella.navigation;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import static android.app.Activity.RESULT_OK;


public class chat extends Fragment implements AIListener {



    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn;
    DatabaseReference ref;
    FirebaseRecyclerAdapter<ChatMessage,chat_rec> adapter;
    private EditText message;
    Boolean flagFab = true;
    View view;
    private AIService aiService;
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    boolean checkEverythingFlag=false;


    LocationManagerClass locManager = null;


    //suggestion buttons

    private Button eventBtn,sponsorsBtn,aboutBtn,navigationBtn,dateBtn,developerBtn,contactBtn;

    public chat(){}




    public void CheckPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,

            }, 99);
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {

            if (locManager == null)
                locManager = new LocationManagerClass(null, getContext());

            locManager.RequestLocationUpdates();
        }
    }



    public void checkLocation()
    {

        //checking if location is enabed for navigation

        final Context context = getContext();
        final int[] flag = {0};

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;



        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled ) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps


                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();

        }



        //end of check if navigation is enabled



    }


    boolean checkEverything()
    {
        CheckPermission();
        checkLocation();

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        if(checkEverythingFlag == false)
            checkEverythingFlag = checkEverything();


        view=inflater.inflate(R.layout.fragment_chat, container, false);
        editText=(EditText)view.findViewById(R.id.message);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        addBtn = (RelativeLayout)view.findViewById(R.id.addBtn);


        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);

        if(isNetworkAvailable()) {
            final AIConfiguration config = new AIConfiguration("10e5d7d612a140df947a8b6da097c530",
                    AIConfiguration.SupportedLanguages.English,
                    AIConfiguration.RecognitionEngine.System);

            aiService = AIService.getService(getContext(), config);
            aiService.setListener(this);

            aiDataService = new AIDataService(config);

            aiRequest = new AIRequest();


            //inking buttons to java
            eventBtn = (Button) view.findViewById(R.id.scrollEventBtn);
            navigationBtn = (Button) view.findViewById(R.id.scrollNavigationBtn);
            aboutBtn = (Button) view.findViewById(R.id.scrollAboutBtn);
            sponsorsBtn = (Button) view.findViewById(R.id.scrollSponsorsBtn);
            dateBtn = (Button) view.findViewById(R.id.scrollDateBtn);
            developerBtn = (Button) view.findViewById(R.id.scrollDevelopersBtn);
            contactBtn = (Button)view.findViewById(R.id.scrollContactBtn);

            //sending initial start messege to bot;
            //sendMessegeToBot("start");


            eventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("Events");
                }
            });


            sponsorsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("Sponsors");
                }
            });

            developerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("Developers");
                }
            });


            contactBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("Contact");
                }
            });

            aboutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("About");
                }
            });


            dateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessegeToBot("Date today");
                }
            });


            navigationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    checkLocation();
                    sendMessegeToBot("Navigate me!");
                }
            });


            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String message = editText.getText().toString().trim();


                    if (!message.equals("")) {

                        sendMessegeToBot(message);
                    } else {

                        promptSpeechInput();

                    }

                    editText.setText("");

                }
            });


            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ImageView fab_img = (ImageView) view.findViewById(R.id.fab_img);
                    Bitmap img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_send_black_24dp);
                    Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_mic_black_24dp);


                    if (s.toString().trim().length() != 0 && flagFab) {
                        ImageViewAnimatedChange(getActivity(), fab_img, img);
                        flagFab = false;

                    } else if (s.toString().trim().length() == 0) {
                        ImageViewAnimatedChange(getActivity(), fab_img, img1);
                        flagFab = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }


            });

            //Toast.makeText(getActivity(),ref.child("chat").toString(),Toast.LENGTH_LONG).show();
            adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(ChatMessage.class, R.layout.msglist, chat_rec.class, ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("chat")) {
                @Override
                protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {

                    if (model.getMsgUser().equals("user") ) {


                        if(!isNetworkAvailable())
                        {
                            Toast.makeText(getContext(),"Internet Connection Lost...",Toast.LENGTH_SHORT).show();
                        }

                        //user messege structuring


                        viewHolder.rightText.setText(model.getMsgText());

                        viewHolder.rightText.setVisibility(View.VISIBLE);


                        viewHolder.leftText.setVisibility(View.GONE);
                        viewHolder.leftImg.setVisibility(View.GONE);


                        //making all the right messege field invisible

                        viewHolder.ll.setVisibility(View.GONE);
                        viewHolder.ll1.setVisibility(View.GONE);
                        viewHolder.ll2.setVisibility(View.GONE);
                        viewHolder.ll3.setVisibility(View.GONE);
                        viewHolder.ll4.setVisibility(View.GONE);
                        viewHolder.ll5.setVisibility(View.GONE);
                        viewHolder.ll6.setVisibility(View.GONE);
                        viewHolder.ll7.setVisibility(View.GONE);
                        viewHolder.ll8.setVisibility(View.GONE);
                        viewHolder.ll9.setVisibility(View.GONE);
                        viewHolder.ll10.setVisibility(View.GONE);
                        viewHolder.ll11.setVisibility(View.GONE);
                        viewHolder.ll12.setVisibility(View.GONE);
                        viewHolder.ll13.setVisibility(View.GONE);
                        viewHolder.ll14.setVisibility(View.GONE);
                        viewHolder.ll15.setVisibility(View.GONE);
                        viewHolder.ll16.setVisibility(View.GONE);
                        viewHolder.ll17.setVisibility(View.GONE);
                        viewHolder.ll18.setVisibility(View.GONE);
                        viewHolder.ll19.setVisibility(View.GONE);
                        viewHolder.ll20.setVisibility(View.GONE);

                    } else {




                        //individual event posters

                        if (model.getMsgText().contains("[paper_presentation]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.paper);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[paper_presentation]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[poster_presentation]")) {

                            //poster presentation info
                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            Drawable res = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[poster_presentation]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }
                        else if (model.getMsgText().contains("[online_programming_contest]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[online_programming_contest]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }
                        else if (model.getMsgText().contains("[hackathon]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[hackathon]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[workshop]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[workshop]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[mock_interview]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[mock_interview]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }
                        else if (model.getMsgText().contains("[treasure_hunt]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[treasure_hunt]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[tag_team_coding]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[tag_team_coding]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }
                        else if (model.getMsgText().contains("[efficient_coding]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[efficient_coding]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[draw_the_code]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.drawthecode);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[draw_the_code]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[code_n_ladder]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.codenadders);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[code_n_ladder]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[tic_tac_toe]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.tictactoe);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[tic_tac_toe]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[snapit]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.snapit);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[snapit]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[word_magic]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.magicwords);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[word_magic]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[crypt_your_mind]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.cryptposter);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[crypt_your_mind]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[brick_the_code]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.brickthecode);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[brick_the_code]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }


                        else if (model.getMsgText().contains("[coding_quiz]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[coding_quiz]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[earn_your_time]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.earnyourtime1);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[earn_your_time]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[code_auction]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[code_auction]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[time_is_up]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.timesup);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[time_is_up]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        else if (model.getMsgText().contains("[crack_it]")) {

                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            //paper presentation info


                            Drawable res = getResources().getDrawable(R.drawable.crackit);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText().substring(0, model.getMsgText().indexOf("[crack_it]")));


                            //making all extra right messege field invisible

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }

                        //common events list

                        else if (model.getMsgText().contains("[events]")) {

                            //info about all the events
                            //setting up respective image and more info button
                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            Drawable res = getResources().getDrawable(R.drawable.paper);
                            viewHolder.leftImg.setImageDrawable(res);
                            viewHolder.ll.setVisibility(View.VISIBLE);
                            viewHolder.leftImg.setVisibility(View.VISIBLE);
                            viewHolder.leftText.setText("Paper Presentation\nRs:100/-(per person)");
                            viewHolder.leftBtn.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("paper presentation");
                                }
                            });


                            Drawable res1 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg1.setImageDrawable(res1);
                            viewHolder.leftImg1.setVisibility(View.VISIBLE);
                            viewHolder.leftText1.setText("Poster Presentation\nRs:100/-(per person)");
                            viewHolder.leftText1.setVisibility(View.VISIBLE);
                            viewHolder.ll1.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn1.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("poster presentation");
                                }
                            });


                            Drawable res2 = getResources().getDrawable(R.drawable.opc);
                            viewHolder.leftImg2.setImageDrawable(res2);
                            viewHolder.leftImg2.setVisibility(View.VISIBLE);
                            viewHolder.leftText2.setText("Online Programming contest\nRs:100/-(per person)");
                            viewHolder.leftText2.setVisibility(View.VISIBLE);
                            viewHolder.ll2.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn2.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Online programming contest");
                                }
                            });


                            Drawable res3 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg3.setImageDrawable(res3);
                            viewHolder.leftImg3.setVisibility(View.VISIBLE);
                            viewHolder.leftText3.setVisibility(View.VISIBLE);
                            viewHolder.leftText3.setText("Hackathon\nRs:100/-(per person)");
                            viewHolder.ll3.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn3.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("hackathon");
                                }
                            });


                            Drawable res4 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg4.setImageDrawable(res4);
                            viewHolder.leftImg4.setVisibility(View.VISIBLE);
                            viewHolder.leftText4.setVisibility(View.VISIBLE);
                            viewHolder.leftText4.setText("Workshop\nRs:100/-(per person)");
                            viewHolder.ll4.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn4.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("workshop");
                                }
                            });


                            Drawable res5 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg5.setImageDrawable(res5);
                            viewHolder.leftImg5.setVisibility(View.VISIBLE);
                            viewHolder.leftText5.setText("Mock Interview\nRs:100/-(per person)");
                            viewHolder.ll5.setVisibility(View.VISIBLE);
                            viewHolder.leftText5.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn5.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Mock Interview");
                                }
                            });


                            Drawable res6 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg6.setImageDrawable(res6);
                            viewHolder.leftImg6.setVisibility(View.VISIBLE);
                            viewHolder.leftText6.setVisibility(View.VISIBLE);
                            viewHolder.leftText6.setText("Treasure Hunt\nRs:100/-(per person)");
                            viewHolder.ll6.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn6.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Treasure Hunt");
                                }
                            });


                            Drawable res7 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg7.setImageDrawable(res7);
                            viewHolder.leftImg7.setVisibility(View.VISIBLE);
                            viewHolder.leftText7.setVisibility(View.VISIBLE);
                            viewHolder.leftText7.setText("Tag Team Coding\nRs:100/-(per person)");
                            viewHolder.ll7.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn7.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn7.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Tag Team Coding");
                                }
                            });


                            Drawable res8 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg8.setImageDrawable(res8);
                            viewHolder.leftImg8.setVisibility(View.VISIBLE);
                            viewHolder.leftText8.setVisibility(View.VISIBLE);
                            viewHolder.leftText8.setText("Efficient Coding\nRs:100/-(per person)");
                            viewHolder.ll8.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn8.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn8.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Efficient Coding");
                                }
                            });


                            Drawable res9 = getResources().getDrawable(R.drawable.drawthecode);
                            viewHolder.leftImg9.setImageDrawable(res9);
                            viewHolder.leftImg9.setVisibility(View.VISIBLE);
                            viewHolder.leftText9.setVisibility(View.VISIBLE);
                            viewHolder.leftText9.setText("Draw the Code\nRs:100/-(per person)");
                            viewHolder.ll9.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn9.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn9.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Draw The Code");
                                }
                            });


                            Drawable res10 = getResources().getDrawable(R.drawable.codenadders);
                            viewHolder.leftImg10.setImageDrawable(res10);
                            viewHolder.leftImg10.setVisibility(View.VISIBLE);
                            viewHolder.leftText10.setVisibility(View.VISIBLE);
                            viewHolder.leftText10.setText("CodeNLadder\nRs:100/-(per person)");
                            viewHolder.ll10.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn10.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn10.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("CodeNLadder");
                                }
                            });


                            Drawable res11 = getResources().getDrawable(R.drawable.tictactoe);
                            viewHolder.leftImg11.setImageDrawable(res11);
                            viewHolder.leftImg11.setVisibility(View.VISIBLE);
                            viewHolder.leftText11.setVisibility(View.VISIBLE);
                            viewHolder.leftText11.setText("Tic Tak Toe\nRs:100/-(per person)");
                            viewHolder.ll11.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn11.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn11.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Tic Tak Toe");
                                }
                            });


                            Drawable res12 = getResources().getDrawable(R.drawable.snapit);
                            viewHolder.leftImg12.setImageDrawable(res12);
                            viewHolder.leftImg12.setVisibility(View.VISIBLE);
                            viewHolder.leftText12.setVisibility(View.VISIBLE);
                            viewHolder.leftText12.setText("Snapit\nRs:100/-(per person)");
                            viewHolder.ll12.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn12.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn12.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Snapit");
                                }
                            });


                            Drawable res13 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg13.setImageDrawable(res13);
                            viewHolder.leftImg13.setVisibility(View.VISIBLE);
                            viewHolder.leftText13.setVisibility(View.VISIBLE);
                            viewHolder.leftText13.setText("Word Magic\nRs:100/-(per person)");
                            viewHolder.ll13.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn13.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn13.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Word Magic");
                                }
                            });


                            Drawable res14 = getResources().getDrawable(R.drawable.cryptposter);
                            viewHolder.leftImg14.setImageDrawable(res14);
                            viewHolder.leftImg14.setVisibility(View.VISIBLE);
                            viewHolder.leftText14.setVisibility(View.VISIBLE);
                            viewHolder.leftText14.setText("Crypt Your Mind\nRs:100/-(per person)");
                            viewHolder.ll14.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn14.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn14.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Crypt Your Mind");
                                }
                            });


                            Drawable res15 = getResources().getDrawable(R.drawable.brickthecode);
                            viewHolder.leftImg15.setImageDrawable(res15);
                            viewHolder.leftImg15.setVisibility(View.VISIBLE);
                            viewHolder.leftText15.setVisibility(View.VISIBLE);
                            viewHolder.leftText15.setText("Brick The Code\nRs:100/-(per person)");
                            viewHolder.ll15.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn15.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn15.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Brick The Code");
                                }
                            });


                            Drawable res16 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg16.setImageDrawable(res16);
                            viewHolder.leftImg16.setVisibility(View.VISIBLE);
                            viewHolder.leftText16.setVisibility(View.VISIBLE);
                            viewHolder.leftText16.setText("Coding Quiz\nRs:100/-(per person)");
                            viewHolder.ll16.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn16.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn16.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Coding Quiz");
                                }
                            });


                            Drawable res17 = getResources().getDrawable(R.drawable.earnyourtime1);
                            viewHolder.leftImg17.setImageDrawable(res17);
                            viewHolder.leftImg17.setVisibility(View.VISIBLE);
                            viewHolder.leftText17.setVisibility(View.VISIBLE);
                            viewHolder.leftText17.setText("Earn your Time\nRs:100/-(per person)");
                            viewHolder.ll17.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn17.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn17.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Earn Your Time");
                                }
                            });


                            Drawable res18 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg18.setImageDrawable(res18);
                            viewHolder.leftImg18.setVisibility(View.VISIBLE);
                            viewHolder.leftText18.setVisibility(View.VISIBLE);
                            viewHolder.leftText18.setText("Code Auction\nRs:100/-(per person)");
                            viewHolder.ll18.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn18.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn18.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Code Auction");
                                }
                            });


                            Drawable res19 = getResources().getDrawable(R.drawable.timesup);
                            viewHolder.leftImg19.setImageDrawable(res19);
                            viewHolder.leftImg19.setVisibility(View.VISIBLE);
                            viewHolder.leftText19.setVisibility(View.VISIBLE);
                            viewHolder.leftText19.setText("Time is up\nRs:100/-(per person)");
                            viewHolder.ll19.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn19.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn19.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Time is up");
                                }
                            });


                            Drawable res20 = getResources().getDrawable(R.drawable.crackit);
                            viewHolder.leftImg20.setImageDrawable(res20);
                            viewHolder.leftImg20.setVisibility(View.VISIBLE);
                            viewHolder.leftText20.setVisibility(View.VISIBLE);
                            viewHolder.leftText20.setText("Crack It\nRs:100/-(per person)");
                            viewHolder.ll20.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn20.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn20.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessegeToBot("Crack It");
                                }
                            });


                        } else if (model.getMsgText().contains("[developers]")) {


                            //developer info

                            viewHolder.ll.setVisibility(View.GONE);
                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.GONE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftImg.setVisibility(View.GONE);


                            Drawable res1 = getResources().getDrawable(R.drawable.sandy);
                            viewHolder.leftImg1.setImageDrawable(res1);
                            viewHolder.leftImg1.setVisibility(View.VISIBLE);
                            viewHolder.leftText1.setText("Sai Sandilya\n 9618272448");
                            viewHolder.leftText1.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn1.setVisibility(View.GONE);
                            viewHolder.ll1.setVisibility(View.VISIBLE);


                            Drawable res2 = getResources().getDrawable(R.drawable.abhi);
                            viewHolder.leftImg2.setImageDrawable(res2);
                            viewHolder.leftImg2.setVisibility(View.VISIBLE);
                            viewHolder.leftText2.setText("Thimma Abhinav Reddy\n 8897415899");
                            viewHolder.leftText2.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn2.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.VISIBLE);


                            Drawable res3 = getResources().getDrawable(R.drawable.chakradhar);
                            viewHolder.leftImg3.setImageDrawable(res3);
                            viewHolder.leftImg3.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn3.setVisibility(View.GONE);
                            viewHolder.leftText3.setText("Chakradhar\n 7893415264");
                            viewHolder.leftText3.setVisibility(View.VISIBLE);
                            viewHolder.ll3.setVisibility(View.VISIBLE);


                            Drawable res4 = getResources().getDrawable(R.drawable.manohar);
                            viewHolder.leftImg4.setImageDrawable(res4);
                            viewHolder.leftImg4.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn4.setVisibility(View.GONE);
                            viewHolder.leftText4.setVisibility(View.VISIBLE);
                            viewHolder.leftText4.setText("Bandha Manohar\n 9676085369");
                            viewHolder.ll4.setVisibility(View.VISIBLE);

                            Drawable res5 = getResources().getDrawable(R.drawable.shiva);
                            viewHolder.leftImg5.setImageDrawable(res5);
                            viewHolder.leftImg5.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn5.setVisibility(View.GONE);
                            viewHolder.leftText5.setText("Shiva Sai\n 8328553031");
                            viewHolder.leftText5.setVisibility(View.VISIBLE);
                            viewHolder.ll5.setVisibility(View.VISIBLE);

                            Drawable res6 = getResources().getDrawable(R.drawable.somesh);
                            viewHolder.leftImg6.setImageDrawable(res6);
                            viewHolder.leftImg6.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn6.setVisibility(View.GONE);
                            viewHolder.leftText6.setText("Somesh Thakur\n 7729061297");
                            viewHolder.leftText6.setVisibility(View.VISIBLE);
                            viewHolder.ll6.setVisibility(View.VISIBLE);


                            Drawable res7 = getResources().getDrawable(R.drawable.uttej);
                            viewHolder.leftImg7.setImageDrawable(res7);
                            viewHolder.leftImg7.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn7.setVisibility(View.GONE);
                            viewHolder.leftText7.setText("Uttej\n 9908095560");
                            viewHolder.leftText7.setVisibility(View.VISIBLE);
                            viewHolder.ll7.setVisibility(View.VISIBLE);


                            Drawable res8 = getResources().getDrawable(R.drawable.raj);
                            viewHolder.leftImg8.setImageDrawable(res8);viewHolder.leftBtn1.setVisibility(View.GONE);
                            viewHolder.leftImg8.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn8.setVisibility(View.GONE);
                            viewHolder.leftText8.setText("Raj\n 9705005121");
                            viewHolder.leftText8.setVisibility(View.VISIBLE);
                            viewHolder.ll8.setVisibility(View.VISIBLE);


                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        } else if (model.getMsgText().contains("[contact]")) {


                            //contact info about the acumen co pordinators



                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.GONE);
                            viewHolder.leftImg.setVisibility(View.GONE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.ll.setVisibility(View.GONE);


                            Drawable res1 = getResources().getDrawable(R.drawable.vip);
                            viewHolder.leftImg1.setImageDrawable(res1);
                            viewHolder.leftImg1.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn1.setVisibility(View.GONE);
                            viewHolder.leftText1.setText("Vipanchith Reddy\n 7416269757");
                            viewHolder.leftText1.setVisibility(View.VISIBLE);
                            viewHolder.ll1.setVisibility(View.VISIBLE);


                            Drawable res2 = getResources().getDrawable(R.drawable.saikrishna);
                            viewHolder.leftImg2.setImageDrawable(res2);
                            viewHolder.leftImg2.setVisibility(View.VISIBLE);
                            viewHolder.leftText2.setText("Sai Krishna\n 9030051070");
                            viewHolder.leftBtn2.setVisibility(View.GONE);
                            viewHolder.leftText2.setVisibility(View.VISIBLE);
                            viewHolder.ll2.setVisibility(View.VISIBLE);


                            Drawable res3 = getResources().getDrawable(R.drawable.manasa);
                            viewHolder.leftImg3.setImageDrawable(res3);
                            viewHolder.leftImg3.setVisibility(View.VISIBLE);
                            viewHolder.leftText3.setText("Manasa Varanasi\n 9908836686");
                            viewHolder.leftBtn3.setVisibility(View.GONE);
                            viewHolder.leftText3.setVisibility(View.VISIBLE);
                            viewHolder.ll3.setVisibility(View.VISIBLE);


                            Drawable res4 = getResources().getDrawable(R.drawable.hani);
                            viewHolder.leftImg4.setImageDrawable(res4);
                            viewHolder.leftImg4.setVisibility(View.VISIBLE);
                            viewHolder.leftText4.setText("Honey\n 9951152911");
                            viewHolder.leftBtn4.setVisibility(View.GONE);
                            viewHolder.leftText4.setVisibility(View.VISIBLE);
                            viewHolder.ll4.setVisibility(View.VISIBLE);

                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }
                        else if (model.getMsgText().contains("[sponsors]")) {


                            //contact info about the acumen co pordinators


                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.leftText.setVisibility(View.GONE);
                            viewHolder.leftImg.setVisibility(View.GONE);
                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.ll.setVisibility(View.GONE);


                            Drawable res1 = getResources().getDrawable(R.drawable.poster);
                            viewHolder.leftImg1.setImageDrawable(res1);
                            viewHolder.leftImg1.setVisibility(View.VISIBLE);
                            viewHolder.leftBtn1.setVisibility(View.GONE);
                            viewHolder.leftText1.setText("NewEdge");
                            viewHolder.leftText1.setVisibility(View.VISIBLE);
                            viewHolder.ll1.setVisibility(View.VISIBLE);
                        }

                        else {

                            viewHolder.leftBtn.setVisibility(View.GONE);
                            viewHolder.leftImg.setVisibility(View.GONE);
                            viewHolder.leftText.setText(model.getMsgText());
                            viewHolder.leftText.setVisibility(View.VISIBLE);


                            viewHolder.rightText.setVisibility(View.GONE);
                            viewHolder.ll.setVisibility(View.VISIBLE);

                            viewHolder.ll1.setVisibility(View.GONE);
                            viewHolder.ll2.setVisibility(View.GONE);
                            viewHolder.ll3.setVisibility(View.GONE);
                            viewHolder.ll4.setVisibility(View.GONE);
                            viewHolder.ll5.setVisibility(View.GONE);
                            viewHolder.ll6.setVisibility(View.GONE);
                            viewHolder.ll7.setVisibility(View.GONE);
                            viewHolder.ll8.setVisibility(View.GONE);
                            viewHolder.ll9.setVisibility(View.GONE);
                            viewHolder.ll10.setVisibility(View.GONE);
                            viewHolder.ll11.setVisibility(View.GONE);
                            viewHolder.ll12.setVisibility(View.GONE);
                            viewHolder.ll13.setVisibility(View.GONE);
                            viewHolder.ll14.setVisibility(View.GONE);
                            viewHolder.ll15.setVisibility(View.GONE);
                            viewHolder.ll16.setVisibility(View.GONE);
                            viewHolder.ll17.setVisibility(View.GONE);
                            viewHolder.ll18.setVisibility(View.GONE);
                            viewHolder.ll19.setVisibility(View.GONE);
                            viewHolder.ll20.setVisibility(View.GONE);

                        }


                    }
                }
            };

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);

                    int msgCount = adapter.getItemCount();
                    int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (msgCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        recyclerView.scrollToPosition(positionStart);

                    }

                }
            });

            recyclerView.setAdapter(adapter);
        }
        else{
            Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }


        return view;
    }


    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    @Override
    public void onResult(AIResponse result) {

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery = result.get(0);
                    editText.setText(userQuery);
                }
                break;
            }
        }
    }





    public void sendMessegeToBot(String message)
    {

        ChatMessage chatMessage = new ChatMessage(message, "user");





        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("chat").push().setValue(chatMessage);

        ref.child("msgCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = (long) dataSnapshot.getValue();
                ref.child("msgCount").setValue(count+1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        aiRequest.setQuery(message);
        new AsyncTask<AIRequest,Void,AIResponse>(){

            @Override
            protected AIResponse doInBackground(AIRequest... aiRequests) {
                final AIRequest request = aiRequests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {

                    Result result = response.getResult();
                    String reply = result.getFulfillment().getSpeech();

                    ChatMessage chatMessage = new ChatMessage(reply, "bot");

                    if (chatMessage.getMsgText().startsWith("https://www.google.com/maps/dir/?api=1&destination=17.380337,78.382667&origin=")) {
                        checkLocation();
                        chatMessage.msgText = chatMessage.msgText.concat(locManager.mLocation.getLatitude() + "," + locManager.mLocation.getLongitude());
                        chatMessage.msgText = String.valueOf(Html.fromHtml(chatMessage.msgText));


                        String uri = "https://www.google.com/maps/dir/?api=1&destination=17.380337,78.382667&origin=" + locManager.mLocation.getLatitude() + "," + locManager.mLocation.getLongitude();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);

                    }



                    ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("chat").push().setValue(chatMessage);
                }
            }
        }.execute(aiRequest);


    }
}

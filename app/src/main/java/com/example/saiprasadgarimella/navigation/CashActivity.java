package com.example.saiprasadgarimella.navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);

        mAuth = FirebaseAuth.getInstance();

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("Scan the Payment QR");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null) {
            if (intentResult.getContents() != null) {
                final DatabaseReference qrReference = FirebaseDatabase.getInstance().getReference().child("Registrations");

                qrReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(intentResult.getContents())) {
                            final DatabaseReference mRefQR = qrReference.child(intentResult.getContents());
                            DatabaseReference activeReference = mRefQR.child("active");
                            activeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue(Integer.class) == 1) {
                                        UserInfo userInfo = mAuth.getCurrentUser();
                                        final String mail = userInfo.getEmail();

                                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("events");
                                        final SharedPreferences eventData = getSharedPreferences("eventData", MODE_PRIVATE);

                                        DatabaseReference event = userReference.child(eventData.getInt("EID", 0) + "");
                                        event.setValue(1);

                                        /*final DatabaseReference eventReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://acumen-cs.firebaseio.com/Events/" + eventData.getInt("EID", 0));
                                        eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""))) {
                                                    final DatabaseReference mailReference = eventReference.child(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""));

                                                    mailReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            mailReference.setValue(dataSnapshot.getValue(Integer.class) + 1);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                } else {
                                                    DatabaseReference mailReference = eventReference.child(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""));
                                                    mailReference.setValue(1);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                         */
                                        final DatabaseReference mRefQREvent = mRefQR.child("Events").child(eventData.getInt("EID", 0) + "");
                                        mRefQREvent.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""))) {
                                                    final DatabaseReference mailReference = mRefQREvent.child(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""));

                                                    mailReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            mailReference.setValue(dataSnapshot.getValue(Integer.class) + 1);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                } else {
                                                    DatabaseReference mailReference = mRefQREvent.child(mail.replace(".", "").replace("$", "").replace("[", "").replace("]", "").replace("#", "").replace("/", ""));
                                                    mailReference.setValue(1);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        startActivity(new Intent(CashActivity.this, RegisterSuccessActivity.class));
                                        finish();
                                    } else
                                        Toast.makeText(CashActivity.this, "Your QR has been deactivated", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else
                            Toast.makeText(CashActivity.this, "Invalid QR", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}


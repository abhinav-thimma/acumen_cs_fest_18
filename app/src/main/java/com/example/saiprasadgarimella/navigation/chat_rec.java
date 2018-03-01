package com.example.saiprasadgarimella.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sai prasad garimella on 09-02-2018.
 */

public class chat_rec extends RecyclerView.ViewHolder {

    TextView  rightText,leftText,leftText1,leftText2,leftText3,leftText4,leftText5,leftText6,leftText7,leftText8,leftText9,leftText10,leftText11,leftText12,leftText13,leftText14,leftText15,leftText16,leftText17,leftText18,leftText19,leftText20;
    ImageView leftImg,leftImg1,leftImg2,leftImg3,leftImg4,leftImg5,leftImg6,leftImg7,leftImg8,leftImg9,leftImg10,leftImg11,leftImg12,leftImg13,leftImg14,leftImg15,leftImg16,leftImg17,leftImg18,leftImg19,leftImg20;
    LinearLayout ll,ll1,ll2,ll3,ll4,ll5,ll6,ll7,ll8,ll9,ll10,ll11,ll12,ll13,ll14,ll15,ll16,ll17,ll18,ll19,ll20;
    Button leftBtn,leftBtn1,leftBtn2,leftBtn3,leftBtn4,leftBtn5,leftBtn6,leftBtn7,leftBtn8,leftBtn9,leftBtn10,leftBtn11,leftBtn12,leftBtn13,leftBtn14,leftBtn15,leftBtn16,leftBtn17,leftBtn18,leftBtn19,leftBtn20;

    public chat_rec(View itemView) {
        super(itemView);

        rightText = (TextView) itemView.findViewById(R.id.rightText);


        //inking all text views in left messege
        leftText = (TextView) itemView.findViewById(R.id.leftText);
        leftText1 = (TextView) itemView.findViewById(R.id.leftText1);
        leftText2 = (TextView) itemView.findViewById(R.id.leftText2);
        leftText3 = (TextView) itemView.findViewById(R.id.leftText3);
        leftText4 = (TextView) itemView.findViewById(R.id.leftText4);
        leftText5 = (TextView) itemView.findViewById(R.id.leftText5);
        leftText6 = (TextView) itemView.findViewById(R.id.leftText6);
        leftText7 = (TextView) itemView.findViewById(R.id.leftText7);
        leftText8 = (TextView) itemView.findViewById(R.id.leftText8);
        leftText9 = (TextView) itemView.findViewById(R.id.leftText9);
        leftText10 = (TextView) itemView.findViewById(R.id.leftText10);
        leftText11 = (TextView) itemView.findViewById(R.id.leftText11);
        leftText12 = (TextView) itemView.findViewById(R.id.leftText12);
        leftText13 = (TextView) itemView.findViewById(R.id.leftText13);
        leftText14 = (TextView) itemView.findViewById(R.id.leftText14);
        leftText15 = (TextView) itemView.findViewById(R.id.leftText15);
        leftText16 = (TextView) itemView.findViewById(R.id.leftText16);
        leftText17 = (TextView) itemView.findViewById(R.id.leftText17);
        leftText18 = (TextView) itemView.findViewById(R.id.leftText18);
        leftText19 = (TextView) itemView.findViewById(R.id.leftText19);
        leftText20 = (TextView) itemView.findViewById(R.id.leftText20);




        //linking all images in left messege
        leftImg = (ImageView)itemView.findViewById(R.id.leftImg);
        leftImg1 = (ImageView)itemView.findViewById(R.id.leftImg1);
        leftImg2 = (ImageView)itemView.findViewById(R.id.leftImg2);
        leftImg3 = (ImageView)itemView.findViewById(R.id.leftImg3);
        leftImg4 = (ImageView)itemView.findViewById(R.id.leftImg4);
        leftImg5 = (ImageView)itemView.findViewById(R.id.leftImg5);
        leftImg6 = (ImageView)itemView.findViewById(R.id.leftImg6);
        leftImg7 = (ImageView)itemView.findViewById(R.id.leftImg7);
        leftImg8 = (ImageView)itemView.findViewById(R.id.leftImg8);
        leftImg9 = (ImageView)itemView.findViewById(R.id.leftImg9);
        leftImg10 = (ImageView)itemView.findViewById(R.id.leftImg10);
        leftImg11 = (ImageView)itemView.findViewById(R.id.leftImg11);
        leftImg12 = (ImageView)itemView.findViewById(R.id.leftImg12);
        leftImg13 = (ImageView)itemView.findViewById(R.id.leftImg13);
        leftImg14 = (ImageView)itemView.findViewById(R.id.leftImg14);
        leftImg15 = (ImageView)itemView.findViewById(R.id.leftImg15);
        leftImg16 = (ImageView)itemView.findViewById(R.id.leftImg16);
        leftImg17 = (ImageView)itemView.findViewById(R.id.leftImg17);
        leftImg18 = (ImageView)itemView.findViewById(R.id.leftImg18);
        leftImg19 = (ImageView)itemView.findViewById(R.id.leftImg19);
        leftImg20 = (ImageView)itemView.findViewById(R.id.leftImg20);


        //linking linear layouts
        ll =(LinearLayout)itemView.findViewById(R.id.ll);
        ll1 =(LinearLayout)itemView.findViewById(R.id.ll1);
        ll2 =(LinearLayout)itemView.findViewById(R.id.ll2);
        ll3 =(LinearLayout)itemView.findViewById(R.id.ll3);
        ll4 =(LinearLayout)itemView.findViewById(R.id.ll4);
        ll5 =(LinearLayout)itemView.findViewById(R.id.ll5);
        ll6 =(LinearLayout)itemView.findViewById(R.id.ll6);
        ll7 =(LinearLayout)itemView.findViewById(R.id.ll7);
        ll8 =(LinearLayout)itemView.findViewById(R.id.ll8);
        ll9 =(LinearLayout)itemView.findViewById(R.id.ll9);
        ll10 =(LinearLayout)itemView.findViewById(R.id.ll10);
        ll11=(LinearLayout)itemView.findViewById(R.id.ll11);
        ll12 =(LinearLayout)itemView.findViewById(R.id.ll12);
        ll13 =(LinearLayout)itemView.findViewById(R.id.ll13);
        ll14 =(LinearLayout)itemView.findViewById(R.id.ll14);
        ll15 =(LinearLayout)itemView.findViewById(R.id.ll15);
        ll16 =(LinearLayout)itemView.findViewById(R.id.ll16);
        ll17 =(LinearLayout)itemView.findViewById(R.id.ll17);
        ll18 =(LinearLayout)itemView.findViewById(R.id.ll18);
        ll19 =(LinearLayout)itemView.findViewById(R.id.ll19);
        ll20 =(LinearLayout)itemView.findViewById(R.id.ll20);




        //linking buttons
        leftBtn = (Button)itemView.findViewById(R.id.leftBtn);
        leftBtn1 = (Button)itemView.findViewById(R.id.leftBtn1);
        leftBtn2 = (Button)itemView.findViewById(R.id.leftBtn2);
        leftBtn3 = (Button)itemView.findViewById(R.id.leftBtn3);
        leftBtn4 = (Button)itemView.findViewById(R.id.leftBtn4);
        leftBtn5 = (Button)itemView.findViewById(R.id.leftBtn5);
        leftBtn6 = (Button)itemView.findViewById(R.id.leftBtn6);
        leftBtn7 = (Button)itemView.findViewById(R.id.leftBtn7);
        leftBtn8 = (Button)itemView.findViewById(R.id.leftBtn8);
        leftBtn9 = (Button)itemView.findViewById(R.id.leftBtn9);
        leftBtn10 = (Button)itemView.findViewById(R.id.leftBtn10);
        leftBtn11 = (Button)itemView.findViewById(R.id.leftBtn11);
        leftBtn12 = (Button)itemView.findViewById(R.id.leftBtn12);
        leftBtn13 = (Button)itemView.findViewById(R.id.leftBtn13);
        leftBtn14 = (Button)itemView.findViewById(R.id.leftBtn14);
        leftBtn15 = (Button)itemView.findViewById(R.id.leftBtn15);
        leftBtn16 = (Button)itemView.findViewById(R.id.leftBtn16);
        leftBtn17 = (Button)itemView.findViewById(R.id.leftBtn17);
        leftBtn18 = (Button)itemView.findViewById(R.id.leftBtn18);
        leftBtn19 = (Button)itemView.findViewById(R.id.leftBtn19);
        leftBtn20 = (Button)itemView.findViewById(R.id.leftBtn20);

    }
}

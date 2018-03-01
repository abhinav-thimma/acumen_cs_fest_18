package com.example.saiprasadgarimella.navigation;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRFragment extends Fragment {


    //size of qr code
    public final static int QRcodeWidth = 1000 ;
    private ImageView myImage;


    View view;
    public QRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        view=inflater.inflate(R.layout.fragment_qr, container, false);


        myImage = (ImageView) view.findViewById(R.id.QR);


        File imgFile = new  File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getApplicationContext().getPackageName()
                + "/Files/QR_Acumen.jpg");

        if(imgFile.exists()){


            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

           myImage.setImageBitmap(myBitmap);

        }
        else
        {

            ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("QR processing");
            pd.setMessage("Generating QR...");
            pd.setCancelable(false);
            pd.show();



            try {
                TextToImageEncode();
            } catch (WriterException e) {
                e.printStackTrace();
            }


            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            myImage.setImageBitmap(myBitmap);

            pd.cancel();

        }




        return view;
    }

    //the function creates a qr for uid of each user and returns bitmap
    void TextToImageEncode() throws WriterException {


        String Value = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();




        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return ;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.qrBlack):getResources().getColor(R.color.colorWhite);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 1000, 0, 0, bitMatrixWidth, bitMatrixHeight);

        //myImage.setImageBitmap(bitmap);

        storeImage(bitmap);

        return ;
    }







    //this function stores the given bitmap onto local storage in phone
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {

        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(),"File not found",Toast.LENGTH_LONG);
        } catch (IOException e) {
            Toast.makeText(getContext(),"unable to create image file",Toast.LENGTH_LONG);
        }
    }




    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name

        File mediaFile;
        String mImageName="QR_Acumen.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + "/" + mImageName);
        return mediaFile;
    }


}

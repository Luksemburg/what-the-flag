package com.code.foo.whattheflag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.code.foo.whattheflag.data.FlagsArraysContract.AppendFlag;
import com.code.foo.whattheflag.data.FlagsDBHelper;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity {


    final int hesh = 12;
    final int hesh2 = 12;
    final int bitOut = 9;
    double [][] w = new double[hesh*hesh2*3][bitOut];
    double [] sigma = new double[bitOut];


    private FlagsDBHelper mDbHelper;

    final String FILENAME = "Poland";
    final String LOG_TAG = "myLogs";
    final String CODE = "111001100";


    Bitmap bitmap;
    char [] array;

    private static final int CAMERA_REQUEST = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new FlagsDBHelper(this);
      //  setContentView(new DrawView(this));
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.picture);

        Start();
    }



 //   class Start {

        //Paint paint;

        //Rect rectSrc;
        //Rect rectDst;
       // Matrix matrix;


        public void Start() {
        //public DrawView(Context context) {
            //super(context);
            //paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poland);
/*
            String info =
                    String.format("Info: size = %s x %s, bytes = %s (%s), config = %s",
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            bitmap.getByteCount(),
                            bitmap.getRowBytes(),
                            bitmap.getConfig());
            Log.d("log", info);

            matrix = new Matrix();
            matrix.postRotate(45);
            matrix.postScale(0.1f, 0.1f);
            matrix.postTranslate(100, 100);

            rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            rectDst = new Rect(200, 550, 600, 700);
*/

            //array = get_web(bitmap);


            //create_database(array);

            parseDB();

        }



        public void parseDB(){

            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String query = "SELECT " + AppendFlag.COLUMN_ARRAY + ", "
                    + AppendFlag.CODE + " FROM " + AppendFlag.TABLE_NAME;

            Cursor cursor = db.rawQuery(query, null);

            while(cursor.moveToNext()){
                String arr = clearing(cursor.getString(cursor.getColumnIndex(AppendFlag.COLUMN_ARRAY)));
                String code = cursor.getString(cursor.getColumnIndex(AppendFlag.CODE));

                neuroLearn(arr, code);

              //  Log.i(LOG_TAG, "code: " + code + "array: " + arr);
            }

            cursor.close();
        }


        public void resRawSQLquery(String in){

            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String query = "SELECT " + AppendFlag.COLUMN_COUNTRY + " FROM " + AppendFlag.TABLE_NAME + " WHERE " + AppendFlag.CODE + " LIKE '" + in + "'";

            Cursor cursor = db.rawQuery(query, null);
            String fin = "";
            while(cursor.moveToNext()){
                fin = cursor.getString(cursor.getColumnIndex(AppendFlag.COLUMN_COUNTRY));
                Log.i(LOG_TAG, "country " + fin);
            }

            Toast toast = Toast
                    .makeText(this, fin, Toast.LENGTH_SHORT);
            toast.show();

            cursor.close();
        }

        public void neuroLearn(String in, String out){

            char [] inArr = in.toCharArray();
            char [] outArr = out.toCharArray();

            for (int j = 0; j < out.length(); j++)
                for (int i = 0; i < in.length(); i++) {
                    w[i][j] += (2 * Character.getNumericValue(outArr[j]) - 1) * (2 * Character.getNumericValue(inArr[i]) - 1);

                    //Log.i(LOG_TAG, "w " + i + j + " = " + w[i][j]);
                }


            for (int j = 0; j < out.length(); j++) {
                for (int i = 0; i < in.length(); i++)
                    sigma[j] += w[i][j];

                sigma[j] /= 2;

                //Log.i(LOG_TAG, "sigma: " + j + " = " + sigma[j]);
            }
        }



        public String neuroCheck(String in){

            String out = "";
            double [] net = new double[bitOut];

            char [] inArr = in.toCharArray();

            for (int i = 0; i < bitOut; i++){
                for (int j = 0; j < in.length(); j++)
                    net[i] += Character.getNumericValue(inArr[j])*w[j][i];

                net[i] -= sigma[i];
            }


            for (int i = 0; i < bitOut; i++) {
                if (net[i] >= 0) out += '1';
                else out += '0';
            }

            //for (int i = 0; i < bitOut; i++)
            //    net[i] = 0;

            return out;
        }


        public String clearing(String in){

            String out = "";
            char [] ch = in.toCharArray();

            for(int i = 0; i<in.length(); i++)
            {
                if(ch[i] == '1' || ch[i] == '0') out += ch[i];
            }

            return out;
        }


        public void create_database(char [] arr){

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(AppendFlag.COLUMN_COUNTRY, FILENAME);
            values.put(AppendFlag.CODE, CODE);
            values.put(AppendFlag.COLUMN_ARRAY, Arrays.toString(arr));

            long newRowId = db.insert(AppendFlag.TABLE_NAME, null, values);
        }




        public char[] get_web (Bitmap pic) {

            int min = 16;
            int R, G, B;
            //R = G = B = 0;
            int pixelColor;

            double[] arr = new double[hesh * hesh2 * 3];
            char[] res = new char[hesh * hesh2 * 3];

            pic = Bitmap.createScaledBitmap(pic, hesh, hesh2, false);
            imageView.setImageBitmap(pic);

            int width = pic.getWidth();
            int height = pic.getHeight();

            //Log.d(LOG_TAG, "size: " + width + "x" + height);


            int n = 0;
            double average = 0;
               for(int i = 0; i < height; i++)
                 for(int j = 0; j < width; j++, n++)
     //       int i = 0;
     //       int j = 0;

      //      while (i < height){
       //         while (j < width)
                {
                    pixelColor = pic.getPixel(j, i);
                    R = Color.red(pixelColor);
                    arr[n] = R;
                    average += R;
                }

            average /= (hesh*hesh2);

            for(int k = 0; k < hesh*hesh2; k++){
                if(arr[k] > average*0.9 && arr[k] > 64)    res[k] = '1';
                else    res[k] = '0';
            }

            Log.d(LOG_TAG, "raw arr: " + Arrays.toString(arr));
            Log.d(LOG_TAG, "average R: " + average);

            average = 0;


            for(int i = 0; i < height; i++)
                for(int j = 0; j < width; j++, n++)
                {
                    pixelColor = pic.getPixel(j,i);
                    G = Color.green(pixelColor);
                    arr[n] = G;
                    average += G;
                }

            Log.d(LOG_TAG, "raw arr: " + Arrays.toString(arr));

            average /= (hesh*hesh2);

            for(int i = hesh*hesh2; i < 2*hesh*hesh2; i++){
                if(arr[i] > average*0.9 && arr[i] > 64)    res[i] = '1';
                else    res[i] = '0';
            }

            Log.d(LOG_TAG, "average G: " + average);
            average = 0;

            for(int i = 0; i < height; i++)
                for(int j = 0; j < width; j++, n++)
                {
                    pixelColor = pic.getPixel(j,i);
                    B = Color.blue(pixelColor);
                    arr[n] = B;
                    average +=B;
                }

            average /= (hesh*hesh2);

            for(int i = 2*hesh*hesh2; i < 3*hesh*hesh2; i++){
                if(arr[i] > average*0.9 && arr[i] > 64)    res[i] = '1';
                else    res[i] = '0';
            }


            Log.d(LOG_TAG, "bits: " + Arrays.toString(res));
            Log.d(LOG_TAG, "raw arr: " + Arrays.toString(arr));
            Log.d(LOG_TAG, "average B: " + average);
            return res;
        }



            /*
            int n = 0;

            int x = width/14;
            int y = height/14;
            while (x < width) {
                while (y < height && n < 588) {


                    pixelColor = pic.getPixel(x, y);

                    R = Color.red(pixelColor)*1.4;
                    G = Color.green(pixelColor)*1.4;
                    B = Color.blue(pixelColor)*1.4;

                    arr[n] = intToFourBit(R);
                    arr[n+1] = intToFourBit(G);
                    arr[n+2] = intToFourBit(B);



                    //Log.d(LOG_TAG, "y " + String.valueOf(y));
                    y += height/16;
                    n += 3;
                }

                y = height/14;
                //Log.d(LOG_TAG, "x " + String.valueOf(x));
                x += width/16;


                }



/*
        public String intToFourBit (double a){



            if (a <= 50)return "0000";
            if (a <= 100)return "0001";
            if (a <= 150)return "0011";
            if (a <= 200)return "0111";*/


/*
            if (a <= 16)return "0000";
            if (a <= 32)return "0001";
            if (a <= 48)return "0010";
            if (a <= 64)return "0011";
            if (a <= 80)return "0100";
            if (a <= 96)return "0101";
            if (a <= 112)return "0110";
            if (a <= 128)return "0111";

            if (a <= 144)return "1000";
            if (a <= 160)return "1001";
            if (a <= 176)return "1010";
            if (a <= 192)return "1011";
            if (a <= 208)return "1100";
            if (a <= 224)return "1101";
            if (a <= 240)return "1110";
            return "1111";
        }*/


    public void onClick(View view){

     /*   String errorMessage = "Click! Suka";
        Toast toast = Toast
                .makeText(this, errorMessage, Toast.LENGTH_SHORT);
        toast.show();
        */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (intent.resolveActivity(getPackageManager()) != null) {
          //startActivityForResult(intent, CAMERA_REQUEST);
        //}

        startActivityForResult(intent, CAMERA_REQUEST);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.italy);
        //String res = neuroCheck(clearing(Arrays.toString(get_web(bitmap))));
       // Log.d(LOG_TAG, "x = " + res);
       // resRawSQLquery(res);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "Click");
        Log.d(LOG_TAG, String.valueOf(requestCode));
        Log.d(LOG_TAG, String.valueOf(resultCode));

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){

            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnailBitmap);

            String res = neuroCheck(clearing(Arrays.toString(get_web(thumbnailBitmap))));
            Log.d(LOG_TAG, "x = " + res);
            resRawSQLquery(res);
        }
    }

    /*

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawARGB(80, 255, 255, 255);
            //canvas.drawBitmap(bitmap, 10, 10, paint);
            canvas.drawBitmap(bitmap, matrix, paint);
            canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);

        }   */

  //  }

}

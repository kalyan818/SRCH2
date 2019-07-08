package com.example.srch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private EditText textView;
    private ImageView imageView,refresh;
    private SearchView searchView;
    private FirebaseAuth mAuth;
    String text="";
    Bitmap bitmap,mBitmapSampled;
    FirebaseVisionImage image = null;
    private ProgressDialog progressDialog;
    private static final int SELECTED_PICTURE=1;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users/");


        refresh = (ImageView)findViewById(R.id.refresh);
        searchView = (SearchView)findViewById(R.id.search);
        btn = (Button)findViewById(R.id.chooseimage);
        textView = (EditText) findViewById(R.id.txt);
        imageView = (ImageView)findViewById(R.id.img);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if touch outside or on it the loading dialog box should not disapper
                progressDialog.setMessage("Loading.......");
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File directory = new File(root+"/ImageToText");
                        File[] files = directory.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            File file = new File(directory +"/"+ files[i].getName());
                            Uri iop = Uri.fromFile(file);

                            Log.d("DATABASE OPERATION",iop.toString());
                            try {
                                if ( file.toString().endsWith(".jpg") || file.toString().endsWith(".jpeg") ) {
                                    Update1(iop);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();

                    }
                };

                thread.start();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                /*DatabaseOperations db =  new DatabaseOperations(ctx);
                db.putInformation(db,s,s);
                Toast.makeText(ctx,"working",Toast.LENGTH_LONG).show();*/
                DatabaseOperations db =  new DatabaseOperations(ctx);
                Cursor cr = db.getInformation(db);
                cr.moveToFirst();

                do {
                    Pattern pattern = Pattern.compile(s);
                    Matcher matcher = pattern.matcher(cr.getString(1));
                    boolean found = false;
                    while (matcher.find()) {
                        text = cr.getString(0);
                        Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();
                        found = true;
                    }
                }while (cr.moveToNext());


                /*try {
                    firebaseUserSearch(s);
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }*/
                return false;
            }

            private void firebaseUserSearch(String s) {
                try {
                    Query firebaseSearchQuery = myRef.orderByChild("image").startAt("\uf8ff"+ s + "\uf8ff");
                    firebaseSearchQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Toast.makeText(MainActivity.this,snapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                                }
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public boolean onQueryTextChange(String s) {

                pass(s);

                return false;
            }

            private void pass(String s) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,SELECTED_PICTURE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTED_PICTURE)
        {
            switch (requestCode){
                case SELECTED_PICTURE:
                    if (resultCode==RESULT_OK){
                        Uri uri = data.getData();
                        String[]projection = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(uri, projection, null,null,null);
                        cursor.moveToFirst();


                        int columnIndex=cursor.getColumnIndex(projection[0]);
                        String filepath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap yourselectedimage = BitmapFactory.decodeFile(filepath);
                        Drawable d = new BitmapDrawable(yourselectedimage);

                        imageView.setImageDrawable(d);
                        Update(yourselectedimage);
                    }
                    break;
                default:
                    break;
            }
        }
    }
   /* ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Upload upload = snapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };*/
    private void Update(Bitmap yourselectedimage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(yourselectedimage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                // ...
                                String text = firebaseVisionText.getText();
                                textView.setText(text);
                                Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();


                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        textView.setText(e.getMessage());
                                        Toast.makeText(MainActivity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                                    }
                                });

    }
    private void Update1(Uri uri) throws IOException {
        final String ip = uri.toString();
        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        if (bitmap != null) {
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    // ...
                                    String text = firebaseVisionText.getText();
                                    DatabaseOperations db = new DatabaseOperations(ctx);
                                    db.putInformation(db, ip, text);
                                    bitmap = null;
                                    // Toast.makeText(MainActivity.this,ip + "uploaded",Toast.LENGTH_LONG).show();


                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            textView.setText(e.getMessage());
                                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });

        }
    }
}

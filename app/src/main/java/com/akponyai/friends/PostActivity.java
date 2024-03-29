package com.akponyai.friends;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button SelectPostImage;
    private Button postbutton;
    private EditText postdescription;

    private Uri postUri;
    private String description;

    private StorageReference postReference;
    private String saveCurrentDate, saveCurrentTime, postRandomName;

    private static final int file_pick=1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        postReference = FirebaseStorage.getInstance().getReference();

        SelectPostImage=(Button)findViewById(R.id.picturebutton);
        postbutton=(Button)findViewById(R.id.postbutton);
        postdescription=(EditText)findViewById(R.id.post_textView);

        toolbar=(Toolbar) findViewById(R.id.update_post_page_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Create Post");

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
        }


        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OpenFiles();
                new MaterialFilePicker()
                        .withActivity(PostActivity.this)
                        .withRequestCode(1000)
                      //  .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                      //  .withFilterDirectories(false) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });


        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ValidatePost();
            }
        });
    }



    private void ValidatePost() {
        String description = postdescription.getText().toString();

        if(postUri==null && description==null){
            Toast.makeText(this,"Add informtation to post", Toast.LENGTH_SHORT).show();
        }

        else{
            StoringFileToFirebaseStorage();
        }
    }

    private void StoringFileToFirebaseStorage() {
        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        Calendar callForTime=Calendar.getInstance();
        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(callForTime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        StorageReference filePath= postReference.child("Files").child(postUri.getLastPathSegment()+postRandomName + ".pdf");

        filePath.putFile(postUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    Toast.makeText(PostActivity.this,"File uploaded to Firebase Storage",Toast.LENGTH_SHORT).show();
                }

                else{
                    String message=task.getException().getMessage();
                    Toast.makeText(PostActivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data!=null) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            postUri = data.getData();
            // Do anything with file
            Toast.makeText(this,"Uploading"+filePath,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1001:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                }
        }


    private void OpenFiles() {
        Intent fileIntent=new Intent();
        fileIntent.setAction(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("file");
        startActivityForResult(fileIntent,file_pick);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==android.R.id.home){
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainActivityintent = new Intent(PostActivity.this, ProfileActivity.class);
        startActivity(mainActivityintent);
    }
}


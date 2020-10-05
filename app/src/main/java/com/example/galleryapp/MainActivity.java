package com.example.galleryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private static final int REQUEST_PERMISSIONS=1234;
    private static final String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_COUNT=2;

   private boolean arePermissionDenied(){
       for(int i=0;i<PERMISSION_COUNT;i++){
           if(checkSelfPermission(PERMISSIONS[i] )!= PackageManager.PERMISSION_GRANTED){
              return true;
           }
       }
       return false;
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSIONS && grantResults.length>0){
            if(arePermissionDenied()){
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE ))).clearApplicationUserData();
                    recreate();
                }
            }
        }
    private List<String> fileslist;
    private void addimagesFrom(String dirPath){
        final File imageDir =new File(dirPath);
                    final File[] files=imageDir.listFiles();
                    final int filescount = files.length;
                    for(int i=0;i<filescount;i++) {
                        final String path = files[i].getAbsolutePath();
                        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
                            fileslist.add(path);

                        }
                    }
    }
     



private boolean isGalleryInitialized;
    private int selectedfileindex;

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        //Initialize our App
        if (!isGalleryInitialized) {
            fileslist=new ArrayList<>();
            addimagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            addimagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)));
            addimagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

            final ListView listView = findViewById(R.id.listView);
            final  GalleryAdapter galleryAdapter=new GalleryAdapter();

            galleryAdapter.setData(fileslist);
            listView.setAdapter(galleryAdapter);

            final TextView imageName=findViewById(R.id.imagename);
            final LinearLayout topBar=findViewById(R.id.topbar);
            final Button deletebutton=findViewById(R.id.deletebutton);

           listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   selectedfileindex=position;
                   imageName.setVisibility(View.VISIBLE);
                   imageName.setText(fileslist.get(position).substring(fileslist.get(position).lastIndexOf('/')+1));
                  topBar.setVisibility(View.VISIBLE);
                   return false;


               }

           });

            isGalleryInitialized = true;
        }
    }
    final class GalleryAdapter extends BaseAdapter {
           private List<String> data =new ArrayList<>();
           void setData(List<String> data) {
               if(this.data.size()>0){
                   data.clear();
               }
               this.data.addAll(data);
               notifyDataSetChanged();
           }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
               final ImageView imageView;
               if(convertView==null){
                   imageView= (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent, false);
               }else{
                   imageView= (ImageView)convertView;
               }
                Glide.with( MainActivity.this).load(data.get(position)).centerCrop().into(imageView);
                return imageView;
            }
        }
    }

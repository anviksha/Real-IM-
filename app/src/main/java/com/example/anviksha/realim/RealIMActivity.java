package com.example.anviksha.realim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RealIMActivity extends ActionBarActivity {

    private EditText messageText;
    private String userId;
    private String userName;
    private ArrayAdapter<Message> mAdapter;
    private static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageText = (EditText)findViewById(R.id.message);
        userId = ParseUser.getCurrentUser().getObjectId();
        userName = LoginActivity.userName;

        ListView chatList = (ListView) findViewById(R.id.chatList);
        ArrayList<Message> mMessages = new ArrayList<Message>();
        mAdapter = new ChatListAdapter(RealIMActivity.this, userId, mMessages);
        chatList.setAdapter(mAdapter);
        receiveMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_camera) {
            startCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ParseUser.getCurrentUser() != null)
            ParseUser.logOut();
    }

    public void sendMessage(View view) {
        String data = messageText.getText().toString();
        if(data == "" || data.length() == 0)
            return;
        Message message = new Message();
        message.setBody(data);
        message.setUserId(userId);
        message.setUserName(userName);
        message.saveInBackground();
        mAdapter.add(message);
        mAdapter.notifyDataSetChanged();
        messageText.setText("");
    }

    private static Long lastRefreshTime;
    private static Long currentTime;
    private static int delay;
    private static int REFRESH_GAP = 400;

    private void receiveMessage() {
        lastRefreshTime = new Date().getTime();
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(50);
        query.orderByAscending("createdAt");
        query.whereNotEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
        Date d = new Date();
        Date latestDate = new Date(d.getTime() - REFRESH_GAP);
        if(!mAdapter.isEmpty()) {
            query.whereGreaterThan("createdAt",mAdapter.getItem(mAdapter.getCount() - 1).getCreatedAt());
        } else {
            query.whereGreaterThan("createdAt",latestDate);
        }
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null && messages.size()!=0) {
                    mAdapter.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                }
                if(ParseUser.getCurrentUser() != null) {
                    currentTime = new Date().getTime();
                    if(currentTime - lastRefreshTime > REFRESH_GAP)
                        delay = 0;
                    else
                        delay = (int)(REFRESH_GAP - (currentTime - lastRefreshTime));
                    delayCall(delay);
                }
            }
        });
    }

    private void delayCall(int delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        receiveMessage();
                    }
                },
                delay
        );
    }

    private Uri fileUri;
    private  File file;
    private void startCamera() {
        file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private ProgressDialog progressDialog;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(RealIMActivity.this);
                progressDialog.setTitle("Joining...");
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 300, 300);
                saveImageInParse(bitmap);
            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RealIMActivity.this, "User Cancelled the request ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageInParse(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        ParseFile pFile = new ParseFile("DocImage.jpg", stream.toByteArray());
        try {
            pFile.save();
            Message message = new Message();
            message.setBody("");
            message.setUserId(userId);
            message.setUserName(userName);
            message.setImageFile(pFile);
            mAdapter.add(message);
            mAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            message.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
            if (height > reqHeight)
            {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            }
            int expectedWidth = width / inSampleSize;
            if (expectedWidth > reqWidth)
            {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}

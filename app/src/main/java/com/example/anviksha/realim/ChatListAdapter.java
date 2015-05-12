package com.example.anviksha.realim;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by anviksha on 10/05/15.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {
    private String mUserId;
    private Context context;
    private static LayoutInflater inflater = null;
    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.mUserId = ParseUser.getCurrentUser().getObjectId();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = inflater.inflate(R.layout.chat_item, parent,false);
        final Message message = (Message)getItem(position);
        final boolean isMe = message.getUserId().equals(mUserId);
        TextView messageBody = (TextView)view.findViewById(R.id.messageBody);
        TextView userNameLeft = (TextView)view.findViewById(R.id.userNameLeft);
        TextView userNameRight = (TextView)view.findViewById(R.id.userNameRight);
        ImageView image = (ImageView)view.findViewById(R.id.imageView);
        messageBody.setText(message.getBody());

        ParseFile pf = message.getImageFile();
        if(pf != null) {
            final ImageView finalImage = image;
            pf.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, com.parse.ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        bmp.extractAlpha();
                        finalImage.setImageBitmap(bmp);
                    }
                }
            });
        } else {
            image.setImageBitmap(null);
        }

        if (isMe) {
            userNameRight.setVisibility(View.VISIBLE);
            userNameRight.setText(message.getUserName());
            userNameLeft.setVisibility(View.GONE);
            messageBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            messageBody.setPadding(0,0,50,0);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.RIGHT;
            layoutParams.leftMargin = 400;
            image.setLayoutParams(layoutParams);

        } else {
            userNameLeft.setVisibility(View.VISIBLE);
            userNameLeft.setText(message.getUserName());
            userNameRight.setVisibility(View.GONE);
            messageBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.LEFT;
            image.setLayoutParams(layoutParams);
        }
        return view;
    }

}
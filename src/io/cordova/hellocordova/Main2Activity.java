package io.cordova.hellocordova;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class Main2Activity extends Activity {
    private String BASE64_STR_CHARACTER="base64,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent=getIntent();
        String base64Str=intent.getStringExtra("imgData");
        base64Str=base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER)+this.BASE64_STR_CHARACTER.length(),base64Str.length());
//        TextView tView= (TextView) findViewById(R.id.textView);
//        tView.setText(base64Str);
        ImageView imageView= (ImageView) findViewById(R.id.showImg);
        byte bytes[] = Base64.decode(base64Str,Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds=false;
        Bitmap img= BitmapFactory.decodeByteArray(bytes,0,bytes.length,opts);
        imageView.setImageBitmap(img);
    }
}

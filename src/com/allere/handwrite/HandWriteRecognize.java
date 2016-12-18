package com.allere.handwrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.device.Device;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.cordova.hellocordova.Main2Activity;
import io.cordova.hellocordova.MainActivity;

/**
 * Created by pjl on 2016/12/17.
 */

public class HandWriteRecognize extends CordovaPlugin {
    private String BASE64_STR_CHARACTER="base64,";
    private Bitmap  getBitMapFromBase64Str(String base64Str){
        base64Str=base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER)+this.BASE64_STR_CHARACTER.length(),base64Str.length());
        byte bytes[] = Base64.decode(base64Str,Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds=false;
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,opts);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getHandWriteInfo".equals(action)) {
            if(args.length()!=0){
                String base64Str= (String) args.get(0);
                Bitmap img=this.getBitMapFromBase64Str(base64Str);
                JSONObject r = new JSONObject();
                r.put("status","true");
                callbackContext.success(r);
                Intent intent = new Intent();
                intent.setClass(this.cordova.getActivity(), Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imgData",base64Str);
                intent.putExtras(bundle);
                this.cordova.getActivity().startActivity(intent);
            }
        }
        else {
            return false;
        }
        return true;
    }
}

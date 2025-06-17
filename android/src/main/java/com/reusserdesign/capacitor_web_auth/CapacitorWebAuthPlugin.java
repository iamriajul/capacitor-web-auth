package com.reusserdesign.capacitor_web_auth;

import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResult;
import androidx.browser.customtabs.CustomTabsIntent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CapacitorWebAuth")
public class CapacitorWebAuthPlugin extends Plugin {

    private CapacitorWebAuth implementation = new CapacitorWebAuth();

    @PluginMethod
    public void login(PluginCall call) {
        String url = call.getString("url");
        String redirectScheme = call.getString("redirectScheme");
        
        if (url == null || url.isEmpty()) {
            call.reject("Must provide a URL");
            return;
        }
        
        if (redirectScheme == null || redirectScheme.isEmpty()) {
            call.reject("Must provide a redirect scheme");
            return;
        }
        
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            
            // Save the call for later
            bridge.saveCall(call);
            
            // Launch the browser
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            
            // Set up the callback for when the browser returns
            bridge.setActivityResultCallback(call, "handleOnActivityResult");
        } catch (Exception e) {
            call.reject("Failed to open browser: " + e.getMessage());
        }
    }
    
    @ActivityCallback
    private void handleOnActivityResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }
        
        Intent intent = result.getData();
        if (intent == null || intent.getData() == null) {
            call.reject("No data received from browser");
            return;
        }
        
        String url = intent.getData().toString();
        JSObject ret = new JSObject();
        ret.put("value", url);
        call.resolve(ret);
    }
}

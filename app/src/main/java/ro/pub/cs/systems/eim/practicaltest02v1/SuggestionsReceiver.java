package ro.pub.cs.systems.eim.practicaltest02v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class SuggestionsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log the action to see when the broadcast is received
        String action = intent.getAction();
        Log.d("SuggestionsReceiver", "Received broadcast with action: " + action);

        // Extract data from the Intent (e.g., suggestions list)
        ArrayList<String> suggestions = intent.getStringArrayListExtra("suggestions");

        if (suggestions != null) {
            // Handle the received suggestions (e.g., update the UI, process data)
            Log.d("SuggestionsReceiver", "Suggestions: " + suggestions.toString());
        }
    }
}

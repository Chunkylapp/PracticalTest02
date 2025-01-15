package ro.pub.cs.systems.eim.practicaltest02v1;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.BroadcastReceiver;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {


    //private static final Log LOG = Log.getInstance();
    private EditText inputEditText = null;
    private TextView outputTextView = null;
    private Button searchButton = null;
    private String myUrlString = "https://www.google.com/complete/search?client=chrome&q=";
    private BroadcastReceiver suggestionsReceiver;
    public static final String ACTION_SUGGESTIONS_RECEIVED = "com.yourapp.ACTION_SUGGESTIONS_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEditText = (EditText) findViewById(R.id.input);
        outputTextView = (TextView) findViewById(R.id.output);

        searchButton = (Button) findViewById(R.id.button);

        searchButton.setOnClickListener(view -> {
            myUrlString += inputEditText.getText().toString();

            // Create a new Thread to handle the network request
            new Thread(() -> {
                try {
                    URL url = new URL(myUrlString);
                    HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("NetworkCommunicationThread", response.toString());

                    // Once the network operation is complete, update the UI
                    runOnUiThread(() -> {
                        // Update the UI with the result
                        String responseString = response.toString();
                        Gson gson = new Gson();
                        List<String> suggestions = (List<String>) gson.fromJson(responseString, List.class).get(1);


                        for (String suggestion : suggestions) {
                            outputTextView.append(suggestion + "\n");
                        }

                        Intent intent = new Intent();
                        intent.setAction("com.yourapp.ACTION_SUGGESTIONS_RECEIVED");
                        intent.putStringArrayListExtra("suggestions", new ArrayList<>(suggestions));
                        sendBroadcast(intent); // Send the broadcast

                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start(); // Start the thread
        });

        suggestionsReceiver = new SuggestionsReceiver();

        // Create an IntentFilter with the action for your custom broadcast
        IntentFilter filter = new IntentFilter("com.yourapp.ACTION_SUGGESTIONS_RECEIVED");

        // Register the receiver to listen for your custom broadcast
        registerReceiver(suggestionsReceiver, filter);
    }
}


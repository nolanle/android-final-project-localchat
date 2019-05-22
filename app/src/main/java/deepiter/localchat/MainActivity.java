package deepiter.localchat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText txtUsername;
    Button btnJoin;

    // Wifi management
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize layout attrs
        txtUsername = findViewById(R.id.txtUsername);
        btnJoin = findViewById(R.id.btnJoin);

        // Set Owner name as username
        //txtUsername.setText(this.getOwnerContactName()[0]);

        // Auto turn Wifi to ON
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        // check edit text change event
        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    btnJoin.setEnabled(false);
                } else {
                    btnJoin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // todo
            }
        });
    }

    public void joinPressed(View view) {
        Intent intent = new Intent(MainActivity.this, BroadcastActivity.class);
        intent.putExtra("username", txtUsername.getText().toString());

        startActivity(intent);
    }
}

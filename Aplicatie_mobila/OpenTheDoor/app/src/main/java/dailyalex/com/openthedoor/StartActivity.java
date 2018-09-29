package dailyalex.com.openthedoor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    private static String TAG = "StartActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TcpClient.getInstance().start();
        UdpClient.getInstance().start();


        Intent intent = new Intent(StartActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}

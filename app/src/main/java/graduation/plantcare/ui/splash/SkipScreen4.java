package graduation.plantcare.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;

import graduation.plantcare.base.BaseActivity;
import graduation.plantcare.R;

public class SkipScreen4 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.skip_screen4);

    }

    public void skipActivity4(View view) {
        Intent intent = new Intent(SkipScreen4.this, WelcomeScreen.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
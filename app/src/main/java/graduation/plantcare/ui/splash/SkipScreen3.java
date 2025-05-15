package graduation.plantcare.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;

import graduation.plantcare.base.BaseActivity;
import graduation.plantcare.R;

public class SkipScreen3 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.skip_screen3);
    }

    public void skipActivity3(View view) {
        Intent intent = new Intent(SkipScreen3.this, WelcomeScreen.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void openNextActivity3(View view) {
        Intent intent = new Intent(SkipScreen3.this, SkipScreen4.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
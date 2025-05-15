package graduation.plantcare.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import graduation.plantcare.base.BaseActivity;
import graduation.plantcare.R;
import androidx.activity.EdgeToEdge;

public class SkipScreen1 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.skip_screen_one);
    }
    public void openNextActivity(View view) {
        Intent intent = new Intent(SkipScreen1.this, SkipScreen2.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    public void skipActivity1(View view) {
        Intent intent = new Intent(SkipScreen1.this, WelcomeScreen.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
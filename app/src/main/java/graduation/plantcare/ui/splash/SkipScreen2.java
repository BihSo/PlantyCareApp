package graduation.plantcare.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import graduation.plantcare.base.BaseActivity;
import graduation.plantcare.R;
import androidx.activity.EdgeToEdge;

public class SkipScreen2 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.skip_screen_two);
    }

    public void toWelcomeActivity(View view) {
        Intent intent = new Intent(SkipScreen2.this, WelcomeScreen.class);
        startActivity(intent);
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(SkipScreen2.this, SkipScreen3.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}

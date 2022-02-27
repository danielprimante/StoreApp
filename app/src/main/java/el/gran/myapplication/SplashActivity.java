package el.gran.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import el.gran.myapplication.login.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen);

        //Duracion del Splash
        int SPLASH_DISPLAY_LENGTH = 1000;

        new Handler().postDelayed(() -> {

            // Intent para mostrar MainActivity
            Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
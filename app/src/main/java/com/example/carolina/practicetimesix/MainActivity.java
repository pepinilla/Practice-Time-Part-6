package com.example.carolina.practicetimesix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {
    ActivityHelper activityHelper = new ActivityHelper();
//    @Bind(R.id.text)
//    TextView text;

    @Bind(R.id.button)
    Button button;
    @Bind(R.id.frameLayout)
    FrameLayout frameLayout;
    @Bind(R.id.textView)
    TextView text;
    @Bind(R.id.EditText)
    EditText edittext;
    @Bind(R.id.button2)
    Button button2;
//shake the device
    private SensorManager sensorManager;
    private Sensor accelerometor;


    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 522;

//text to speech
    private TextToSpeech tts;
    private boolean ttsInitialized;

    //Read music
    private AudioHelper audiohelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tts= new TextToSpeech(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.button)
    public void onViewClicked(View v) {
        //displayMessage("running code");
        View circle = new CircleView(this, Color.CYAN);
        frameLayout.addView(circle);

        String texto = edittext.getText().toString();
        if (texto.isEmpty()){
            Toast.makeText(this, "What do you want to say", Toast.LENGTH_LONG).show();
        }else{
            displayMessage(texto);
            saySomething(texto);
        }
        if (audiohelper != null){
            audiohelper.stop();
        }
        audiohelper = new AudioHelper(this, "musicFile.mp3");
        audiohelper.prepareAndPlay();
        displayMessage("playing");
    }

    @OnClick(R.id.button2)
    public void stop(View v){
        if (audiohelper != null){
            audiohelper.stop();
            displayMessage("stop it");
        }
    }

    public void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        //activityHelper.log(this, text, message, true);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdate) > 300) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    displayMessage("Stop shaking me!");
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.FRENCH);

            if ( result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED){
                displayMessage("This message isn't supported");
            } else { ttsInitialized = true;

            }

            }else {
            displayMessage(" TTS initialization failed");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saySomething(String speech){
        if (!ttsInitialized){
            displayMessage("TextToSpeech wasn't initializaed");
            return;
        }

        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "speech");
    }

    class CircleView extends View{
        private int color;
        public CircleView(Context context, int color) {
            super(context);
            this.color = color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int x = getWidth();
            int y = getWidth();

            int radius = 300;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            canvas.drawPaint(paint);

            paint.setColor(color);
            canvas.drawCircle(x / 2, y / 2, radius, paint);

        }
    }
}

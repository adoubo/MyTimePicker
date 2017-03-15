package com.adoubo.mytimepicker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adoubo.customtimepicker.MyTimePickerDialog;
import com.adoubo.customtimepicker.RadialPickerLayout;
import com.adoubo.customtimepicker.TimeSection;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MyTimePickerDialog.OnTimeSetListener {

    private TextView startText;
    private TextView endText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startText = (TextView) findViewById(R.id.start_time);
        endText = (TextView) findViewById(R.id.end_time);
        final Calendar calendar = Calendar.getInstance();
        final TimeSection timeSection = new TimeSection();
        timeSection.setStartHour(calendar.get(Calendar.HOUR_OF_DAY));
        timeSection.setStartMinute(calendar.get(Calendar.MINUTE));
        timeSection.setEndHour(calendar.get(Calendar.HOUR_OF_DAY));
        timeSection.setEndMinute(calendar.get(Calendar.MINUTE));
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTimePickerDialog myTimePickerDialog = MyTimePickerDialog.newInstance(MainActivity.this, timeSection);
                //myTimePickerDialog.setLayoutMode("strengthen");
                myTimePickerDialog.show(getSupportFragmentManager(), "mydialog");
            }
        });
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int startHour, int startMinute, int endHour, int endMinute, int week) {
        startText.setText(startHour + ":" + startMinute);
        endText.setText(endHour + ":" + endMinute);
        Toast.makeText(this, "week:" + week, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }
}

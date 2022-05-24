package com.duke.handwear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatePicker datePicker;
    Button buttonDate;

    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference myRef;
    ObjectMapper objectMapper = new ObjectMapper();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyyHH:mm:ss");
    LineChart lineChartHeartbeat;
    LineChart lineChartSpo2;

    ArrayList<Entry> entryListHeartbeat = new ArrayList<>();
    ArrayList<Entry> entryListSpo2 = new ArrayList<>();

    List<DataTracking> dataTrackingList = new ArrayList<>();
    List<DataTracking> dataTrackingListAll = new ArrayList<>();

    boolean firstFetch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChartHeartbeat=(LineChart) findViewById(R.id.line_chart_heartbeat);
        lineChartSpo2=(LineChart) findViewById(R.id.line_chart_spo2);
        buttonDate = (Button) this.findViewById(R.id.button_date);
        datePicker = (DatePicker) this.findViewById(R.id.datePicker);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init( year, month , day , new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
            }
        });
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTrackingList = new ArrayList<>();
                for (DataTracking dataTracking : dataTrackingListAll) {
                    if (dataTracking.getDateObj().getDate() == datePicker.getDayOfMonth()
                    && dataTracking.getDateObj().getMonth() == datePicker.getMonth()
                    && dataTracking.getDateObj().getYear() + 1900 == datePicker.getYear()) {
                        dataTrackingList.add(dataTracking);
                    }
                }
                Collections.sort(dataTrackingList);
                if (dataTrackingList.size() > 20) {
                    dataTrackingList = dataTrackingList.subList(0, 20);
                }
                renderData();
            }
        });


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("data");

        configureLineChart();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                assert hashMap != null;
                dataTrackingList = new ArrayList<>();
                dataTrackingListAll = new ArrayList<>();
                for (String string :
                        hashMap.values()) {

                    try {
                        DataTracking dataTracking = objectMapper.readValue(string, DataTracking.class);
                        dataTracking.setDateObj(simpleDateFormat.parse(dataTracking.getDate() + dataTracking.getTime()));
                        dataTrackingListAll.add(dataTracking);

                    } catch (JsonProcessingException | ParseException e) {
                        e.printStackTrace();
                    }

                }
                Collections.sort(dataTrackingListAll);
                if (dataTrackingListAll.size() > 20) {
                    dataTrackingList = dataTrackingListAll.subList(0, 20);
                    datePicker.init( year, dataTrackingListAll.get(0).getDateObj().getMonth() , dataTrackingListAll.get(0).getDateObj().getDate() , new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        }
                    });
                }
                renderData();
                if (1 == dataTrackingListAll.get(0).getMessage()) {
                    Toast.makeText(MainActivity.this, "Some message is coming", Toast.LENGTH_LONG).show();
                }
                if (1 == dataTrackingListAll.get(0).getHorn()) {
                    Toast.makeText(MainActivity.this, "Some horn is coming", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }



    private void renderData() {


        Collections.reverse(dataTrackingList);
        entryListHeartbeat = new ArrayList<>();
        entryListSpo2 = new ArrayList<>();
        for (DataTracking dataTracking :
                dataTrackingList) {
            entryListHeartbeat.add(new Entry(entryListHeartbeat.size()+1, dataTracking.getHeartbeat()));
            entryListSpo2.add(new Entry(entryListSpo2.size()+1, dataTracking.getSpO2()));
        }
        LineDataSet heart = new LineDataSet(entryListHeartbeat, "Nhịp tim");
        ArrayList<ILineDataSet> dataSetsHeart = new ArrayList<>();
        dataSetsHeart.add(heart);
        lineChartHeartbeat.setData(new LineData(dataSetsHeart));
        lineChartHeartbeat.invalidate();

        LineDataSet spo2 = new LineDataSet(entryListSpo2, "SPO2");
        ArrayList<ILineDataSet> dataSetsSpo2 = new ArrayList<>();
        dataSetsSpo2.add(spo2);
        lineChartSpo2.setData(new LineData(dataSetsSpo2));
        lineChartSpo2.invalidate();
    }

    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Thông tin nhịp tim");
        lineChartHeartbeat.setDescription(desc);

        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return dataTrackingList.get((int)value-1).getTime();
            }
        };
        XAxis xAxis = lineChartHeartbeat.getXAxis();
        xAxis.setValueFormatter(valueFormatter);


        Description desc1 = new Description();
        desc1.setText("Thông tin SpO2");
        lineChartSpo2.setDescription(desc);

        XAxis xAxis1 = lineChartSpo2.getXAxis();
        xAxis1.setValueFormatter(valueFormatter);
    }


}
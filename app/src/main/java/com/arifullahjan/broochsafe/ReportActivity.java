package com.arifullahjan.broochsafe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.arifullahjan.broochsafe.firebase.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.trafi.ratingseekbar.RatingSeekBar;

public class ReportActivity extends AppCompatActivity {

    Report report = new Report();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });


        report.location = new GeoPoint(getIntent().getExtras().getDouble("lat"),getIntent().getExtras().getDouble("long"));

        ((RatingSeekBar)findViewById(R.id.seek_bar)).setOnSeekBarChangeListener(new RatingSeekBar.OnRatingSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RatingSeekBar ratingSeekBar, int i) {
                report.intensity = i;
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    private void submit(){
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.submit).setVisibility(View.GONE);

        report.category = ((EditText)findViewById(R.id.category)).getText().toString();
        report.details =  ((EditText)findViewById(R.id.details)).getText().toString();
        db.collection("reports").document(Math.random()+"").set(report)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        Toast.makeText(getApplicationContext(),"Successfully reported incident!",Toast.LENGTH_LONG).show();
                    }
                });
    }

}

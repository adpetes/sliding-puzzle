package com.example.slidingpuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    Button btnPlay;
    RadioGroup rgrpBoardSize;
    RadioButton rbtnChecked;
    int numCols = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRadioButtons();
        setupPlayButton();
    }

    private void setupRadioButtons() {
        rgrpBoardSize = findViewById(R.id.rgrpBoardSize);
        rbtnChecked = findViewById(rgrpBoardSize.getCheckedRadioButtonId());

        rgrpBoardSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                rbtnChecked = findViewById(i);
                switch(rbtnChecked.getId()){
                    case R.id.rbtn3:
                        numCols = 3;
                        break;
                    case R.id.rbtn4:
                        numCols = 4;
                        break;
                }
            }
        });
    }

    private void setupPlayButton() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Num cols", numCols);
                startActivity(intent);
            }
        });
    }
}
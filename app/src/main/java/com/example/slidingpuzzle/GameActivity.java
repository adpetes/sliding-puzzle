package com.example.slidingpuzzle;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

public class GameActivity extends AppCompatActivity {
    //TODO: Make some kind of solver class to contain the BFS, DFS, and aStarSearch code. Also organize model and ui code
    private static CustomGridView gridView;
    private static ArrayList<Button> buttons;
    private static CustomAdapter adapter;
    public static int emptySquare;
    private Button btnReset;
    private Button btnSolve;
    private Button btnNext;
    private Button btnPrev;
    private TextView txtMode;
    private ConstraintLayout cLayoutGame;
    private static TextView tvMoves;
    private static Chronometer chronometer;

    public static int COLUMNS;
    public static int NUM_TILES;

    enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private static int numMoves;
    private static String[] tileList;
    private int pathIndex = 1;
    private Solver solver;
    public static boolean solveMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        COLUMNS = getIntent().getIntExtra("Num cols", 3);
        NUM_TILES = COLUMNS * COLUMNS;

        init();
        setupUI();
        scramble();

        tvMoves.setText("0");
        numMoves = 0;
        chronometer.setBase(SystemClock.elapsedRealtime()); // reset time
    }

    private void setupUI() {
        cLayoutGame = findViewById(R.id.clayoutGame);
        txtMode = findViewById(R.id.txtMode);
        btnReset = findViewById(R.id.btnReset);
        btnSolve = findViewById(R.id.btnSolve);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvMoves = findViewById(R.id.tvMoves);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start(); // start time

        btnPrev.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scramble();
                tvMoves.setText("0");
                numMoves = 0;
                pathIndex = 1;
                chronometer.setBase(SystemClock.elapsedRealtime()); // reset time
                chronometer.start();
                if (solveMode)
                    switchMode();
            }
        });

        btnSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //long start = System.currentTimeMillis();
                pathIndex = 1;
                solver = new Solver(tileList);
                solver.solve();
                //long end = System.currentTimeMillis();
                chronometer.setBase(SystemClock.elapsedRealtime()); // reset time
                chronometer.stop();
                switchMode();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pathIndex >= solver.getPath().size())
                    return;
                nextMove(solver.getPath().get(pathIndex));
                pathIndex++;
            }
        });
    }

    private void switchMode() {
        if (!solveMode) {
            cLayoutGame.setBackgroundResource(R.color.solve_mode);
            solveMode = true;
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnSolve.setText("Exit");
            txtMode.setText("Solver");
        }
        else{
            cLayoutGame.setBackgroundResource(R.color.puzzle_mode);
            solveMode = false;
            btnPrev.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
            btnSolve.setText("Solver");
            txtMode.setText("Puzzle");
        }
    }

    private void nextMove(State s) {
        int pos = 0;

        if (s.direction.equals("UP")){
            pos = s.prevState.emptySquare + COLUMNS;
        }
        else if (s.direction.equals("DOWN")){
            pos = s.prevState.emptySquare - COLUMNS;
        }
        else if (s.direction.equals("LEFT")){
            pos = s.prevState.emptySquare + 1;
        }
        else if (s.direction.equals("RIGHT")){
            pos = s.prevState.emptySquare - 1;
        }

        updateBoard(GameActivity.this, s.direction, pos);
    }

    private void init() {
        gridView = findViewById(R.id.grid);
        gridView.setNumColumns(COLUMNS);
        if (COLUMNS == 4){
            ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
            layoutParams.width = 900; //this is in pixels
            gridView.setLayoutParams(layoutParams);
        }

        tileList = new String[NUM_TILES];
        for (int i = 1; i <= NUM_TILES; i++) {
            tileList[i-1] = String.valueOf(i);
        }
        createButtonsList();
    }

    private void scramble() {
        init();
        Random random = new Random();
        int pos;
        int scrambleVal = 150;
        if (COLUMNS == 4)
            scrambleVal = 100;

        for (int i = 0; i < scrambleVal; i++) {
            int move = random.nextInt(4);
            switch(move){
                case 0:
                    pos = emptySquare + COLUMNS;
                    if (pos < NUM_TILES)
                        swap(this, pos, emptySquare, false);
                    break;
                case 1:
                    pos = emptySquare - COLUMNS;
                    if (pos >= 0)
                        swap(this, pos, emptySquare, false);
                    break;
                case 2:
                    pos = emptySquare - 1;
                    if (emptySquare % COLUMNS != 0)
                        swap(this, pos, emptySquare, false);
                    break;
                case 3:
                    pos = emptySquare + 1;
                    if (emptySquare % COLUMNS < COLUMNS - 1)
                        swap(this, pos, emptySquare, false);
                break;
            }
        }
    }

    private void createButtonsList() {
        buttons = new ArrayList<>();
        Button button;
        Drawable d = getResources().getDrawable(R.drawable.game_tile);

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(this);
            button.setBackgroundDrawable(d);
            button.setLayoutParams(new LinearLayout.LayoutParams(200, 200));

            if (tileList[i].equals(Integer.toString(NUM_TILES))){
                button.setVisibility(View.INVISIBLE);
                emptySquare = i;
            }
            else{
                button.setText(tileList[i]);
            }

            buttons.add(button);
        }
        adapter = new CustomAdapter(buttons);
        gridView.setAdapter(adapter);
    }

    public static void swap(Context context, int currentPosition, int newPosition, boolean gameStarted) {
        Button btn1;
        Button btn2;

        String temp = tileList[newPosition];
        String temp1 = tileList[currentPosition];
        tileList[newPosition] = temp1;
        tileList[currentPosition] = temp;

        btn1 = buttons.get(currentPosition);
        btn2 = buttons.get(newPosition);

        btn1.setText(temp);
        btn2.setText(temp1);
        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.VISIBLE);

        emptySquare = currentPosition;

        if (gameStarted) {
            numMoves++;
            tvMoves.setText(Integer.toString(numMoves));

            if (isSolved())
                Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateBoard(Context context, String direction, int position) {
        Direction d = Direction.valueOf(direction);
        int newPosition = 0;

        switch(d){
            case UP:
                newPosition = position - COLUMNS;
                break;
            case DOWN:
                newPosition = position + COLUMNS;
                break;
            case LEFT:
                newPosition = position - 1;
                break;
            case RIGHT:
                newPosition = position + 1;
                break;
        }

        if (newPosition != emptySquare){
            Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
        }

        else {
            swap(context, position, newPosition, true);
        }
    }

    private static boolean isSolved() {
        boolean solved = true;

        for (int i = 1; i < NUM_TILES; i++) {
            if (Integer.valueOf(tileList[i-1]) != i) {
                solved = false;
                break;
            }
        }

        return solved;
    }
}
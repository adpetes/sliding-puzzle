package com.example.slidingpuzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.slidingpuzzle.GameActivity.COLUMNS;
import static com.example.slidingpuzzle.GameActivity.NUM_TILES;
import static java.lang.Math.abs;

public class State {
    public List<State> nextStates = new ArrayList<>();
    public State prevState;
    public String direction;
    public int[] board;
    public int emptySquare;
    int g;
    int h;
    int f;

    public State(int[] board) {
        this.board = Arrays.copyOf(board, board.length);
    }

    public boolean checkGoalState(){
        for (int i = 0; i < GameActivity.NUM_TILES-1; i++){
            if (board[i] != i+1)
                return false;
        }
        return true;
    }

    public void moveLeft(int[] board, int i){
        if (i % COLUMNS < COLUMNS - 1){
            int[] next;
            next = Arrays.copyOf(board, board.length);

            int temp = next[i+1];
            next[i+1] = next[i];
            next[i] = temp;

            State nextState = new State(next);
            nextState.emptySquare = this.emptySquare + 1;
            nextState.direction = "LEFT";
            nextStates.add(nextState);
            nextState.prevState = this;

            nextState.g = this.g + 1;
            nextState.computeH(GameActivity.Direction.LEFT);
            nextState.f = nextState.g + nextState.h;
        }
    }

    public void moveRight(int[] board, int i){
        if (i % COLUMNS != 0){
            int[] next;
            next = Arrays.copyOf(board, board.length);

            next[i] = next[i-1];
            next[i-1] = NUM_TILES;

            State nextState = new State(next);
            nextState.emptySquare = this.emptySquare - 1;
            nextState.direction = "RIGHT";
            nextStates.add(nextState);
            nextState.prevState = this;

            nextState.g = this.g + 1;
            nextState.computeH(GameActivity.Direction.RIGHT);
            nextState.f = nextState.g + nextState.h;
        }
    }

    public void moveDown(int[] board, int i){
        if (i - COLUMNS >= 0){
            int[] next;
            next = Arrays.copyOf(board, board.length);

            int temp = next[i- COLUMNS];
            next[i- COLUMNS] = next[i];
            next[i] = temp;

            State nextState = new State(next);
            nextState.emptySquare = this.emptySquare - COLUMNS;
            nextState.direction = "DOWN";
            nextStates.add(nextState);
            nextState.prevState = this;

            nextState.g = this.g + 1;
            nextState.computeH(GameActivity.Direction.DOWN);
            nextState.f = nextState.g + nextState.h;
        }
    }

    public void moveUp(int[] board, int i){
        if (i + COLUMNS < NUM_TILES){
            int[] next;
            next = Arrays.copyOf(board, board.length);

            int temp = next[i+ COLUMNS];
            next[i+ COLUMNS] = next[i];
            next[i] = temp;

            State nextState = new State(next);
            nextState.emptySquare = this.emptySquare + COLUMNS;
            nextState.direction = "UP";
            nextStates.add(nextState);
            nextState.prevState = this;

            nextState.g = this.g + 1;
            nextState.computeH(GameActivity.Direction.UP);
            nextState.f = nextState.g + nextState.h;
        }
    }

    private void computeH(GameActivity.Direction direction) {
        int curH = prevState.h;
        int movedVal = prevState.board[emptySquare];
        int oldRow, trueRow, oldCol, trueCol;

        switch(direction){
            case UP:
                curH--;
                oldRow = emptySquare / COLUMNS;
                trueRow = (movedVal-1) / COLUMNS;
                if (trueRow < oldRow)
                    curH--;
                else
                    curH++;
                break;
            case LEFT:// If we move left, our heuristic gets smaller
                // if the moved value's 'true col' (where it belongs)
                // is to the left of the col it was in before moving.
                // The vertical distance from it's 'true place' remains the
                // same, so we only consider horizontal change
                curH--;
                oldCol = emptySquare % COLUMNS;
                trueCol = (movedVal-1) % COLUMNS;
                if (trueCol < oldCol)
                    curH--;
                else
                    curH++;
                break;

            case DOWN:
                curH++;
                oldRow = emptySquare / COLUMNS;
                trueRow = (movedVal-1) / COLUMNS;
                if (trueRow > oldRow)
                    curH--;
                else
                    curH++;
                break;

            case RIGHT:
                curH++;
                oldCol = emptySquare % COLUMNS;
                trueCol = (movedVal-1) % COLUMNS;
                if (trueCol > oldCol)
                    curH--;
                else
                    curH++;
                break;
        }
        int calch =  0;
        int curRow, curCol, tRow, tCol;
        for (int i = 0; i < NUM_TILES; i++){
            curRow = i / COLUMNS;
            curCol = i % COLUMNS;
            tRow = (board[i]-1) / COLUMNS;
            tCol = (board[i]-1) % COLUMNS;
            calch += abs(tRow - curRow) + abs(tCol - curCol);
        }
        if (calch != curH)
            Log.i("TAG", "Wrong h...");
        h = curH;
    }

    public boolean isSameBoard(int[] board){
        for (int i = 0; i < board.length; i++){
            if (board[i] != this.board[i]){
                return false;
            }
        }
        return true;
    }

    public void getNextStates(){
        moveUp(board, emptySquare);
        moveDown(board, emptySquare);
        moveLeft(board, emptySquare);
        moveRight(board, emptySquare);
    }
}

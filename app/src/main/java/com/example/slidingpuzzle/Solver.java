package com.example.slidingpuzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.abs;

public class Solver {
    private int COLUMNS = GameActivity.COLUMNS;
    private int NUM_TILES = GameActivity.NUM_TILES;
    private State initialState;
    private List<State> path = new ArrayList<>();

    public List<State> getPath() {
        return path;
    }

    public Solver(String[] tileList){
        int[] board = new int[NUM_TILES];
        int es = 0;
        for (int i = 0; i < NUM_TILES; i++){
            board[i] = Integer.valueOf(tileList[i]);
            if (board[i] == NUM_TILES){
                es = i;
            }
        }
        initialState = new State(board);
        initialState.emptySquare = es;

        int h =  0;
        int curRow, curCol, trueRow, trueCol;
        for (int i = 0; i < NUM_TILES; i++){
            curRow = i / COLUMNS;
            curCol = i % COLUMNS;
            trueRow = (board[i]-1) / COLUMNS;
            trueCol = (board[i]-1) % COLUMNS;
            h += abs(trueRow - curRow) + abs(trueCol - curCol);
        }
        initialState.h = h;
    }

    public void solve(){
        List<State> pQueue = new ArrayList<>();
        HashSet<String> visited = new HashSet();

        pQueue.add(initialState);
        boolean pathFound = false;

        while (!pathFound){ // remove size check?
            State cur = pQueue.get(0);
            pQueue.remove(0);
            if(visited.contains(Arrays.toString(cur.board))){
                int hi = 2;
                continue;
            }
            insertVisited(visited, cur);

            cur.getNextStates();
            for (State next : cur.nextStates){
                if (next.checkGoalState()){
                    pathFound = true;
                    findPath(next);
                }
                if(!visited.contains(Arrays.toString(next.board))){
                    insertQueue(pQueue, next);
                }
            }
        }
    }

    private void insertVisited(HashSet<String> visited, State state){
        String strState = Arrays.toString(state.board);
        visited.add(strState);
    }

    private void insertQueue(List<State> pQueue, State state) {
        int left, right, mid;
        mid = 0;
        left = 0;
        right = pQueue.size()-1;

        while (left <= right){
            mid = (left + right) / 2;
            if (state.f == pQueue.get(mid).f){
                break;
            }
            else if (pQueue.get(mid).f > state.f){
                right = mid - 1;
            }
            else {
                left = mid + 1;
            }
        }

        pQueue.add(mid, state);
    }

    public void BFS() {
        List<State> moveQueue = new ArrayList<>();
        List<State> visited = new ArrayList<>();

        moveQueue.add(initialState);
        boolean pathFound = false;

        while (moveQueue.size() > 0 && !pathFound){
            State cur = moveQueue.get(0);
            moveQueue.remove(0);
            visited.add(cur);

            cur.getNextStates();
            for (State next : cur.nextStates){
                if (next.checkGoalState()){
                    pathFound = true;
                    findPath(next);
                }

                if(!contains(moveQueue, next) && !contains(visited, next)){
                    moveQueue.add(next);
                }
            }
        }
    }

    public boolean contains(List<State> list, State s){
        for (int i = 0; i < list.size(); i ++){
            if (list.get(i).isSameBoard(s.board)){
                return true;
            }
        }
        return false;
    }

    public void findPath(State cur){
        path.add(0, cur);

        while (cur.prevState != null){
            cur = cur.prevState;
            path.add(0, cur);
        }
    }
}

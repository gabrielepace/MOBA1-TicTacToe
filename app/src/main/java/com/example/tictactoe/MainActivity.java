package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

    static boolean circle;
    int[][] grid;
    ImageView iv;

    public void setupGame(){
        circle = true;
        grid = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};//0 = empty, 1 = circle, 2 = cross
        iv = null;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupGame();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        Button btn = findViewById(R.id.exitbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app", "test");
            }
        });

        findViewById(R.id.testbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app", "test2");
                ImageView iv = (ImageView)findViewById(R.id.tile1);
                iv.setImageResource(R.drawable.circle);

            }
        });



        RelativeLayout rl = findViewById(R.id.gameTouch);
        rl.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.i("app", event.getX() + " " + event.getY());
                    int x = Math.round((event.getX())/233);
                    int y = Math.round((event.getY())/233);
                    Log.i("app", x+ " " + y);

                    if(x==1&&y==1)iv = findViewById(R.id.tile1);
                    if(x==2&&y==1)iv = findViewById(R.id.tile2);
                    if(x==3&&y==1)iv = findViewById(R.id.tile3);
                    if(x==1&&y==2)iv = findViewById(R.id.tile4);
                    if(x==2&&y==2)iv = findViewById(R.id.tile5);
                    if(x==3&&y==2)iv = findViewById(R.id.tile6);
                    if(x==1&&y==3)iv = findViewById(R.id.tile7);
                    if(x==2&&y==3)iv = findViewById(R.id.tile8);
                    if(x==3&&y==3)iv = findViewById(R.id.tile9);
                    if(iv!=null && grid[x-1][y-1] == 0){
                        grid[x-1][y-1] = circle ? 1 : 2;
                        int turn = grid[x-1][y-1];
                        iv.setImageResource(circle ? R.drawable.circle : R.drawable.cross);
                        circle=!circle;
                        if(checkWinner(turn) == turn){
                            setWinner(turn);
                        }

                    }
                }
                return true;
            }
        });



    }

    public int checkWinner(int turn){
        if((grid[1-1][1-1] == turn) && (grid[1-1][2-1] == turn) && (grid[1-1][3-1] == turn)){
            return turn;
        }
        if((grid[2-1][1-1] == turn) && (grid[2-1][2-1] == turn) && (grid[2-1][3-1] == turn)){
            return turn;
        }
        if((grid[3-1][1-1] == turn) && (grid[3-1][2-1] == turn) && (grid[3-1][3-1] == turn)){
            return turn;
        }
        if((grid[1-1][1-1] == turn) && (grid[2-1][1-1] == turn) && (grid[3-1][1-1] == turn)){
            return turn;
        }
        if((grid[1-1][2-1] == turn) && (grid[2-1][2-1] == turn) && (grid[3-1][2-1] == turn)){
            return turn;
        }
        if((grid[1-1][3-1] == turn) && (grid[2-1][3-1] == turn) && (grid[3-1][3-1] == turn)){
            return turn;
        }

        if((grid[1-1][1-1] == turn) && (grid[2-1][2-1] == turn) && (grid[3-1][3-1] == turn)){
            return turn;
        }

        if((grid[3-1][1-1] == turn) && (grid[2-1][2-1] == turn) && (grid[1-1][3-1] == turn)){
            return turn;
        }
        else{
            return 0;
        }
    }

    public void setWinner(int t){
        String winner = "";
        switch(t) {
            case 1:
                Log.i("winner ist:", "Kreis" );
                winner = "Kreis";
                break;
            case 2:
                Log.i("winner ist:", " Kreuz");
                winner = "Kreuz";
                break;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Der Gewinner ist "+ winner );

        alertDialogBuilder.setPositiveButton("Nochmals Spielen?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("Spiel verlassen",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
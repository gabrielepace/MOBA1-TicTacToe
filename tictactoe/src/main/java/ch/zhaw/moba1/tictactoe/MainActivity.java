package ch.zhaw.moba1.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView playerOneScore, playerTwoScore, playerStatus;
    private Button[] buttons = new Button[9];
    private Button reset;

    private int playerOneScoreCount, playerTwoScoreCount;

    public boolean activePlayer;

    public int[] gameState = {2,2,2,2,2,2,2,2,2};

    public int[][] winningPositions = {
                    {0,1,2}, {3,4,5}, {6,7,8}, // rows
                    {0,3,6}, {1,4,7}, {2,5,8}, // columns
                    {0,4,8}, {2,4,6} // diagonals
    };

    private WebSocket webSocket;

    boolean online = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerOneScore = (TextView) findViewById(R.id.playerOneScore);
        playerTwoScore = (TextView) findViewById(R.id.playerTwoScore);
        playerStatus = (TextView) findViewById(R.id.playerStatus);

        reset = (Button) findViewById(R.id.reset);

        for (int i=0; i< buttons.length; i++){
            String buttonId = "btn_" + i;
            int resourceId = getResources().getIdentifier(buttonId, "id",getPackageName());
            buttons[i] = (Button) findViewById(resourceId);
            buttons[i].setOnClickListener(this);
        }

        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        activePlayer = true;

        findViewById(R.id.onlinebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOnline();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgain();
                playerOneScoreCount = 0;
                playerTwoScoreCount = 0;
                playerStatus.setText("");
                updatePlayerScore();
            }
        });
    }

    void goOnline(){
        Log.i("app","going online");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://80.218.189.9:3001").build();//"wss://echo.websocket.org""ws://localhost:3001/""ws://178.82.64.27:3001/""ws://moba1.herokuapp.com/"
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i("app","onOpen success");
            online = true;

        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            super.onMessage(webSocket, text);
            Log.i("app","onMessage " + text);

            String text2 = text.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").replace(" ", "").split("\"")[3];
            String[] items = text2.split(",");
            Log.i("app","text2: " + text2);
            for (int i = 0; i < 9; i++) {
                try {
                    gameState[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    Log.i("app","error");
                };
            }


            updateBoard();
            showWinner();

            if (!checkWinner()) {
                activePlayer = !activePlayer;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });
        }
    }

    void send(){
        try {
            if(webSocket != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", Arrays.toString(gameState));

                webSocket.send(jsonObject.toString());
                Log.i("app","sent msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //uses the board array to update the graphics
    public void updateBoard(){
        int[] btns = {R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3,R.id.btn_4,R.id.btn_5,R.id.btn_6,R.id.btn_7,R.id.btn_8};

        for (int i = 0;i<9;i++){
            Button current = (Button) findViewById(btns[i]);
            if(gameState[i] == 0){
                current.setText("X");
                current.setTextColor(Color.parseColor("#FFC34A"));
            } else if(gameState[i] == 1) {
                current.setText("O");
                current.setTextColor(Color.parseColor("#70FFEA"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        String buttonId = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(buttonId.substring(buttonId.length() - 1, buttonId.length()));

        if (activePlayer) {
            gameState[gameStatePointer] = 0;
        } else {
            gameState[gameStatePointer] = 1;
        }


        send();
        updateBoard();
        showWinner();
    }

    boolean checkIfTie(){
        int sum = 0;
        for (int i = 0;i<9;i++){
            sum+= gameState[i] == 2 ? 0 : 1;
        }
        return sum == 9;
    }

    void showWinner(){
        if (checkWinner()) {
            if(activePlayer){
                playerOneScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player 1 Won!", Toast.LENGTH_SHORT).show();
                playAgain();
                goOnline();
            }else {
                playerTwoScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player 2 Won!", Toast.LENGTH_SHORT).show();
                playAgain();
                goOnline();
            }
        }else if(checkIfTie()){
            playAgain();
            Toast.makeText(this, "No Winner!", Toast.LENGTH_SHORT).show();
            goOnline();
        }else {
            if(online)activePlayer = !activePlayer;
        }

        if(playerOneScoreCount > playerTwoScoreCount){
            playerStatus.setText("Player 1 is Winning!");
        }else if(playerTwoScoreCount > playerOneScoreCount){
            playerStatus.setText("Player 2 is Winning!");
        }else {
            playerStatus.setText("");
        }
    }

    public boolean checkWinner(){
        boolean winnerResult = false;

        for(int[] winningPosition: winningPositions){
            if((gameState[winningPosition[0]] == gameState[winningPosition[1]]) &&
                    (gameState[winningPosition[1]] == gameState[winningPosition[2]]) &&
                            (gameState[winningPosition[2]] != 2)){
                winnerResult = true;
            }
        }
        return winnerResult;
    }

    public void updatePlayerScore(){
        playerOneScore.setText(Integer.toString(playerOneScoreCount));
        playerTwoScore.setText(Integer.toString(playerTwoScoreCount));
    }

    public void playAgain(){
        activePlayer = true;

        for(int i=0; i<buttons.length; i++){
            gameState[i] = 2;
            buttons[i].setText("");
        }
        send();
    }
}

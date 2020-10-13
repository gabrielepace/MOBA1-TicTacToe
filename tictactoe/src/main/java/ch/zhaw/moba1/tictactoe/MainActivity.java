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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView playerOneScore, playerTwoScore, playerStatus;
    private Button[] buttons = new Button[9];
    private Button reset;

    private int playerOneScoreCount, playerTwoScoreCount, roundCount;

    public boolean activePlayer;

    public int[] gameState = {2,2,2,2,2,2,2,2,2};

    public int[][] winningPositions = {
                    {0,1,2}, {3,4,5}, {6,7,8}, // rows
                    {0,3,6}, {1,4,7}, {2,5,8}, // columns
                    {0,4,8}, {2,4,6} // diagonals
    };

    private WebSocket webSocket;



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
        roundCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        activePlayer = true;

        findViewById(R.id.onlinebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app","going online");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("wss://echo.websocket.org").build();//"wss://echo.websocket.org""ws://localhost:3001/""ws://178.82.64.27:3001/""ws://moba1.herokuapp.com/"
                webSocket = client.newWebSocket(request, new SocketListener());




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

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i("app","onOpen success");


        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("app","onMessage " + text);

                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        try {
            if(webSocket != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "2,2,2,2,2,2,2,2,2");

                webSocket.send(jsonObject.toString());
                Log.i("app","sent msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String buttonId = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(buttonId.substring(buttonId.length() - 1, buttonId.length()));

        if (activePlayer) {
            ((Button) v).setText("X");
            ((Button) v).setTextColor(Color.parseColor("#FFC34A"));
            gameState[gameStatePointer] = 0;
        } else {
            ((Button) v).setText("O");
            ((Button) v).setTextColor(Color.parseColor("#70FFEA"));
            gameState[gameStatePointer] = 1;
        }
        roundCount++;

        if (checkWinner()) {
            if(activePlayer){
                playerOneScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player 1 Won!", Toast.LENGTH_SHORT).show();
                playAgain();
            }else {
                playerTwoScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player 2 Won!", Toast.LENGTH_SHORT).show();
                playAgain();
            }
        }else if(roundCount == 9){
        playAgain();
        Toast.makeText(this, "No Winner!", Toast.LENGTH_SHORT).show();
        }else {
            activePlayer = !activePlayer;
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
        roundCount = 0;
        activePlayer = true;

        for(int i=0; i<buttons.length; i++){
            gameState[i] = 2;
            buttons[i].setText("");
        }
    }
}

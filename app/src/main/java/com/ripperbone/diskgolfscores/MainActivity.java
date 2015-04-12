package com.ripperbone.diskgolfscores;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	
	public final static String DATE = "DATE";
	public final static String COURSE = "COURSE";
	public final static String SCORES = "SCORES";
	public final static String PLAYERS = "PLAYERS";
	public final static String CURRENT_PLAYER = "CURRENT_PLAYER";
	public final static String CURRENT_PLAYER_SCORES = "CURRENT_PLAYER_SCORES";
	public final static int COURSE_REQUEST_CODE = 1;
	public final static int PLAYERS_REQUEST_CODE = 2;
	public final static String PARS = "PARS";
	
	
	private String date;
	private String course;
	private HashMap<String, ArrayList<Integer>> scores;
	private ArrayList<String> players;
	private ArrayAdapter<String> playerChoiceList;
	private final static String PAR_FILE_PATH = "pars.txt";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		

			
			
		if (savedInstanceState != null) {
			
			date = savedInstanceState.getString(DATE);
			course = savedInstanceState.getString(COURSE);
			scores = (HashMap<String, ArrayList<Integer>>) savedInstanceState.getSerializable(SCORES);
			players = savedInstanceState.getStringArrayList(PLAYERS);
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		String todaysDate = dateFormat.format(cal.getTime());
		
		if (date == null) date = todaysDate;
		if (players == null) players = new ArrayList<String>();
		if (course == null) course = "";
		if (scores == null) scores = new HashMap<String, ArrayList<Integer>>();
		

	
		Spinner spinner = (Spinner) findViewById(R.id.playerInputField);
		
		playerChoiceList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, players);
		playerChoiceList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(playerChoiceList);
		
		
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addScore(View view) {
	
		//Get the score and player information
		
		EditText scoreInput = (EditText) findViewById(R.id.scoreInputField);
		Spinner playerInput = (Spinner) findViewById(R.id.playerInputField);
		
		String scoreValue = scoreInput.getText().toString().trim();
		String player = (String) playerInput.getSelectedItem();
	
		if (scoreValue.length() < 1) {
			displayMessage(R.string.no_score_provided);
		} else if (scoreValue.length() > 2) {
			displayMessage(R.string.score_too_big);
		} else if (player == null ) {
			displayMessage(R.string.no_players_added);
		} else {
			ArrayList<Integer> playerScores = scores.get(player);
			if (playerScores == null) playerScores = new ArrayList<Integer>();
			playerScores.add(Integer.parseInt(scoreValue));
			scores.put(player, playerScores);
	
			
			
			scoreInput.setText("");
			displayMessage(R.string.score_added);

			
		}
	}
	
	public void newGame(View view) {
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				switch(choice) {
				case DialogInterface.BUTTON_POSITIVE:
					resetGameDetails();
					break;
				
				case DialogInterface.BUTTON_NEGATIVE:
					displayMessage(R.string.cancelled);
					break;
					

			}
		}
	};
	
	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	dialogBuilder.setMessage(getString(R.string.new_game_dialog))
	.setPositiveButton(getString(R.string.yes), listener)
	.setNegativeButton(getString(R.string.no), listener)
	.show();
		
	}
	
	private void resetGameDetails() {
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		String todaysDate = dateFormat.format(cal.getTime());
		date = todaysDate;
		
		course = "";
		scores = new HashMap<String, ArrayList<Integer>>();
		players = new ArrayList<String>();
		playerChoiceList.clear();
		playerChoiceList.notifyDataSetChanged();
		
		
	}
	

	
	public void computeTotal(View view) {
		
		Intent intent = new Intent(this, DisplayTotalScores.class);
		intent.putExtra(SCORES, scores);
		startActivity(intent);

	}
	
	public void displayPlayerScores(View view) {
		Spinner playerInput = (Spinner) findViewById(R.id.playerInputField);
		
		Object player = playerInput.getSelectedItem();
		if (player != null) {
			ArrayList<Integer> playerScores = scores.get(player.toString());
			
			ArrayList<Integer> pars = new ArrayList<Integer>();
			
			
			// Find pars for the current course
			
			File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			//File[] files = storage.listFiles();              // check that we can see files
			File file = new File(storage, PAR_FILE_PATH);
			BufferedReader reader;
			

			try {
				reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				
				while (line != null) {
					if (line.split(":")[0].equalsIgnoreCase(course)) {
						String[] parStrings = line.split(":")[1].split(",");
						for (int i=0; i < parStrings.length; i++) {
							pars.add(Integer.parseInt(parStrings[i]));
						}
					}
					line = reader.readLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			
			
			
			
			
			Intent intent = new Intent(this, DisplayPlayerScores.class);
			intent.putExtra(CURRENT_PLAYER, player.toString());
			intent.putExtra(CURRENT_PLAYER_SCORES, playerScores);
			intent.putExtra(PARS, pars);
			startActivity(intent);
		} else {
			displayMessage(R.string.no_players_added);
		}
	}
	
	
	private void displayMessage(int message) {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(DATE, date);
		outState.putString(COURSE, course);
		outState.putSerializable(SCORES, scores);
		outState.putStringArrayList(PLAYERS,players);
		
	}
	
	
	public void addPlayer(View view) {
		
		EditText playerInput = (EditText) findViewById(R.id.addPlayerField);
		String player = playerInput.getText().toString().trim();
		if (!player.equals("")) {
            playerChoiceList.add(player);
            playerChoiceList.notifyDataSetChanged();
            playerInput.setText("");
        } else {
            displayMessage(R.string.no_player_name_provided);
        }
	}
	
}

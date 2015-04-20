package com.ripperbone.diskgolfscores;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	
	public final static String DATE = "DATE";
	public final static String COURSE = "COURSE";
	public final static String SCORES = "SCORES";
	public final static String PLAYERS = "PLAYERS";
	public final static String CURRENT_PLAYER = "CURRENT_PLAYER";
	public final static String CURRENT_PLAYER_SCORES = "CURRENT_PLAYER_SCORES";
	
	private String date;
	private String course;
	private HashMap<String, ArrayList<Integer>> scores;
	private ArrayList<String> players;
	private ArrayAdapter<String> playerChoiceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		/*

		Load info from saved instance. Course isn't used right now but a field to update it might get added in later so I'll leave it.

		 */

		if (savedInstanceState != null) {
			
			date = savedInstanceState.getString(DATE);
			course = savedInstanceState.getString(COURSE);
			scores = (HashMap<String, ArrayList<Integer>>) savedInstanceState.getSerializable(SCORES);
			players = savedInstanceState.getStringArrayList(PLAYERS);
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		String todaysDate = dateFormat.format(cal.getTime());
		
		if (date == null) date = todaysDate;
		if (players == null) players = new ArrayList<String>();
		if (course == null) course = getString(R.string.course_placeholder);
		if (scores == null) scores = new HashMap<String, ArrayList<Integer>>();
		

        /*
            Setup the drop down for choosing player.
         */

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

            // start Settings activity


            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);


            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addScore(View view) {
	
		/*
		    Add a score for the player specified  in the spinner and the score specified in the score input field
		 */
		
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

        /*
            Ask user if they really want to start a new game. If so, save to file and reset game details.

         */


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean saveFile = prefs.getBoolean("pref_save_file", false);



        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				switch(choice) {
				case DialogInterface.BUTTON_POSITIVE:


                    // check if preference set to save to file



                    if (saveFile) {


                        // save to file

                        String state = Environment.getExternalStorageState();


                        // if nothing added to scores yet, don't save a blank file

                        if (scores.size() == 0) {
                            displayMessage(R.string.not_saving_empty_file);

                        } else {


                            // check if we are in a state where media is inaccessible

                            if (!state.equals(Environment.MEDIA_BAD_REMOVAL) && !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY) && !state.equals(Environment.MEDIA_UNMOUNTED)) {


                                // let the user know that the scores are being saved

                                displayMessage(R.string.saving_file);


                                File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                                File scoresFile = new File(storage, "scores_" + date + ".txt");


                                try {
                                    FileOutputStream out = new FileOutputStream(scoresFile);
                                    PrintWriter writer = new PrintWriter(out);


                                    Iterator<HashMap.Entry<String, ArrayList<Integer>>> iterator = scores.entrySet().iterator();
                                    ArrayList<Integer> playerScores;
                                    String playerName;

                                    // iterate through player scores and add to the file

                                    while (iterator.hasNext()) {

                                        HashMap.Entry<String, ArrayList<Integer>> entry = iterator.next();
                                        playerScores = entry.getValue();
                                        playerName = entry.getKey();


                                        writer.println(playerName + ": " + playerScores.toString());
                                        writer.flush();
                                    }
                                    writer.close();
                                    out.close();

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        }
                    }


                    // reset the game details

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
                    Calendar cal = Calendar.getInstance();
                    String todaysDate = dateFormat.format(cal.getTime());
                    date = todaysDate;

                    course = "";
                    scores = new HashMap<String, ArrayList<Integer>>();
                    players = new ArrayList<String>();
                    playerChoiceList.clear();
                    playerChoiceList.notifyDataSetChanged();


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





	
	public void computeTotal(View view) {

        /*
            Start a new activity which will display total scores for each player along with some other statistics.
         */
		
		Intent intent = new Intent(this, DisplayTotalScores.class);
		intent.putExtra(SCORES, scores);
		startActivity(intent);

	}
	
	public void displayPlayerScores(View view) {

        /*
            Start a new activity to display each individual score for a specified player
         */

		Spinner playerInput = (Spinner) findViewById(R.id.playerInputField);
		
		Object player = playerInput.getSelectedItem();
		if (player != null) {
			ArrayList<Integer> playerScores = scores.get(player.toString());

			
			Intent intent = new Intent(this, DisplayPlayerScores.class);
			intent.putExtra(CURRENT_PLAYER, player.toString());
			intent.putExtra(CURRENT_PLAYER_SCORES, playerScores);

			startActivity(intent);
		} else {
			displayMessage(R.string.no_players_added);
		}
	}
	
	
	private void displayMessage(int message) {
        /*
            Show a brief popup informational message
         */


		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	protected void onSaveInstanceState(Bundle outState) {

        /*
            Save information so that it doesn't get wiped out in the middle of a game
         */


		super.onSaveInstanceState(outState);
		
		outState.putString(DATE, date);
		outState.putString(COURSE, course);
		outState.putSerializable(SCORES, scores);
		outState.putStringArrayList(PLAYERS,players);
		
	}
	
	
	public void addPlayer(View view) {

        /*
            Add a player to the spinner so that they can be selected during the current game

         */
		
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

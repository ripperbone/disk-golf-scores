package com.ripperbone.diskgolfscores;

import java.util.ArrayList;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayPlayerScores extends Activity {

    /*
        Show all the scores for a specified player
     */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_player_scores);
		Intent intent = getIntent();
		String playerValue = intent.getStringExtra(MainActivity.CURRENT_PLAYER);
		ArrayList<Integer> scores = intent.getIntegerArrayListExtra(MainActivity.CURRENT_PLAYER_SCORES);
		
		if (scores == null) scores = new ArrayList<Integer>();
		
		TableLayout table = (TableLayout) findViewById(R.id.playerScoresTable);
		
		TableRow row = new TableRow(this);
		
		row.addView(makeTextView(getString(R.string.scores_for_player) + playerValue));


		table.addView(row);
		
		int score;

		
		for (int i=0; i < scores.size(); i++) {
			row = new TableRow(this);
			score = scores.get(i);
			row.addView(makeTextView(String.valueOf(score)));
			
			table.addView(row);
		}	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_player_scores, menu);
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
	
	private TextView makeTextView(String text) {

        /*
            Create text views with consistent appearance and set the text to the string passed in
         */

		TextView view = new TextView(this);
		view.setText(text);
		view.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large_Inverse);
		return view;
	}
	

}

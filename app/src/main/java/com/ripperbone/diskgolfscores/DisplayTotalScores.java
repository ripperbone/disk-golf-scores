package com.ripperbone.diskgolfscores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class DisplayTotalScores extends Activity {

    /*
        This activity will display total score along with some additional details for each player
     */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//need to set content view before getting table layout
		setContentView(R.layout.activity_display_total_scores);
		TableLayout table = (TableLayout) findViewById(R.id.scoresTable);
		
		
		Intent intent = getIntent();
		

		HashMap<String, ArrayList<Integer>> scores = (HashMap<String, ArrayList<Integer>>) intent.getSerializableExtra(MainActivity.SCORES);
		
		
		// add headers to table
		TableRow row = new TableRow(this);
		TextView playerNameHeader = makeTextView(getString(R.string.player_name));
		TextView totalScoreHeader = makeTextView(getString(R.string.total_score));
		TextView averageHeader = makeTextView(getString(R.string.average));
		TextView leastThrowsHeader = makeTextView(getString(R.string.least_throws));
		TextView mostThrowsHeader = makeTextView(getString(R.string.most_throws));
		row.addView(playerNameHeader);
		row.addView(totalScoreHeader);
		row.addView(averageHeader);
		row.addView(leastThrowsHeader);
		row.addView(mostThrowsHeader);
		table.addView(row);
		
		// done adding headers
				
		if (scores != null) {
		
			Iterator<HashMap.Entry<String, ArrayList<Integer>>> iterator = scores.entrySet().iterator();
			while(iterator.hasNext()) {
				HashMap.Entry<String, ArrayList<Integer>> entry = iterator.next();
				ArrayList<Integer> playerScores = entry.getValue();
			
				if (playerScores.size() > 0) {
				
					row = new TableRow(this);
			
					
					TextView player = makeTextView(entry.getKey());
					TextView totalScore = makeTextView(String.valueOf(getTotalScore(playerScores)));
					TextView average = makeTextView(String.format("%.2f", getAverageScore(playerScores)));
					TextView lowest = makeTextView(String.valueOf(getLowestScore(playerScores)));
					TextView greatest = makeTextView(String.valueOf(getHighestScore(playerScores)));
					
					row.addView(player);
					row.addView(totalScore);
					row.addView(average);
					row.addView(lowest);
					row.addView(greatest);
					
					
					
					
					table.addView(row);
				}
			}
		}
	 	
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_total_scores, menu);
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
		TextView view = new TextView(this);
		view.setText(text);
		view.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large_Inverse);
		return view;
	}

	
	private int getTotalScore(ArrayList<Integer> scores) {
		int total = 0;
		for (int score: scores) {
			total = total + score;
		}
		return total;
	}
	
	private double getAverageScore(ArrayList<Integer> scores) {
		int total = 0;
		for (int score: scores) {
			total = total + score;
		}
		int scoreCount = scores.size();
		return (double) total / scoreCount;
	}
	
	private int getHighestScore(ArrayList<Integer> scores) {
		int ret = scores.get(0);
		Iterator<Integer> iter = scores.iterator();
		// skip first element
		iter.next();
		while (iter.hasNext()) {
			int score = iter.next();
			if (score > ret) ret = score;
		}

		return ret;
	}
	
	private int getLowestScore(ArrayList<Integer> scores) {
		int ret = scores.get(0);
		Iterator<Integer> iter = scores.iterator();
		// skip first element
		iter.next();
		while (iter.hasNext()) {
			int score = iter.next();
			if (score < ret) ret = score;
		}
		return ret;
	}
}

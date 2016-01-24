package games.primitive.musicalchairs;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	public int playersNumber = 5;
	public TextView textPlayersNumber;
	public TextView textChairsNumber;
	public int averageRoundDurationNumber = 15;
	public TextView textAverageRoundDurationNumber;
	public int pauseDurationNumber = 10;
	public TextView textPauseDurationNumber;
	public int currentPause = 0;
	public ImageButton buttonPlay;
	public ImageButton buttonPause;
	public ImageButton buttonStop;
	public TextView textPauseCountdown;
	public RelativeLayout countdownBackground;
	public TextView textDebugging;
	public SeekBar seekBarPlayers;
	public TextView textSongName;
	public TextView textArtistName;

	public MediaPlayer customMusic = null;
	public MediaPlayer music;
	public String artist = "Kevin MacLeod";
	public String song = "Meatball Parade";
	public MediaPlayer winningMusic;
	public MediaPlayer tic;

	public Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textPlayersNumber = (TextView) findViewById(R.id.textPlayersNumber);
		textChairsNumber = (TextView) findViewById(R.id.textChairsNumber);

		seekBarPlayers = (SeekBar) findViewById(R.id.seekBarPlayers);
		seekBarPlayers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				playersNumber = progress + 2;
				textPlayersNumber.setText(String.valueOf(progress + 2));
				textChairsNumber.setText(String.valueOf(progress + 1));
			}
		});

		textAverageRoundDurationNumber = (TextView) findViewById(R.id.textAverageRoundDurationNumber);

		SeekBar seekBarAverageRoundDuration = (SeekBar) findViewById(R.id.seekBarAverageRoundDuration);
		seekBarAverageRoundDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				averageRoundDurationNumber = progress + 5;
				textAverageRoundDurationNumber.setText(String.valueOf(progress + 5) + " s");
			}
		});

		textPauseDurationNumber = (TextView) findViewById(R.id.textPauseDurationNumber);

		SeekBar seekBarPauseDuration = (SeekBar) findViewById(R.id.seekBarPauseDuration);
		seekBarPauseDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				pauseDurationNumber = progress + 3;
				textPauseDurationNumber.setText(String.valueOf(progress + 3) + " s");
			}
		});

		buttonPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
		buttonPause = (ImageButton) findViewById(R.id.imageButtonPause);
		buttonStop = (ImageButton) findViewById(R.id.imageButtonStop);
		countdownBackground = (RelativeLayout) findViewById(R.id.countdownBackground);
		textPauseCountdown = (TextView) findViewById(R.id.textPauseCountdown);
		textSongName = (TextView) findViewById(R.id.textSongName);
		textArtistName = (TextView) findViewById(R.id.textArtistName);

		textDebugging = (TextView) findViewById(R.id.debugging);

		music = MediaPlayer.create(this, R.raw.meatballparade);
		music.setLooping(true);
		winningMusic = MediaPlayer.create(this, R.raw.lopingsting);
		tic = MediaPlayer.create(this, R.raw.tic);
	}

	public Handler pauseHandler = new Handler();
	public void playMusic(View view) {
		Toast.makeText(this, "V2", Toast.LENGTH_SHORT).show();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (customMusic != null) {
			music = customMusic;
		}
		music.setLooping(true);
		music.start();
		buttonPlay.setVisibility(View.GONE);
		buttonStop.setVisibility(View.VISIBLE);
		int min = averageRoundDurationNumber * 500;
		int max = averageRoundDurationNumber * 1500;
		Random random = new Random();
		int roundDuration = random.nextInt(max - min + 1) + min;
		textDebugging.setText("Round duration: " + String.valueOf(roundDuration) + " ms");

		pauseHandler.postDelayed(endRoundRunnable, roundDuration);
	}

	public Runnable endRoundRunnable = new Runnable() {
		@Override
		public void run() {
			if (playersNumber > 2) {
				seekBarPlayers.setProgress(playersNumber - 3);
				pauseMusic(null);
			} else {
				stopMusic(null);
				pauseHandler.postDelayed(endGameRunnable, 2000);
				resetGame();
			}
		}
	};

	public Runnable endGameRunnable = new Runnable() {
		@Override
		public void run() {
			winningMusic.start();
		}
	};

	public void pauseMusic(View view) {
		music.pause();
		buttonPause.setVisibility(View.GONE);
		buttonStop.setVisibility(View.GONE);
		buttonPlay.setVisibility(View.VISIBLE);

		currentPause = 0;
		countdownBackground.setVisibility(View.VISIBLE);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateTimer();
			}
		}, 0, 1000);
	}

	public void stopMusic(View view) {
		resetGame();
		music.pause();
		buttonPause.setVisibility(View.GONE);
		buttonStop.setVisibility(View.GONE);
		buttonPlay.setVisibility(View.VISIBLE);
		pauseHandler.removeCallbacks(endRoundRunnable);
	}

	public void updateTimer() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				currentPause++;
				textPauseCountdown.setText(String.valueOf(pauseDurationNumber + 1 - currentPause));
				if (pauseDurationNumber + 1 == currentPause) {
					countdownBackground.setVisibility(View.GONE);
					timer.cancel();
					playMusic(null);
				} else {
					tic.seekTo(0);
					tic.start();
				}
			}
		});
	}

	public void resetGame() {
		//TODO reset game sliders
		music.seekTo(0);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void openFile(View view) {
		Toast.makeText(this, "Pressed...", Toast.LENGTH_SHORT).show();
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent , 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0){
			Uri customMusicUri = data.getData();
			if (customMusicUri != null){
				String pathFromUri = getRealPathFromURI(this, customMusicUri);
				customMusic = new MediaPlayer();
				try {
					customMusic.setDataSource(this, Uri.parse(pathFromUri));
					customMusic.prepare();
					textSongName.setText(song);
					textArtistName.setText(artist);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getRealPathFromURI(Context context, Uri contentUri) {
		String[] projection = { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST };
		CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int uriColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		int songColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
		int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		cursor.moveToFirst();
		song = cursor.getString(songColumnIndex);
		artist = cursor.getString(artistColumnIndex);
		return cursor.getString(uriColumnIndex);
	}
}
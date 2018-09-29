package osu.edu.rainbow;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Liz on 10/9/2017.
 * This is the "meat" fragment for playing the catch-phrase knockoff game
 */

public class RainbowFragment extends Fragment {
    private final int COUNT = 6; //number of color options
    private int colorIndex = 0;
    private int termIndex = 0;
    Button button = null;
    private static final int MUSIC_LENGTH_MILLI = 64000;
    private static final String KEY_COLOR_INDEX = "index";
    private static final String KEY_TERM_INDEX = "term_index";
    private static final String TAG = "RainbowFragment"; //for logging purposes
    private static boolean firstClick = true; //first time sceen is tapped, game starts

    private ArrayList<String> termsList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ensure that if the application is closed, the app will reopen with the same color and term
        if (savedInstanceState != null) {
            colorIndex = savedInstanceState.getInt(KEY_COLOR_INDEX, 0);
            termIndex = savedInstanceState.getInt(KEY_TERM_INDEX, 0) + 1;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        changeColor(button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rainbow, container, false);
        firstClick = true;

        // get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        // get all the children of terms
        DatabaseReference terms = myRef.child("terms");

        //get the list of terms from the database
        terms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    termsList.add(postSnapshot.getKey());
                }
                //randomized order each time
                Collections.shuffle(termsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button = (Button) v.findViewById(R.id.button_color);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modulo operator to cycle through the colors and terms
                colorIndex = (colorIndex + 1) % COUNT;
                termIndex = (termIndex + 1) % termsList.size();
                changeColor(button);
                changeWord(button);
                if(firstClick){
                    //start service and play music
                    getActivity().startService(new Intent(getActivity(), SoundService.class));
                    timedFragmentStart();
                    firstClick = false;
                }else{
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    toneG.release();
                }
            }
        });

        return v;
    }

    //cycle through colors, whose hex values are defined in colors.xml
    public void changeColor(Button button){
        if(colorIndex == 0){
            button.setBackgroundColor(getResources().getColor(R.color.red));
            button.setTextColor(getResources().getColor(R.color.white));
        }else if(colorIndex == 1){
            button.setBackgroundColor(getResources().getColor(R.color.orange));
            button.setTextColor(getResources().getColor(R.color.white));
        }else if(colorIndex == 2){
            button.setBackgroundColor(getResources().getColor(R.color.yellow));
            button.setTextColor(getResources().getColor(R.color.black));
        }else if(colorIndex == 3){
            button.setBackgroundColor(getResources().getColor(R.color.green));
            button.setTextColor(getResources().getColor(R.color.white));
        }else if(colorIndex == 4){
            button.setBackgroundColor(getResources().getColor(R.color.blue));
            button.setTextColor(getResources().getColor(R.color.white));
        }else if(colorIndex == 5){
            button.setBackgroundColor(getResources().getColor(R.color.purple));
            button.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void changeWord(Button button){
        int numTerms = termsList.size();
        button.setText(termsList.get(termIndex));
    }

    //plays the accelerating beeping sound in the background, and when the "music" ends, the end game
    //fragment is opened
    public void timedFragmentStart(){
        Handler mainLooperHandler = new Handler(Looper.getMainLooper());

        mainLooperHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity()!=null){
                    getActivity().stopService(new Intent(getActivity(), SoundService.class));

                    EndGameFragment endGameFragment = new EndGameFragment();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, endGameFragment)
                            .commit();
                }
            }
        }, MUSIC_LENGTH_MILLI);
    }

    //save the color and term so that if the user navigates into and out of the app, the color and term
    //are restored
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_COLOR_INDEX, colorIndex);
        savedInstanceState.putInt(KEY_TERM_INDEX, termIndex);
    }
}

package osu.edu.rainbow;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Liz on 5/2/2018.
 */

public class EndGameFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_end_game, container, false);

        Button restartButton = v.findViewById(R.id.button_restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RainbowFragment rainbowFragment = new RainbowFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, rainbowFragment)
                        .commit();
            }
        });


        return v;
    }

}

package com.logicline.tech.stube.ui.activities.playerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ActivityTestBinding;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.ui.adapters.RelatedVideoAdapter;

public class TestActivity extends AppCompatActivity {

    ActivityTestBinding binding;
    PlayerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        if (getIntent() != null){
            String videoId = "MhyEpZuWNGU";

            viewModel.loadRelatedVideos(videoId);

        }

        RelatedVideoAdapter adapter = new RelatedVideoAdapter(this);
        binding.rvTest.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTest.setAdapter(adapter);

        viewModel.getRelatedVideo().observe(this, new Observer<RelatedVideo>() {
            @Override
            public void onChanged(RelatedVideo relatedVideo) {
                if (relatedVideo!= null){
                    adapter.setData(relatedVideo.items);
                }
            }
        });


    }
}
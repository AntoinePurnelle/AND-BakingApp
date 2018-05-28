/*
 * Copyright 2018 Antoine PURNELLE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ouftech.bakingapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import net.ouftech.bakingapp.commons.BaseFragment;
import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.commons.Logger;
import net.ouftech.bakingapp.model.Recipe;
import net.ouftech.bakingapp.model.Step;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import icepick.State;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
@FragmentWithArgs
public class StepDetailFragment extends BaseFragment {

    Unbinder unbinder;

    @NonNull
    @Override
    protected String getLotTag() {
        return "StepDetailFragment";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.step_detail;
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_STEP_NUMBER = "stepNumber";

    @BindView(R.id.step_details_player_view)
    SimpleExoPlayerView playerView;
    @Nullable
    @BindView(R.id.step_details_image_view)
    AppCompatImageView imageView;
    @Nullable
    @BindView(R.id.step_details_description_tv)
    TextView descriptionTV;
    @Nullable
    @BindView(R.id.step_details_previous_button)
    AppCompatImageView previousButton;
    @Nullable
    @BindView(R.id.step_details_next_button)
    AppCompatImageView nextButton;

    SimpleExoPlayer exoPlayer;

    @Arg
    Recipe recipe;
    @Arg
    int stepNumber;

    private RequestManager requestManager;

    @State
    boolean startAutoPlay = true;
    @State
    int startWindow;
    @State
    long startPosition;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            recipe = getArguments().getParcelable(ARG_RECIPE);
            stepNumber = getArguments().getInt(ARG_STEP_NUMBER);

            Activity activity = this.getActivity();
            if (activity != null && activity.getActionBar() != null)
                activity.getActionBar().setTitle(getStep().shortDescription);
        }

        if (savedInstanceState == null)
            clearStartPosition();

        this.requestManager = Glide.with(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        if (rootView == null)
            return null;

        unbinder = ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    protected void initView() {
        Step step = getStep();

        if (step != null && descriptionTV != null)
            descriptionTV.setText(step.description);

        if (previousButton != null)
            previousButton.setVisibility(stepNumber == 0 ? View.GONE : View.VISIBLE);
        if (nextButton != null)
            nextButton.setVisibility(stepNumber >= CollectionUtils.getSize(recipe.steps) - 1 ? View.GONE : View.VISIBLE);

        initializePlayer();
        initialiseImageView();

    }

    private void initializePlayer() {

        Step step = getStep();

        if (getActivity() == null || step == null)
            return;

        if (TextUtils.isEmpty(step.videoUrl)) {
            playerView.setVisibility(View.GONE);

            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer.release();
            }

            clearStartPosition();
        } else {
            playerView.setVisibility(View.VISIBLE);

            if (exoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            }

            exoPlayer.setPlayWhenReady(startAutoPlay);

            playerView.setPlayer(exoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(step.videoUrl), new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            boolean hasStartPosition = startWindow != C.INDEX_UNSET;
            if (hasStartPosition) {
                exoPlayer.seekTo(startWindow, startPosition);
            }
            exoPlayer.prepare(mediaSource, !hasStartPosition, false);
        }
    }

    private void initialiseImageView() {

        Step step = getStep();

        if (getActivity() == null || step == null || imageView == null)
            return;

        if (TextUtils.isEmpty(step.thumbnailUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            URL url;
            try {
                url = new URL(step.thumbnailUrl);
                String imageURL = url.toString();
                requestManager
                        .load(imageURL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                    target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imageView);
            } catch (MalformedURLException e) {
                Logger.w(getLotTag(), "Error while parsing URL", e);
                imageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            updateStartPosition();
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateStartPosition();
        releasePlayer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (startWindow != C.INDEX_UNSET)
            initializePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Nullable
    private Step getStep() {
        if (recipe == null)
            return null;

        if (!CollectionUtils.contains(recipe.steps, stepNumber))
            return null;

        return recipe.steps.get(stepNumber);
    }

    @Optional
    @OnClick(R.id.step_details_previous_button)
    public void onStepDetailsPreviousButtonClicked() {
        if (recipe == null || stepNumber == 0)
            return;

        stepNumber--;
        if (getArguments() != null)
            getArguments().putInt(ARG_STEP_NUMBER, stepNumber);

        clearStartPosition();
        initView();
    }

    @Optional
    @OnClick(R.id.step_details_next_button)
    public void onStepDetailsNextButtonClicked() {
        if (recipe == null || stepNumber >= CollectionUtils.getSize(recipe.steps))
            return;

        stepNumber++;
        if (getArguments() != null)
            getArguments().putInt(ARG_STEP_NUMBER, stepNumber);

        clearStartPosition();
        initView();
    }

    private void updateStartPosition() {
        if (exoPlayer != null) {
            startAutoPlay = exoPlayer.getPlayWhenReady();
            startWindow = exoPlayer.getCurrentWindowIndex();
            startPosition = Math.max(0, exoPlayer.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }
}

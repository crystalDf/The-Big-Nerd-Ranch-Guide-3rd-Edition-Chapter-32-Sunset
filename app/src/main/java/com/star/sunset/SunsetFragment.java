package com.star.sunset;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mShadowView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private int mHeatSunColor;
    private int mColdSunColor;

    private boolean mSunset = true;

    public static final long DURATION = 3000;

    private float mSunYCurrent = Float.NaN;
    private float mShadowYCurrent = Float.NaN;

    private int mSunsetSkyColorCurrent;
    private int mNightSkyColorCurrent;

    private AnimatorSet mSunsetAnimatorSet;
    private AnimatorSet mSunriseAnimatorSet;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mShadowView = view.findViewById(R.id.shadow);
        mSkyView = view.findViewById(R.id.sky);

        mSceneView.setOnClickListener(v -> {
            if (mSunset) {
                startSunsetAnimation();
                if (mSunriseAnimatorSet != null) {
                    mSunriseAnimatorSet.end();
                    mSunriseAnimatorSet = null;
                }
            } else {
                startSunriseAnimation();
                if (mSunsetAnimatorSet != null) {
                    mSunsetAnimatorSet.end();
                    mSunsetAnimatorSet = null;
                }
            }
            mSunset = !mSunset;
            startSunPulsateAnimation();
        });

        if (getActivity() == null) {
            return null;
        }

        mBlueSkyColor = ContextCompat.getColor(getActivity(), R.color.blue_sky);
        mSunsetSkyColor = ContextCompat.getColor(getActivity(), R.color.sun_set_sky);
        mNightSkyColor = ContextCompat.getColor(getActivity(), R.color.night_sky);

        mHeatSunColor = ContextCompat.getColor(getActivity(), R.color.heat_sun);
        mColdSunColor = ContextCompat.getColor(getActivity(), R.color.cold_sun);

        return view;
    }

    private void startSunriseAnimation() {
        float sunYStart = isNaNCheck(mSunYCurrent, mSkyView.getHeight(), mSunYCurrent);
        float sunYEnd = mSunView.getTop();

        float shadowYStart = isNaNCheck(mShadowYCurrent, -mShadowView.getHeight(), mShadowYCurrent);
        float shadowYEnd = mShadowView.getTop();

        int sunriseSkyColorStart = (int) isNaNCheck(mSunYCurrent, mSunsetSkyColor, mSunsetSkyColorCurrent);
        int sunriseSkyColorStop = mBlueSkyColor;

        long sunriseDuration = (long) isNaNCheck(mSunYCurrent, DURATION,
                ((float) DURATION) / (mSunView.getTop() - mSkyView.getHeight())
                        * (mSunView.getTop() - mSunYCurrent));

        int nightSkyColorStart = (mNightSkyColorCurrent == 0 ?
                mNightSkyColor : mNightSkyColorCurrent);
        int nightSkyColorStop = mSunsetSkyColor;

        long nightDuration = (long) (mNightSkyColorCurrent == 0 ? DURATION :
                ((float) DURATION) / (mSunsetSkyColor - mNightSkyColor)
                        * (mSunsetSkyColor - mNightSkyColorCurrent));

        ObjectAnimator sunHeightAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                sunYStart, sunYEnd)
                .setDuration(sunriseDuration);

        sunHeightAnimator.setInterpolator(new DecelerateInterpolator());
        sunHeightAnimator.addUpdateListener(animation ->
                mSunYCurrent = (float) animation.getAnimatedValue());

        ObjectAnimator shadowHeightAnimator = ObjectAnimator.ofFloat(mShadowView, "y",
                shadowYStart, shadowYEnd)
                .setDuration(sunriseDuration);

        shadowHeightAnimator.setInterpolator(new DecelerateInterpolator());
        shadowHeightAnimator.addUpdateListener(animation ->
                mShadowYCurrent = (float) animation.getAnimatedValue());

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), sunriseSkyColorStart, sunriseSkyColorStop)
                .setDuration(sunriseDuration);

        sunriseSkyAnimator.addUpdateListener(animation ->
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue());

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), nightSkyColorStart, nightSkyColorStop)
                .setDuration(nightDuration);

        nightSkyAnimator.addUpdateListener(animation ->
                mNightSkyColorCurrent = (int) animation.getAnimatedValue());

        mSunriseAnimatorSet = new AnimatorSet();
        mSunriseAnimatorSet
                .play(sunHeightAnimator)
                .with(shadowHeightAnimator)
                .with(sunriseSkyAnimator)
                .after(nightSkyAnimator);

        mSunriseAnimatorSet.start();
    }

    private void startSunsetAnimation() {
        float sunYStart = isNaNCheck(mSunYCurrent, mSunView.getTop(), mSunYCurrent);
        float sunYEnd = mSkyView.getHeight();

        float shadowYStart = isNaNCheck(mShadowYCurrent, mShadowView.getTop(), mShadowYCurrent);
        float shadowYEnd = -mShadowView.getHeight();

        int sunsetSkyColorStart = (mSunsetSkyColorCurrent == 0 ?
                mBlueSkyColor : mSunsetSkyColorCurrent);
        int sunsetSkyColorStop = mSunsetSkyColor;

        long sunsetDuration = (long) isNaNCheck(mSunYCurrent, DURATION,
                ((float)DURATION) / (mSkyView.getHeight() - mSunView.getTop())
                        * (mSkyView.getHeight() - mSunYCurrent));

        int nightSkyColorStart = (mNightSkyColorCurrent == 0 ?
                mSunsetSkyColor : mNightSkyColorCurrent);
        int nightSkyColorStop = mNightSkyColor;

        long nightDuration = (long) (mNightSkyColorCurrent == 0 ? DURATION :
                ((float) DURATION) / (mNightSkyColor - mSunsetSkyColor)
                        * (mNightSkyColor - mNightSkyColorCurrent));

        ObjectAnimator sunHeightAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                sunYStart, sunYEnd)
                .setDuration(sunsetDuration);

        sunHeightAnimator.setInterpolator(new AccelerateInterpolator());
        sunHeightAnimator.addUpdateListener(animation ->
                mSunYCurrent = (float) animation.getAnimatedValue());

        ObjectAnimator shadowHeightAnimator = ObjectAnimator.ofFloat(mShadowView, "y",
                shadowYStart, shadowYEnd)
                .setDuration(sunsetDuration);

        shadowHeightAnimator.setInterpolator(new AccelerateInterpolator());
        shadowHeightAnimator.addUpdateListener(animation ->
                mShadowYCurrent = (float) animation.getAnimatedValue());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), sunsetSkyColorStart, sunsetSkyColorStop)
                .setDuration(sunsetDuration);

        sunsetSkyAnimator.addUpdateListener(animation ->
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue());

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), nightSkyColorStart, nightSkyColorStop)
                .setDuration(nightDuration);

        nightSkyAnimator.addUpdateListener(animation ->
                mNightSkyColorCurrent = (int) animation.getAnimatedValue());

        mSunsetAnimatorSet = new AnimatorSet();
        mSunsetAnimatorSet
                .play(sunHeightAnimator)
                .with(shadowHeightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);

        mSunsetAnimatorSet.start();
    }

    private void startSunPulsateAnimation() {
        ObjectAnimator sunPulsateAnimator = ObjectAnimator.ofObject(mSunView, "backgroundColor",
                new ArgbEvaluator(), mColdSunColor, mHeatSunColor)
                .setDuration(1000);

        sunPulsateAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sunPulsateAnimator.setRepeatCount(ValueAnimator.INFINITE);

        sunPulsateAnimator.start();
    }

    private float isNaNCheck(float checkValue, float isNaNValue, float isANValue) {
        return Float.valueOf(checkValue).isNaN() ? isNaNValue : isANValue;
    }
}

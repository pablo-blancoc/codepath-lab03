package com.codepath.android.lollipopexercise.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.models.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private Contact mContact;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvPhone;
    private View vPalette;
    private FloatingActionButton btnCall;
    private Transition.TransitionListener transitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);
        btnCall = findViewById(R.id.fab);
        btnCall.setVisibility(View.INVISIBLE);

        // Extract contact from bundle
        mContact = (Contact) getIntent().getExtras().getSerializable(EXTRA_CONTACT);

        // Fill views with data
        // Glide.with(DetailsActivity.this).load(mContact.getThumbnailDrawable()).centerCrop().into(ivProfile);
        Glide.with(DetailsActivity.this).asBitmap().load(mContact.getThumbnailDrawable()).centerCrop().into( new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                    // TO DO 1. Instruct Glide to load the bitmap into the `holder.ivProfile` profile image view
                    Glide.with(DetailsActivity.this)
                            .load(resource)
                            .into(ivProfile);

                    // TO DO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                    Palette palette = Palette.from(resource).generate();

                    // Set the result as the background color for `vPalette` view containing the contact's name.
                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                    if (vibrant != null) {
                        // Set the background color of a layout based on the vibrant color
                        vPalette.setBackgroundColor(vibrant.getRgb());
                        // Update the title TextView with the proper text color
                        tvName.setTextColor(vibrant.getTitleTextColor());
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // can leave empty
                }
            }
        );
        tvName.setText(mContact.getName());
        tvPhone.setText(mContact.getNumber());

        // Call FAB button
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mContact.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        // Transition Listener
        transitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };

        // Activate animation on Enter
        getWindow().getEnterTransition().addListener(transitionListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // finishAfterTransition();
            exitReveal();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }

    void enterReveal() {
        // get the center for the clipping circle
        int cx = btnCall.getMeasuredWidth() / 2;
        int cy = btnCall.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(btnCall.getWidth(), btnCall.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(btnCall, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        btnCall.setVisibility(View.VISIBLE);

        // Add Listener for button animation
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(transitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        anim.start();
    }

    void exitReveal() {
        // get the center for the clipping circle
        int cx = btnCall.getMeasuredWidth() / 2;
        int cy = btnCall.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = btnCall.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(btnCall, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnCall.setVisibility(View.INVISIBLE);
            }
        });

        // Add listener to finish activity when animation finished
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finishAfterTransition();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        // start the animation
        anim.start();
    }

}

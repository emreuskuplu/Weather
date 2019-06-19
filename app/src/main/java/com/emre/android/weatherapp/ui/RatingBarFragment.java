package com.emre.android.weatherapp.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.emre.android.weatherapp.R;

public class RatingBarFragment extends DialogFragment {
    private static final String TAG = RatingBarFragment.class.getSimpleName();

    public static RatingBarFragment newInstance() {
        return new RatingBarFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_ratingbar, null);

        Button sendButton = v.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(v);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }

        return dialog;
    }
}
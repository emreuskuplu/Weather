package com.emre.android.weatherapp.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dao.LocationDAO;

public class DeleteAllBookmarksDialogFragment extends DialogFragment {
    private static final String TAG = DeleteAllBookmarksDialogFragment.class.getSimpleName();

    public static DeleteAllBookmarksDialogFragment newInstance() {
        return new DeleteAllBookmarksDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_delete_all_bookmarks, null);

        final LocationDAO locationDAO = new LocationDAO(requireContext());

        Button yesButton = v.findViewById(R.id.yes_button);
        Button noButton = v.findViewById(R.id.no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDAO.LocationDbDeleteAllLocationData();

                showAllBookmarksDeletedToast();

                if (getDialog() != null) {
                    getDialog().cancel();
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().cancel();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(v);

        return builder.create();
    }

    private void showAllBookmarksDeletedToast() {
        Toast.makeText(requireContext(),
                R.string.all_bookmarks_deleted,
                Toast.LENGTH_LONG).show();
    }
}

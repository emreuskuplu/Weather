/*
 * Copyright (c) 2019. Emre Üsküplü
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.emre.android.weatherapp.scenes.settings.dialog;

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
import com.emre.android.weatherapp.dataaccessobjects.locationdataaccess.LocationDAO;

/**
 * @author Emre Üsküplü
 *
 * Creates dialog for delete all bookmarks in list
 */
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

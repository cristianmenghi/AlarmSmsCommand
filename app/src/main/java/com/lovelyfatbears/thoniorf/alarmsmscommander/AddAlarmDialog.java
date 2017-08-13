package com.lovelyfatbears.thoniorf.alarmsmscommander;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by thoniorf on 7/29/17.
 */

public class AddAlarmDialog extends AppCompatDialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(AddAlarmDialog dialog);
        public void onDialogNegativeClick(AddAlarmDialog dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    TextView name;
    EditText number;
    EditText password;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view =inflater.inflate(R.layout.add_dialog, null);
        builder.setView(view)

                .setPositiveButton(R.string.add_dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(AddAlarmDialog.this);
                    }
                })
                .setNegativeButton(R.string.add_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(AddAlarmDialog.this);
                    }
                });

        name = (TextView) view.findViewById(R.id.add_dialog_txt_name);
        number = (EditText) view.findViewById(R.id.add_dialog_txt_number);
        password = (EditText) view.findViewById(R.id.add_dialog_txt_password);
        return builder.create();
    }
}

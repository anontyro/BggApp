package co.alexwilkinson.bgguserapp.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import co.alexwilkinson.bgguserapp.R;

/**
 * Created by Alex on 03/11/2016.
 */

public class CreateUserDialogFrame extends DialogFragment{
    EditText etUsername;

    /**
     * Interface to allow user to received event call backs implementing both the buttons used
     * this will create parameters for the positive and negative call back
     */
    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);

    }

    public interface OnCompleteListener{
        public void onComplete(String username);
    }

    OnCompleteListener completeListener;
    NoticeDialogListener mListener;

    /**
     * Used to enforce the interface implemention
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            completeListener =(OnCompleteListener) context;
            mListener = (NoticeDialogListener) context;
        }
        catch (ClassCastException ex){
            throw new ClassCastException(context.toString()
                    + "must implement NoticeDialogListener");
        }
    }

    /**
     * Static method used to send the username already found in the application if it exists
     * @param title valid bgg username
     * @return creates a new instance of the dialog with the username saved
     */
    public static CreateUserDialogFrame setUsername(String title){
        CreateUserDialogFrame myFragment = new CreateUserDialogFrame();
        Bundle args = new Bundle();
        args.putString("username",title);
        myFragment.setArguments(args);
        return myFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String username = getArguments().getString("username");
        System.out.println(username);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        final View view = inflater.inflate(R.layout.create_user_dialog,null);


        etUsername = (EditText) view.findViewById(R.id.etUsername);
        etUsername.setText(username);
        etUsername.hasFocus();
        etUsername.selectAll();


        builder.setView(view);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                completeListener.onComplete(etUsername.getText().toString());
                mListener.onDialogPositiveClick(CreateUserDialogFrame.this);

            }
        })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(CreateUserDialogFrame.this);
                        CreateUserDialogFrame.this.getDialog().cancel();
                    }
                })

        ;

        return builder.create();
    }
}

package com.roaaserver.placementstudent.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.roaaserver.placementstudent.R;

import java.util.ArrayList;

public class EditMultipleChoiceFragment extends DialogFragment {

   public interface onMultiChoiceListener{
      void onPositiveButtonClicked(String[] list,ArrayList<String> selectedItemList);
      void onNegativeButtonClicked();
   }

   onMultiChoiceListener listener;

   @Override
   public void onAttach(@NonNull Context context) {
      super.onAttach(context);
      try {
         listener =(onMultiChoiceListener) context;
      } catch (Exception e) {
         throw new ClassCastException(getActivity().toString()+"onMultiChoiceListener must implement");
      }
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      final ArrayList<String> selectedItemList = new ArrayList<>();
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      String[] list = getActivity().getResources().getStringArray(R.array.edit_choice);


      builder.setTitle("Select fields for edit")
              .setMultiChoiceItems(list, null, new DialogInterface.OnMultiChoiceClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                    if(isChecked){
                       selectedItemList.add(list[i]);
                    }else{
                       selectedItemList.remove(list[i]);
                    }
                 }
              })
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                    listener.onPositiveButtonClicked(list,selectedItemList);
                 }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                    listener.onNegativeButtonClicked();
                 }
              });
      return builder.create();

   }
}

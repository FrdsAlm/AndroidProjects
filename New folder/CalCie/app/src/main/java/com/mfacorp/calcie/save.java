package com.mfacorp.calcie;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.widget.Toast.*;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;


/**
 * A simple {@link Fragment} subclass.
 */
public class save extends Fragment {

    MaterialButton buView,buSave;
    TextInputLayout LayoutEtSave;
    TextInputEditText etSave;
    DatabaseHelper mDatabaseHelper;
    ListView mListView;
    SwipeRefreshLayout swipeRefreshLayout;



    public save() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragmentsave, container, false);
        super.onCreate(savedInstanceState);
        buView=v.findViewById(R.id.buView);
        buSave=v.findViewById(R.id.buSave);
        LayoutEtSave=v.findViewById(R.id.LayoutSaveEt);
        etSave=v.findViewById(R.id.etSave);
        mDatabaseHelper = new DatabaseHelper(getActivity());
        mListView =v.findViewById(R.id.mListView);
        swipeRefreshLayout=v.findViewById(R.id.fragmentsave);
        populateListView();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateListView();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });






        buSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String newEntry = Objects.requireNonNull(etSave.getText()).toString();
                if (etSave.length() != 0) {
                    AddData(newEntry);

                    etSave.setText("");
                } else {
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                            "Nothing is there to save", LENGTH_LONG).show();
                }
                InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
                populateListView();
            }

        });

        buView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                populateListView();

            }




        });




        return v;
    }

    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }



    public void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter

        ArrayAdapter<String> adapter=new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,listData);
        mListView.setAdapter(adapter);


        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + name);

                Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    MyCustomDialog dialog = new MyCustomDialog();
                    assert getFragmentManager() != null;
                    dialog.show(getFragmentManager(),"MyCustomDialog");
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemID", itemID);
                    bundle.putString("name", name);
                    dialog.setArguments(bundle);

                }
                else{
                    toastMessage("No ID associated with that name");
                }
            }
        });
    }



    private void toastMessage(String message){
        Toast.makeText(getActivity(),message, LENGTH_SHORT).show();
    }



}













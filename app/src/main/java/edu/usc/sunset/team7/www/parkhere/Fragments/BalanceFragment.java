package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.v7.appcompat.R.styleable.View;

/**
 * Created by Acer on 10/29/2016.
 */

public class BalanceFragment extends Fragment {

    @BindView(R.id.transfer_button)
    Button transferBalanceButton;

    @BindView(R.id.current_balance)
    AppCompatTextView currentBalanceNumber;

    private double userBalance;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private String uid;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        System.out.println(uid);
        userBalance = 0.0;

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE+"/"+uid);
        dbRef.orderByChild(Consts.USER_BALANCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().equals(Consts.USER_BALANCE)) {
                        System.out.println(child.getValue().toString());
                        userBalance = Double.parseDouble(child.getValue().toString());
                        break;
                    }
                }
                String formattedBalance = "$" + String.format("%.2f", userBalance);
                currentBalanceNumber.setText(formattedBalance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.transfer_button)
    protected void transferToBank(){

        String formattedBalance = "$" + String.format("%.2f", userBalance);
        Toast.makeText(getActivity().getApplicationContext(),
                "You transferred " + formattedBalance + " to the bank!",
                Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference balanceRef = mDatabase.child(Consts.USERS_DATABASE).child(uid);
        balanceRef.child(Consts.USER_BALANCE).setValue(0.0);
        userBalance = 0.0;
        formattedBalance = "$" + String.format("%.2f", userBalance);
        currentBalanceNumber.setText(formattedBalance);
    }
}

package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    public void onCreate(Bundle savedBundleInstance) { super.onCreate(savedBundleInstance); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_fragment, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        userBalance = 0.0;

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE+"/"+uid);
        dbRef.orderByChild(Consts.USER_BALANCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().equals(Consts.USER_BALANCE)) {
                        userBalance = Double.valueOf(child.getValue().toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return null;
    }

    @OnClick(R.id.transfer_button)
    protected void transferToBank(){
        //currentUser = the firebase used
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference balanceRef = mDatabase.child(Consts.USERS_DATABASE).child(uid);
        balanceRef.child(Consts.USER_BALANCE).setValue(0.0);
        currentBalanceNumber.setText("0.0");
    }
}

package com.example.cryptchat.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptchat.Adapter.UserAdapter;
import com.example.cryptchat.Model.Contacts;
import com.example.cryptchat.Model.Users;
import com.example.cryptchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    // initial, 0 = ascuns ; 1 = vizibil
    public static int flag = 0;

    // Widgets
    private EditText searchField;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<Contacts> contactList;

    private ImageButton searchButton;

    private DataSnapshot lastQueriedItem;

    // Firebase Instantiations
    FirebaseUser fUser;
    private DatabaseReference databaseReference;
    private String userid;


    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchField = view.findViewById(R.id.search_field);

        FloatingActionButton floatingBtn = view.findViewById(R.id.float_search);
        searchButton = view.findViewById(R.id.search_button);

        // hide the 2 search items unless the user doesn't click on the Floating Search Button
        searchField.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        contactList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts").child(fUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               contactList.clear();

                // Loop for all users in order to bring their chats into the Contacts fragment:
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Contacts contacts = snap.getValue(Contacts.class);
                    contactList.add(contacts);
                }

                readContacts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        ReadUsers();

        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                // managing manually the keyboard, showing before typing and hiding it afterwards
                searchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (v == searchField) {
                            if (hasFocus) {
                                // Open keyboard
                                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchField, InputMethodManager.SHOW_FORCED);
                            }
                            else {
                                // Close keyboard
                                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchField.getWindowToken(), 0);

                            }
                        }
                    }
                });

                // if the flag is 0, that means the search items are hidden and we can show them
                if (flag == 0)
                {
                    searchField.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.VISIBLE);
                    // showing the keyboard manually
                    setSearchFieldFocus(true);
                    flag = 1;

                    // Searching for a Contact by ID to add the Contact in our list

                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (searchField != null) {
                                String searchText = searchField.getText().toString();
                                {
                                    if (!searchText.equals(""))
                                        firebaseUserSearch(searchText);
                                }
                            }

                        }
                    });

                }
                // else, hide everything and clear the textField
                else
                {
                    searchField.setVisibility(View.GONE);
                    searchButton.setVisibility(View.GONE);
                    searchField.setText("");
                    // hiding the keyboard manually
                    setSearchFieldFocus(false);
                    flag = 0;
                }

            }
        });

        return view;
    }

    private void firebaseUserSearch(String searchText) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers");

        Query searchUserQuery = reference.orderByChild("id").startAt(searchText).endAt(searchText + "\uf8ff");

        searchUserQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    assert  user != null;

                    // checking if the ID is not the user ID running the app,
                    // then we add the user to the list (not adding ourself into the list)
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
//                    mUsers.clear();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // Method to return all of the users from the Firebase DB
    private void readContacts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers");

        mUsers = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);

                    for (Contacts contacts : contactList) {
                        if(user.getId().equals(contacts.getId())) {
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Contacts")
                .child(fUser.getUid())
                .child(userid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if the chats doesn't exist in the Chat Fragment, we add it searching by the userid
                if (!dataSnapshot.exists()) {
                    ref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    assert  user != null;

                    for (ChatList chatList : contactList) {
                        if (user.getId().equals(chatList.getId())) {
                            mUsers.add(user);
                        }
                    }


//                    // checking if the ID is not the user ID running the app,
//                    // then we add the user to the list (not adding ourself into the list)
//                    assert firebaseUser != null;
//                    if (!user.getId().equals(firebaseUser.getUid())){
//                        mUsers.add(user);
//                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        */

    }

    private void setSearchFieldFocus(boolean isFocused) {
        searchField.setCursorVisible(isFocused);
        searchField.setFocusable(isFocused);
        searchField.setFocusableInTouchMode(isFocused);

        if (isFocused) {
            searchField.requestFocus();
        }
    }

}

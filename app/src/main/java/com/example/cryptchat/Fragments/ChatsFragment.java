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
import com.example.cryptchat.Model.ChatList;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ChatsFragment extends Fragment {

    // initial, 0 = ascuns ; 1 = vizibil
    private static int flag = 0;
    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<ChatList> usersList;

    // Firebase Instantiations
    FirebaseUser fUser;
    DatabaseReference databaseReference;

    // Widgets
    private RecyclerView recyclerView;
    private EditText searchField;
    private ImageButton searchButton;
    private FloatingActionButton floatingBtn;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchField = view.findViewById(R.id.search_field);
        floatingBtn = view.findViewById(R.id.float_search);
        searchButton = view.findViewById(R.id.search_button);

        // hide the 2 search items unless the user doesn't click on the Floating Search Button
        searchField.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        // hide the keyboard as well
        setSearchFieldFocus(false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                // Loop for all users in order to bring their chats into the Chats fragment:
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    usersList.add(chatList);

                }

                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                                if (!searchText.equals("") && !searchText.equals(fUser.getUid()))
                                {
                                    firebaseUserSearch(searchText);
//                                        Toast.makeText( getContext() ,"Search started", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (searchField == null) {
                            chatList();
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
                    chatList();
                }

            }
        });

        return view;

    }

    // Method to Get all the recent Chats with our contacts into the Chat Fragment
    private void chatList() {

        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    // Passing the Modal class for ChatList
                    for (ChatList chatList : usersList) {

                        if (user.getId().equals(chatList.getId())) {
                            mUsers.add(user);

                        }
                    }
                }

                // Sort the Contacts by the Online/ Offline status
                Collections.sort(mUsers, new Comparator<Users>() {
                    @Override
                    public int compare(Users o1, Users o2) {
                        return o1.getStatus().compareTo(o2.getStatus());
                    }
                });
                Collections.reverse(mUsers);

                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);

                searchField.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void firebaseUserSearch(String searchText) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers");

        Query searchUserQuery = reference.orderByChild("id").startAt(searchText).endAt(searchText + "\uf8ff");

        searchUserQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    assert  user != null;

                    // checking if the ID is not the user ID running the app,
                    // then we add the user to the list (not adding ourselves into the list)
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

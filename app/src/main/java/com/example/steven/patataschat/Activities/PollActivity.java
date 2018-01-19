package com.example.steven.patataschat.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steven.patataschat.Adapters.PollsAdapter;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Poll;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class PollActivity extends AppCompatActivity {

    private final ArrayList<Poll> polls = new ArrayList<>();
    private final ArrayList<Users> users = new ArrayList<>();
    private ArrayList<RadioButton> options_rb;
    private ArrayList<RadioButton> options_create;
    private ArrayList<ImageButton> buttons_create;
    private ArrayList<TextView> results_tv;
    private ArrayList<EditText> titles_create;
    private ArrayList<LinearLayout> layouts_create;
    private ArrayList<String> options_list;
    private ArrayList<ProgressBar> progress_list;
    private RecyclerView polls_recycler;
    private LinearLayout options_layout;
    private PollsAdapter poll_adapter;
    private FirebaseAuth auth_service;
    private DatabaseReference userRef;
    private DatabaseReference pollRef;
    private ChildEventListener pollChild;
    private ChildEventListener userChild;
    private ValueEventListener userValue;
    private RadioGroup options_group;
    private int current_poll_position;
    private int previous_selected = -1;
    private Button submit;
    private EditText title_create;
    private String chat_name;
    private String name;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        dialog = ProgressDialog.show(PollActivity.this,"","Loading polls...",true);
        instantiate();
        this.auth_service = FirebaseAuth.getInstance();
        this.polls_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        this.pollRef = FirebaseDatabase.getInstance().getReference("polls");
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
        this.chat_name = getIntent().getExtras().getString("chat_name");
        this.name = getIntent().getExtras().getString("user_name");
        iniReferences();
        current_poll_position = -1;
        this.options_layout.setVisibility(View.GONE);
    }

    public void iniArrays(){
        this.options_rb.add((RadioButton) findViewById(R.id.opt_1));
        this.options_rb.add((RadioButton) findViewById(R.id.opt_2));
        this.options_rb.add((RadioButton) findViewById(R.id.opt_3));
        this.options_rb.add((RadioButton) findViewById(R.id.opt_4));
        this.options_rb.add((RadioButton) findViewById(R.id.opt_5));
        this.results_tv.add((TextView) findViewById(R.id.result_1));
        this.results_tv.add((TextView) findViewById(R.id.result_2));
        this.results_tv.add((TextView) findViewById(R.id.result_3));
        this.results_tv.add((TextView) findViewById(R.id.result_4));
        this.results_tv.add((TextView) findViewById(R.id.result_5));
        this.options_create.add((RadioButton) findViewById(R.id.option_1));
        this.options_create.add((RadioButton) findViewById(R.id.option_2));
        this.options_create.add((RadioButton) findViewById(R.id.option_3));
        this.options_create.add((RadioButton) findViewById(R.id.option_4));
        this.options_create.add((RadioButton) findViewById(R.id.option_5));
        this.titles_create.add((EditText) findViewById(R.id.text_1));
        this.titles_create.add((EditText) findViewById(R.id.text_2));
        this.titles_create.add((EditText) findViewById(R.id.text_3));
        this.titles_create.add((EditText) findViewById(R.id.text_4));
        this.titles_create.add((EditText) findViewById(R.id.text_5));
        this.buttons_create.add((ImageButton) findViewById(R.id.button_1));
        this.buttons_create.add((ImageButton) findViewById(R.id.button_2));
        this.buttons_create.add((ImageButton) findViewById(R.id.button_3));
        this.buttons_create.add((ImageButton) findViewById(R.id.button_4));
        this.buttons_create.add((ImageButton) findViewById(R.id.button_5));
        this.layouts_create.add((LinearLayout) findViewById(R.id.layout_1));
        this.layouts_create.add((LinearLayout) findViewById(R.id.layout_2));
        this.layouts_create.add((LinearLayout) findViewById(R.id.layout_3));
        this.layouts_create.add((LinearLayout) findViewById(R.id.layout_4));
        this.layouts_create.add((LinearLayout) findViewById(R.id.layout_5));
        this.progress_list.add((ProgressBar) findViewById(R.id.progress_1));
        this.progress_list.add((ProgressBar) findViewById(R.id.progress_2));
        this.progress_list.add((ProgressBar) findViewById(R.id.progress_3));
        this.progress_list.add((ProgressBar) findViewById(R.id.progress_4));
        this.progress_list.add((ProgressBar) findViewById(R.id.progress_5));
    }

    public void instantiate(){
        this.options_layout = findViewById(R.id.options_layout);
        this.polls_recycler = findViewById(R.id.poll_recycler);
        this.options_group = findViewById(R.id.radio_group_1);
        this.submit = findViewById(R.id.submit_vote);
        this.title_create = findViewById(R.id.title);
        this.options_rb = new ArrayList<>();
        this.options_list = new ArrayList<>();
        this.results_tv = new ArrayList<>();
        this.options_create = new ArrayList<>();
        this.titles_create = new ArrayList<>();
        this.buttons_create = new ArrayList<>();
        this.layouts_create = new ArrayList<>();
        this.progress_list = new ArrayList<>();
        iniArrays();
    }

    public void show_options(Poll poll){
        int size = poll.getOptions().size();
        RadioButton option;
        for(int i = 0; i<size; i++){
            option = options_rb.get(i);
            option.setText(poll.getOptionTitle(i));
            if(option.getVisibility() == View.GONE){
                option.setVisibility(View.VISIBLE);
            }
        }
        for(int i = size; i < 5; i++){
            option = options_rb.get(i);
            option.setText("");
            if(option.getVisibility() == View.VISIBLE){
                option.setVisibility(View.GONE);
            }
        }
        for(int i = 0; i < 5; i++){
            results_tv.get(i).setVisibility(View.GONE);
            progress_list.get(i).setVisibility(View.GONE);
        }
        options_group.clearCheck();
        if(submit.getVisibility() == View.GONE){
            submit.setVisibility(View.VISIBLE);
        }
    }

    public void show_details(Poll poll){
        int total = poll.getVoted_users_list().size();
        int size = poll.getOptions().size();
        for(int i = 0; i < size; i++){
            float percentage = ((float)poll.getOptionVotedCount(i)/total)*100;
            options_rb.get(i).setVisibility(View.GONE);
            results_tv.get(i).setVisibility(View.VISIBLE);
            progress_list.get(i).setVisibility(View.VISIBLE);
            String option_detail = poll.getOptionTitle(i);
            results_tv.get(i).setText(option_detail);
            progress_list.get(i).setProgress((int) percentage);
        }
        for(int i = size; i < 5; i++){
            options_rb.get(i).setVisibility(View.GONE);
            results_tv.get(i).setVisibility(View.GONE);
            progress_list.get(i).setVisibility(View.GONE);
        }
        if(submit.getVisibility() == View.VISIBLE){
            submit.setVisibility(View.GONE);
        }
    }

    public void show_choice(int position, int visibility){
        titles_create.get(position).setText("");
        options_create.get(position).setChecked(false);
        titles_create.get(position).setVisibility(visibility);
        options_create.get(position).setVisibility(visibility);
    }

    public void output_option(int array_position, int visibility){
        switch(visibility){
            case View.VISIBLE:
                show_choice(array_position,View.VISIBLE);
                buttons_create.get(array_position).setImageDrawable(getDrawable(R.drawable.ic_cancel_black_24dp));
                if(array_position < 4){
                    layouts_create.get(array_position+1).setVisibility(View.VISIBLE);
                }
                break;
            case View.GONE:
                if(array_position == 4){
                    show_choice(array_position,View.GONE);
                }else{
                    int first_invisible = getFirstInvisibleItem();
                    int last_visible = first_invisible-1;
                    if(first_invisible == 3){
                        //Toast cant have less than 2 options
                    }else{
                        for(int i = array_position; i < last_visible; i++){
                            titles_create.get(i).setText(titles_create.get(i+1).getText());
                        }
                        changeLastOption(last_visible);
                    }
                }
                break;
        }
    }

    public void changeLastOption(int last_visible){
        if(titles_create.get(last_visible).getVisibility() == View.GONE){
            layouts_create.get(last_visible).setVisibility(View.GONE);
            show_choice(last_visible-1,View.GONE);
            buttons_create.get(last_visible-1).setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_24dp));
        }else{
            show_choice(last_visible,View.GONE);
            buttons_create.get(last_visible).setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_24dp));
        }
    }

    public int getFirstInvisibleItem(){
        for(int i = 0; i <= 4; i++){
            if(layouts_create.get(i).getVisibility() == View.GONE){
                return i;
            }
        }
        return 5;
    }

    public void change_check(View view){
        switch(view.getId()){
            case R.id.option_1:
                uncheck_others(0);
                break;
            case R.id.option_2:
                uncheck_others(1);
                break;
            case R.id.option_3:
                uncheck_others(2);
                break;
            case R.id.option_4:
                uncheck_others(3);
                break;
            case R.id.option_5:
                uncheck_others(4);
                break;
        }
    }

    public void uncheck_others(int position_to_not_change){
        for(int i = 0; i < 5; i++){
            if(i != position_to_not_change){
                options_create.get(i).setChecked(false);
            }else{
                options_create.get(i).setChecked(true);
            }
        }
    }

    public void anotherOption(View view){
        switch(view.getId()){
            case R.id.button_1:
                output_option(0,View.GONE);
                break;
            case R.id.button_2:
                output_option(1,View.GONE);
                break;
            case R.id.button_3:
                if(titles_create.get(2).getVisibility() == View.VISIBLE){
                    output_option(2,View.GONE);
                }else{
                    output_option(2,View.VISIBLE);
                }
                break;
            case R.id.button_4:
                if(titles_create.get(3).getVisibility() == View.VISIBLE){
                    output_option(3,View.GONE);
                }else{
                    output_option(3,View.VISIBLE);
                }
                break;
            case R.id.button_5:
                if(titles_create.get(4).getVisibility() == View.VISIBLE){
                    output_option(4,View.GONE);
                }else{
                    output_option(4,View.VISIBLE);
                }
                break;
        }
    }

    public boolean isAnyOptionEmpty(){
        for(int i = 0; i < 5; i++){
            String text = titles_create.get(i).getText().toString();
            if(titles_create.get(i).getVisibility() == View.VISIBLE && (text.isEmpty() || text.trim().isEmpty())){
                return true;
            }
        }
        return false;
    }

    public void setOptions(){
        for(int i = 0; i < 5; i++){
            if(titles_create.get(i).getVisibility() == View.VISIBLE){
                options_list.add(titles_create.get(i).getText().toString());
            }
        }
    }

    public boolean isAnOptionChecked(){
        for(int i = 0; i < 5; i++){
            if(options_create.get(i).getVisibility() == View.VISIBLE && options_create.get(i).isChecked()){
                return true;
            }
        }
        return false;
    }

    public void createPoll(View view){
        if(isAnyOptionEmpty()){
            Toast.makeText(this,R.string.poll_empty,Toast.LENGTH_SHORT).show();
        }else if(!isAnOptionChecked()){
            Toast.makeText(this, R.string.poll_no_option, Toast.LENGTH_SHORT).show();
        }else{
            String poll_title = title_create.getText().toString();
            setOptions();
            int size = options_list.size();
            if(size < 2){
                Toast.makeText(this,R.string.poll_minimum,Toast.LENGTH_SHORT).show();
            } else if (size > 5) {
                Toast.makeText(this,R.string.poll_max,Toast.LENGTH_SHORT).show();
            }else if(poll_title.isEmpty() || poll_title.trim().isEmpty()){
                Toast.makeText(this,R.string.poll_title,Toast.LENGTH_SHORT).show();
            }else{
                String pollID = pollRef.push().getKey();
                ArrayList<Integer> votes = Poll.create_options_count(size,getSelectedOption());
                ArrayList<String> users = new ArrayList<>();
                users.add(auth_service.getCurrentUser().getUid());
                Poll poll = new Poll(pollID,poll_title,true,options_list,votes,users);
                pollRef.child(pollID).setValue(poll);
                String message = name+" created the poll: "+poll_title;
                Messages.sendMessage(name,message,Messages.getDateFormatted(new Date()),chat_name,1);
                title_create.setText("");
                clearEverything();
                Toast.makeText(this,R.string.poll_created,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getSelectedOption(){
        for(int i = 0; i < 5; i++){
            if(options_create.get(i).isChecked()){
                return i;
            }
        }
        return 0;
    }

    public void clearEverything(){
        output_option(4,View.GONE);
        output_option(3,View.GONE);
        output_option(2,View.GONE);
    }

    public void submit_vote(View view){
        int option = options_group.getCheckedRadioButtonId();
        if(current_poll_position != -1){
            if(option != -1){
                Poll selected = polls.get(current_poll_position);
                switch(option){
                    case R.id.opt_1:
                        selected.increment_vote(0);
                        break;
                    case R.id.opt_2:
                        selected.increment_vote(1);
                        break;
                    case R.id.opt_3:
                        selected.increment_vote(2);
                        break;
                    case R.id.opt_4:
                        selected.increment_vote(3);
                        break;
                    case R.id.opt_5:
                        selected.increment_vote(4);
                        break;
                }
                selected.addUserID(auth_service.getCurrentUser().getUid());
                if(selected.getVoted_users_list().size() == users.size()){
                    selected.setActive(false);
                }
                pollRef.child(selected.getPollID()).setValue(selected);
            }else{
                //toast sleect option
            }
        }else{
            //toast select a poll
        }
    }

    public void iniRecycler(int user_count){
        this.poll_adapter= new PollsAdapter(polls,user_count);
        this.polls_recycler.setAdapter(poll_adapter);
        this.polls_recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View selected_poll = rv.findChildViewUnder(e.getX(),e.getY());
                if(current_poll_position != -1){
                    previous_selected = current_poll_position;
                }
                int poll_position = rv.getChildAdapterPosition(selected_poll);
                if(poll_position != -1){
                    Poll poll = polls.get(poll_position);
                    if(!poll.hasUserVoted(auth_service.getCurrentUser().getUid()) && poll.isActive()){
                        show_options(poll);
                    }else{
                        show_details(poll);
                    }
                    if(previous_selected != -1){
                        ((PollsAdapter.PollHolder)rv.getChildViewHolder(rv.getChildAt(previous_selected))).
                                checkSelected(false);
                    }
                    ((PollsAdapter.PollHolder)rv.getChildViewHolder(selected_poll)).checkSelected(true);
                    current_poll_position = poll_position;
                    options_layout.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        dialog.dismiss();
    }

    public void iniReferences(){
        this.pollChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                polls.add(dataSnapshot.getValue(Poll.class));
                poll_adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = polls.size();
                Poll poll = dataSnapshot.getValue(Poll.class);
                for(int i = 0; i<size; i++){
                    if(poll.getTitle().equals(polls.get(i).getTitle())){
                        polls.set(i,poll);
                        poll_adapter.notifyDataSetChanged();
                        if((!poll.isActive() || poll.hasUserVoted(auth_service.getCurrentUser().getUid())) && current_poll_position == i){
                            show_details(poll);
                        }
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        this.userChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                users.add(dataSnapshot.getValue(Users.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(auth_service != null){
                    Users user = dataSnapshot.getValue(Users.class);
                    if(user.getUser_id().equals(auth_service.getCurrentUser().getUid())){
                        if(user.isBanned()){
                            auth_service.signOut();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_banned_message), Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    }else{
                        int size = users.size();
                        for(int i = 0; i < size; i++){
                            if(users.get(i).getUser_id().equals(user.getUser_id())){
                                users.set(i,user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        this.userValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                iniRecycler(users.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userChild != null){
            userRef.removeEventListener(userChild);
        }
        if(userValue != null){
            userRef.removeEventListener(userValue);
        }
        if(pollChild != null){
            pollRef.removeEventListener(pollChild);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.pollRef.addChildEventListener(pollChild);
        this.userRef.addChildEventListener(userChild);
        this.userRef.addListenerForSingleValueEvent(userValue);
    }
}

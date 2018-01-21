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
    /*Widget Arrays*/
    private ArrayList<RadioButton> optionsRb;
    private ArrayList<RadioButton> optionsCreate;
    private ArrayList<ImageButton> buttonsCreate;
    private ArrayList<TextView> resultsTv;
    private ArrayList<EditText> titlesCreate;
    private ArrayList<LinearLayout> layoutsCreate;
    private ArrayList<String> optionsList;
    private ArrayList<ProgressBar> progressList;
    /*Widgets*/
    private RecyclerView pollsRecycler;
    private LinearLayout optionsLayout;
    private PollsAdapter pollsAdapter;
    /*Database and auth*/
    private FirebaseAuth authService;
    private DatabaseReference userRef;
    private DatabaseReference pollRef;
    private ChildEventListener pollChild;
    private ChildEventListener userChild;
    private ValueEventListener userValue;
    private RadioGroup optionsGroup;
    private Button submit;
    private EditText titleCreate;
    private ProgressDialog dialog;
    /*Variables*/
    private int currentPollPosition;
    private int previousSelected = -1;
    private String chat_name;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        dialog = ProgressDialog.show(PollActivity.this,"","Loading polls...",true);
        instantiate();
        this.authService = FirebaseAuth.getInstance();
        this.pollsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        this.pollRef = FirebaseDatabase.getInstance().getReference("polls");
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
        this.chat_name = getIntent().getExtras().getString("chat_name");
        this.name = getIntent().getExtras().getString("user_name");
        iniReferences();
        currentPollPosition = -1;
        this.optionsLayout.setVisibility(View.GONE);
    }

    public void iniArrays(){
        this.optionsRb.add((RadioButton) findViewById(R.id.opt_1));
        this.optionsRb.add((RadioButton) findViewById(R.id.opt_2));
        this.optionsRb.add((RadioButton) findViewById(R.id.opt_3));
        this.optionsRb.add((RadioButton) findViewById(R.id.opt_4));
        this.optionsRb.add((RadioButton) findViewById(R.id.opt_5));
        this.resultsTv.add((TextView) findViewById(R.id.result_1));
        this.resultsTv.add((TextView) findViewById(R.id.result_2));
        this.resultsTv.add((TextView) findViewById(R.id.result_3));
        this.resultsTv.add((TextView) findViewById(R.id.result_4));
        this.resultsTv.add((TextView) findViewById(R.id.result_5));
        this.optionsCreate.add((RadioButton) findViewById(R.id.option_1));
        this.optionsCreate.add((RadioButton) findViewById(R.id.option_2));
        this.optionsCreate.add((RadioButton) findViewById(R.id.option_3));
        this.optionsCreate.add((RadioButton) findViewById(R.id.option_4));
        this.optionsCreate.add((RadioButton) findViewById(R.id.option_5));
        this.titlesCreate.add((EditText) findViewById(R.id.text_1));
        this.titlesCreate.add((EditText) findViewById(R.id.text_2));
        this.titlesCreate.add((EditText) findViewById(R.id.text_3));
        this.titlesCreate.add((EditText) findViewById(R.id.text_4));
        this.titlesCreate.add((EditText) findViewById(R.id.text_5));
        this.buttonsCreate.add((ImageButton) findViewById(R.id.button_1));
        this.buttonsCreate.add((ImageButton) findViewById(R.id.button_2));
        this.buttonsCreate.add((ImageButton) findViewById(R.id.button_3));
        this.buttonsCreate.add((ImageButton) findViewById(R.id.button_4));
        this.buttonsCreate.add((ImageButton) findViewById(R.id.button_5));
        this.layoutsCreate.add((LinearLayout) findViewById(R.id.layout_1));
        this.layoutsCreate.add((LinearLayout) findViewById(R.id.layout_2));
        this.layoutsCreate.add((LinearLayout) findViewById(R.id.layout_3));
        this.layoutsCreate.add((LinearLayout) findViewById(R.id.layout_4));
        this.layoutsCreate.add((LinearLayout) findViewById(R.id.layout_5));
        this.progressList.add((ProgressBar) findViewById(R.id.progress_1));
        this.progressList.add((ProgressBar) findViewById(R.id.progress_2));
        this.progressList.add((ProgressBar) findViewById(R.id.progress_3));
        this.progressList.add((ProgressBar) findViewById(R.id.progress_4));
        this.progressList.add((ProgressBar) findViewById(R.id.progress_5));
    }

    public void instantiate(){
        this.optionsLayout = findViewById(R.id.options_layout);
        this.pollsRecycler = findViewById(R.id.poll_recycler);
        this.optionsGroup = findViewById(R.id.radio_group_1);
        this.submit = findViewById(R.id.submit_vote);
        this.titleCreate = findViewById(R.id.title);
        this.optionsRb = new ArrayList<>();
        this.optionsList = new ArrayList<>();
        this.resultsTv = new ArrayList<>();
        this.optionsCreate = new ArrayList<>();
        this.titlesCreate = new ArrayList<>();
        this.buttonsCreate = new ArrayList<>();
        this.layoutsCreate = new ArrayList<>();
        this.progressList = new ArrayList<>();
        iniArrays();
    }

    public void showOptions(Poll poll){
        int size = poll.getOptions().size();
        RadioButton option;
        for(int i = 0; i<size; i++){
            option = optionsRb.get(i);
            option.setText(poll.getOptionTitle(i));
            if(option.getVisibility() == View.GONE){
                option.setVisibility(View.VISIBLE);
            }
        }
        for(int i = size; i < 5; i++){
            option = optionsRb.get(i);
            option.setText("");
            if(option.getVisibility() == View.VISIBLE){
                option.setVisibility(View.GONE);
            }
        }
        for(int i = 0; i < 5; i++){
            resultsTv.get(i).setVisibility(View.GONE);
            progressList.get(i).setVisibility(View.GONE);
        }
        optionsGroup.clearCheck();
        if(submit.getVisibility() == View.GONE){
            submit.setVisibility(View.VISIBLE);
        }
    }

    public void showDetails(Poll poll){
        int total = poll.getVoted_users_list().size();
        int size = poll.getOptions().size();
        for(int i = 0; i < size; i++){
            float percentage = ((float)poll.getOptionVotedCount(i)/total)*100;
            optionsRb.get(i).setVisibility(View.GONE);
            resultsTv.get(i).setVisibility(View.VISIBLE);
            progressList.get(i).setVisibility(View.VISIBLE);
            String option_detail = poll.getOptionTitle(i);
            resultsTv.get(i).setText(option_detail);
            progressList.get(i).setProgress((int) percentage);
        }
        for(int i = size; i < 5; i++){
            optionsRb.get(i).setVisibility(View.GONE);
            resultsTv.get(i).setVisibility(View.GONE);
            progressList.get(i).setVisibility(View.GONE);
        }
        if(submit.getVisibility() == View.VISIBLE){
            submit.setVisibility(View.GONE);
        }
    }

    public void showChoice(int position, int visibility){
        titlesCreate.get(position).setText("");
        optionsCreate.get(position).setChecked(false);
        titlesCreate.get(position).setVisibility(visibility);
        optionsCreate.get(position).setVisibility(visibility);
    }

    public void outputOption(int array_position, int visibility){
        switch(visibility){
            case View.VISIBLE:
                showChoice(array_position,View.VISIBLE);
                buttonsCreate.get(array_position).setImageDrawable(getDrawable(R.drawable.ic_cancel_black_24dp));
                if(array_position < 4){
                    layoutsCreate.get(array_position+1).setVisibility(View.VISIBLE);
                }
                break;
            case View.GONE:
                if(array_position == 4){
                    showChoice(array_position,View.GONE);
                }else{
                    int first_invisible = getFirstInvisibleItem();
                    int last_visible = first_invisible-1;
                    if(first_invisible == 3){
                        Toast.makeText(this, R.string.poll_minimum, Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = array_position; i < last_visible; i++){
                            titlesCreate.get(i).setText(titlesCreate.get(i+1).getText());
                        }
                        changeLastOption(last_visible);
                    }
                }
                break;
        }
    }

    public void changeLastOption(int last_visible){
        if(titlesCreate.get(last_visible).getVisibility() == View.GONE){
            layoutsCreate.get(last_visible).setVisibility(View.GONE);
            showChoice(last_visible-1,View.GONE);
            buttonsCreate.get(last_visible-1).setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_24dp));
        }else{
            showChoice(last_visible,View.GONE);
            buttonsCreate.get(last_visible).setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_24dp));
        }
    }

    public int getFirstInvisibleItem(){
        for(int i = 0; i <= 4; i++){
            if(layoutsCreate.get(i).getVisibility() == View.GONE){
                return i;
            }
        }
        return 5;
    }

    public void changeCheck(View view){
        switch(view.getId()){
            case R.id.option_1:
                unCheckOthers(0);
                break;
            case R.id.option_2:
                unCheckOthers(1);
                break;
            case R.id.option_3:
                unCheckOthers(2);
                break;
            case R.id.option_4:
                unCheckOthers(3);
                break;
            case R.id.option_5:
                unCheckOthers(4);
                break;
        }
    }

    public void unCheckOthers(int position_to_not_change){
        for(int i = 0; i < 5; i++){
            if(i != position_to_not_change){
                optionsCreate.get(i).setChecked(false);
            }else{
                optionsCreate.get(i).setChecked(true);
            }
        }
    }

    public void anotherOption(View view){
        switch(view.getId()){
            case R.id.button_1:
                outputOption(0,View.GONE);
                break;
            case R.id.button_2:
                outputOption(1,View.GONE);
                break;
            case R.id.button_3:
                if(titlesCreate.get(2).getVisibility() == View.VISIBLE){
                    outputOption(2,View.GONE);
                }else{
                    outputOption(2,View.VISIBLE);
                }
                break;
            case R.id.button_4:
                if(titlesCreate.get(3).getVisibility() == View.VISIBLE){
                    outputOption(3,View.GONE);
                }else{
                    outputOption(3,View.VISIBLE);
                }
                break;
            case R.id.button_5:
                if(titlesCreate.get(4).getVisibility() == View.VISIBLE){
                    outputOption(4,View.GONE);
                }else{
                    outputOption(4,View.VISIBLE);
                }
                break;
        }
    }

    public boolean isAnyOptionEmpty(){
        for(int i = 0; i < 5; i++){
            String text = titlesCreate.get(i).getText().toString();
            if(titlesCreate.get(i).getVisibility() == View.VISIBLE && (text.isEmpty() || text.trim().isEmpty())){
                return true;
            }
        }
        return false;
    }

    public void setOptions(){
        for(int i = 0; i < 5; i++){
            if(titlesCreate.get(i).getVisibility() == View.VISIBLE){
                optionsList.add(titlesCreate.get(i).getText().toString());
            }
        }
    }

    public boolean isAnOptionChecked(){
        for(int i = 0; i < 5; i++){
            if(optionsCreate.get(i).getVisibility() == View.VISIBLE && optionsCreate.get(i).isChecked()){
                return true;
            }
        }
        return false;
    }

    public boolean isTitleSet(String title){
        if(title.isEmpty() || title.trim().isEmpty()){
            titleCreate.setError(getString(R.string.poll_title_empty));
            return false;
        }else{
            titleCreate.setError(null);
            return true;
        }
    }

    public void createPoll(View view){
        if(isAnyOptionEmpty()){
            Toast.makeText(this,R.string.poll_empty,Toast.LENGTH_SHORT).show();
        }else if(!isAnOptionChecked()){
            Toast.makeText(this, R.string.poll_no_option, Toast.LENGTH_SHORT).show();
        }else{
            String poll_title = titleCreate.getText().toString();
            setOptions();
            int size = optionsList.size();
            if(size < 2){
                Toast.makeText(this,R.string.poll_minimum,Toast.LENGTH_SHORT).show();
            } else if (size > 5) {
                Toast.makeText(this,R.string.poll_max,Toast.LENGTH_SHORT).show();
            }else if(!isTitleSet(poll_title)){
                //Uh.. nothing, everything is handled IN it. But needs to be called anyway!
            }else{
                String pollID = pollRef.push().getKey();
                ArrayList<Integer> votes = Poll.create_options_count(size,getSelectedOption());
                ArrayList<String> users = new ArrayList<>();
                users.add(authService.getCurrentUser().getUid());
                Poll poll = new Poll(pollID,poll_title,true, optionsList,votes,users);
                pollRef.child(pollID).setValue(poll);
                String message = String.format(getString(R.string.poll_created_announce),name,poll_title);
                Messages.sendMessage(name,message,Messages.getDateFormatted(new Date()),chat_name,1);
                titleCreate.setText("");
                clearEverything();
                Toast.makeText(this,R.string.poll_created,Toast.LENGTH_SHORT).show();
            }
            optionsList.clear();
        }
    }

    public int getSelectedOption(){
        for(int i = 0; i < 5; i++){
            if(optionsCreate.get(i).isChecked()){
                return i;
            }
        }
        return 0;
    }

    public void clearEverything(){
        titlesCreate.get(0).setText("");
        titlesCreate.get(1).setText("");
        outputOption(4,View.GONE);
        outputOption(3,View.GONE);
        outputOption(2,View.GONE);
    }

    public void submitVote(View view){
        int option = optionsGroup.getCheckedRadioButtonId();
        if(currentPollPosition != -1){
            if(option != -1){
                Poll selected = polls.get(currentPollPosition);
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
                selected.addUserID(authService.getCurrentUser().getUid());
                if(selected.getVoted_users_list().size() == users.size()){
                    selected.setActive(false);
                }
                pollRef.child(selected.getPollID()).setValue(selected);
            }else{
                Toast.makeText(this, R.string.poll_no_option, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.poll_select, Toast.LENGTH_SHORT).show();
        }
    }

    public void iniRecycler(int user_count){
        this.pollsAdapter = new PollsAdapter(polls,user_count);
        this.pollsRecycler.setAdapter(pollsAdapter);
        this.pollsRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View selected_poll = rv.findChildViewUnder(e.getX(),e.getY());
                if(selected_poll != null && e.getAction() == MotionEvent.ACTION_DOWN){
                    if(currentPollPosition != -1){
                        previousSelected = currentPollPosition;
                    }
                    int poll_position = rv.getChildAdapterPosition(selected_poll);
                    if(poll_position != -1){
                        Poll poll = polls.get(poll_position);
                        if(!poll.hasUserVoted(authService.getCurrentUser().getUid()) && poll.isActive()){
                            showOptions(poll);
                        }else{
                            showDetails(poll);
                        }
                        if(previousSelected != -1){
                            PollsAdapter.PollHolder holder = ((PollsAdapter.PollHolder)rv.findViewHolderForAdapterPosition(previousSelected));
                            if(holder != null){
                                holder.checkSelected(false);
                            }
                        }
                        ((PollsAdapter.PollHolder)rv.findViewHolderForAdapterPosition(poll_position)).checkSelected(true);
                        currentPollPosition = poll_position;
                        optionsLayout.setVisibility(View.VISIBLE);
                    }
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
                pollsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = polls.size();
                Poll poll = dataSnapshot.getValue(Poll.class);
                for(int i = 0; i<size; i++){
                    if(poll.getTitle().equals(polls.get(i).getTitle())){
                        polls.set(i,poll);
                        pollsAdapter.notifyDataSetChanged();
                        if((!poll.isActive() || poll.hasUserVoted(authService.getCurrentUser().getUid())) && currentPollPosition == i){
                            showDetails(poll);
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
                if(authService != null){
                    Users user = dataSnapshot.getValue(Users.class);
                    if(user.getUser_id().equals(authService.getCurrentUser().getUid())){
                        if(user.isBanned()){
                            authService.signOut();
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

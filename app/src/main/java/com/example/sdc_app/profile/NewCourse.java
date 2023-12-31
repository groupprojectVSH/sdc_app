package com.example.sdc_app.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdc_app.R;
import com.example.sdc_app.community.CommunityItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewCourse extends Fragment {
    EditText courseName,courseDescription;
    EditText groupLink;
    Button submitCourse;
    int numTopics=0;
    ImageView addNewTopicIcon;
    private SharedViewModel sharedViewModel;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference communityReference;
    DatabaseReference courseReference;
    DatabaseReference adminReference;
    private ArrayAdapter<String> topicAdapter;
    private ListView topicsListView;
    List<AddTopic> topicList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel=new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        topicList=new ArrayList<>();
        topicAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
    }

    public NewCourse(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Setting View IDs
        View view = LayoutInflater.from(getContext()).inflate(R.layout.new_course, container, false);

        //Setting all Fields
        setAllFields(view);

        //Setting listener for new topic add
        addNewTopicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction=requireActivity().getSupportFragmentManager().beginTransaction();
                NewTopic topic=new NewTopic();
                transaction.replace(R.id.add_course_frame,topic);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //Getting Topics using shared View Model
        LiveData<AddTopic> liveData=sharedViewModel.getSharedTopicData();
        liveData.observe(getViewLifecycleOwner(), new Observer<AddTopic>() {
            @Override
            public void onChanged(AddTopic addTopic) {
                if(addTopic!=null){
                    numTopics++;
                    topicList.add(addTopic);
                    Toast.makeText(getContext(),"Topic added",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Adding listener for submit course
        submitCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courseName.getText().toString().equals("")
                        ||courseDescription.getText().toString().equals("")
                        ||groupLink.getText().toString().equals("")
                        ||numTopics==0){
                    Toast.makeText(getContext(),"Enter Complete Course Details",Toast.LENGTH_LONG).show();

                }
                else {
                    String key=courseReference.push().getKey();
                    AddCourse course=new AddCourse(key,courseName.getText().toString(),
                            courseDescription.getText().toString(),user.getDisplayName(),topicList);
                    courseReference.child(key).setValue(course);
                    adminReference.child(user.getUid()).child(key).setValue("added");
                    CommunityItem communityItem=new CommunityItem(courseName.getText().toString(),key,groupLink.getText().toString()
                            , user.getDisplayName());
                    communityReference.child(key).setValue(communityItem);
                    getActivity().finish();

                }


            }
        });

        return view;
    }
    public void setAllFields(View view){
        communityReference=FirebaseDatabase.getInstance().getReference("community");
        courseReference= FirebaseDatabase.getInstance().getReference("course");
        adminReference= FirebaseDatabase.getInstance().getReference("admin");
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        courseName=view.findViewById(R.id.explore_course_name);
        courseDescription=view.findViewById(R.id.course_description);
        submitCourse=view.findViewById(R.id.enroll_course_btn);
        topicsListView=view.findViewById(R.id.profile_list_of_topics);
        topicsListView.setAdapter(topicAdapter);
        addNewTopicIcon=view.findViewById(R.id.add_new_topic_icon);
        groupLink=view.findViewById(R.id.group_link);

    }


    @Override
    public void onResume() {
        topicAdapter.clear();
        for (int i = 0; i < topicList.size(); i++) {
            topicAdapter.add(topicList.get(i).getName());
        }
        topicAdapter.notifyDataSetChanged();
        super.onResume();
    }
}

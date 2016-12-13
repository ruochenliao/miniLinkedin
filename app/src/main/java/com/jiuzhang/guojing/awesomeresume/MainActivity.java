package com.jiuzhang.guojing.awesomeresume;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.guojing.awesomeresume.model.BasicInfo;
import com.jiuzhang.guojing.awesomeresume.model.Education;
import com.jiuzhang.guojing.awesomeresume.model.Experience;
import com.jiuzhang.guojing.awesomeresume.model.Project;
import com.jiuzhang.guojing.awesomeresume.util.DateUtils;
import com.jiuzhang.guojing.awesomeresume.util.ImageUtils;
import com.jiuzhang.guojing.awesomeresume.util.ModelUtils;

import java.util.ArrayList;
import java.util.List;

//import com.jiuzhang.guojing.awesomeresume.util.ImageUtils;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {
    public static final int REQ_CODE_EDIT_BASIC_INFO = 103;
    public static final int  REQ_CODE_EDIT_EDUCATION = 100;
    public static final int REQ_CODE_EDIT_PROJECT = 102;
    public static final int REQ_CODE_EDIT_EXPERIENCE = 101;

    private static final String MODEL_BASIC_INFO = "basic_info";
    private static final String MODEL_EDUCATIONS = "educations";
    private static final String MODEL_PROJECT = "projects";
    private static final String MODEL_EXPERIENCE = "experiences";


    private BasicInfo basicInfo;
    private List<Education> educations;
    private List<Project> projects;
    private List<Experience> experiences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
        setupUI();
        Toast.makeText(MainActivity.this, "data loaded, UI setup", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
        if( resultCode == Activity.RESULT_OK ){
            switch( requestCode ){

                case REQ_CODE_EDIT_BASIC_INFO:
                    BasicInfo basicInfo = data.getParcelableExtra(BasicInfoEditActivity.KEY_BASIC_INFO);
                    updateBasicInfo(basicInfo);
                    break;

                case REQ_CODE_EDIT_EDUCATION:
                    //@KEY_EDUCATION_ID is a signal, when it it exist, delete the data
                    //when it is not exist, keep the data
                    String educationId = data.getStringExtra( EducationEditActivity.KEY_EDUCATION_ID );
                    if( educationId != null ){
                        deleteEducation(educationId);
                    }
                    else{
                        Education education = data.getParcelableExtra(EducationEditActivity.KEY_EDUCATION);
                        updateEducation( education );
                    }
                    break;
                case REQ_CODE_EDIT_PROJECT:
                    String projectId = data.getStringExtra( ProjectEditActivity.KEY_PROJECT_ID );
                    if( projectId == null ){
                        Project project = data.getParcelableExtra(ProjectEditActivity.KEY_PROJECT);
                        updateProject(project);
                    }
                    else {
                        deleteProject(projectId);
                    }
                    break;
                case REQ_CODE_EDIT_EXPERIENCE:
                    //Toast.makeText(MainActivity.this, "world",Toast.LENGTH_SHORT);
                    String experienceId= data.getStringExtra(ExperienceEditActivity.KEY_EXPERIENCE_ID);
                    if( experienceId == null ){
                        Experience experience = data.getParcelableExtra(ExperienceEditActivity.KEY_EXPERIENCE);
                        //Toast.makeText(MainActivity.this, DateUtils.dateToString(experience.startDate) ,Toast.LENGTH_SHORT);
                        updateExperience(experience);
                    }
                    else{
                        deleteExperience(experienceId);
                    }
                    break;
            }
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);
        ImageButton addEducationBtn = (ImageButton) findViewById(R.id.add_education_btn);
        ImageButton addProjectBtn = (ImageButton) findViewById(R.id.add_Project_btn);
        ImageButton addExperienceBtn = (ImageButton) findViewById(R.id.add_Experience_btn);
        //ImageButton basicInfoBtn = (ImageButton) findViewById(R.id.edit_basic_info_btn);
 /*
        basicInfoBtn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, BasicInfoEditActivity.class );
                startActivityForResult(intent, );
            }
        });
*/
        addEducationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, EducationEditActivity.class );
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });
        addProjectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, ProjectEditActivity.class );
                startActivityForResult(intent, REQ_CODE_EDIT_PROJECT);
            }
        });
        addExperienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, ExperienceEditActivity.class );
                startActivityForResult( intent, REQ_CODE_EDIT_EXPERIENCE);
            }
        });
        setupBasicInfoUI();
        setupEducationUI();
        setupProjectUI();
        setupExperienceUI();
    }

    private void setupBasicInfoUI() {
        //((TextView) findViewById(R.id.main_name)).setText( TextUtils.isEmpty(basicInfo.name)? "Null":"Ruochen Liao" );
        ((TextView) findViewById(R.id.main_name)).setText(TextUtils.isEmpty(basicInfo.name)? "Your name": basicInfo.name );
        ((TextView) findViewById(R.id.main_email)).setText(TextUtils.isEmpty(basicInfo.email)?"Your email": basicInfo.email);

        //get the layout of user picture
        ImageView userPicture = (ImageView) findViewById(R.id.main_activity_basic_info_user_picture);
        if( basicInfo.imageUrl != null ){
            //Toast.makeText(this, basicInfo.imageUrl.toString(), Toast.LENGTH_SHORT).show();
            ImageUtils.loadImage( this, basicInfo.imageUrl, userPicture );
        }
        else{
            userPicture.setImageResource(R.drawable.somebody);
        }
        findViewById(R.id.edit_basic_info_btn).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, BasicInfoEditActivity.class );
                intent.putExtra( BasicInfoEditActivity.KEY_BASIC_INFO, basicInfo );
                startActivityForResult(intent, REQ_CODE_EDIT_BASIC_INFO);
            }
        });

    }
    //setup UI for educations
    //@educationView: a single education view
    //@educationLayout: the big education layout
    private void setupEducationUI() {
        LinearLayout educationsLayout = (LinearLayout) findViewById( R.id.education_list );
        educationsLayout.removeAllViews();
        for(Education education: educations) {
            View educationView = getLayoutInflater().inflate(R.layout.education_item, null);
            setupEducation(educationView, education);
            //after the educationView is set upt, add the view the education layout.
            educationsLayout.addView( educationView );
        }
    }
    //setup UI for projects
    private void setupProjectUI(){
        LinearLayout projectLayout = (LinearLayout) findViewById(R.id.project_list);
        projectLayout.removeAllViews();
        for( Project project: projects ){
            View projectView = getLayoutInflater().inflate(R.layout.project_item, null);
            setupProject(projectView, project);
            projectLayout.addView(projectView);
        }
    }
    private void setupExperienceUI(){
        LinearLayout experienceItemLayout = (LinearLayout) findViewById(R.id.experience_item_list);
        experienceItemLayout.removeAllViews();
        for(int i = 0; i < experiences.size(); i++){
            Experience experience = experiences.get(i);
            View experienceView = getLayoutInflater().inflate(R.layout.experience_item, null);
            setupExperience(experienceView, experience);
            experienceItemLayout.addView(experienceView);
        }
    }
    //setup UI for experience
    private void setupExperience(View experienceView, final Experience experience ){
        String experience_name = experience.name + " (" + DateUtils.dateToString(experience.startDate)
                + "~" + DateUtils.dateToString(experience.endDate) +") ";
        String description = formatItems(experience.description );

        ((TextView) experienceView.findViewById(R.id.experience_item_name)).setText(experience_name);
        ((TextView) experienceView.findViewById(R.id.experience_item_details)).setText(description);

        //ImageButton project_edit_btn = (ImageButton) projectView.findViewById(R.id.project_item_edit_btn);
        ImageButton experience_edit_btn = (ImageButton) experienceView.findViewById(R.id.experience_item_edit_btn);
        experience_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "click",Toast.LENGTH_SHORT);

                Intent intent = new Intent(MainActivity.this, ExperienceEditActivity.class);
                intent.putExtra( ExperienceEditActivity.KEY_EXPERIENCE, experience );
                startActivityForResult(intent , REQ_CODE_EDIT_EXPERIENCE);

            }
        });
    }

    //setup UI for project
    private void setupProject(View projectView, final Project project){
        String project_name  = project.name + " ( " + DateUtils.dateToString(project.startDate) +" ~ "
                + DateUtils.dateToString(project.endDate) +" )";
        String project_description = formatItems( project.details );

        ((TextView) projectView.findViewById(R.id.project_item_name)).setText(project_name);
        ((TextView) projectView.findViewById(R.id.project_id_description)).setText(project_description);

        ImageButton project_edit_btn = (ImageButton) projectView.findViewById(R.id.project_item_edit_btn);
        project_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProjectEditActivity.class);
                intent.putExtra(ProjectEditActivity.KEY_PROJECT, project);
                startActivityForResult(intent, REQ_CODE_EDIT_PROJECT);
            }
        });
    }

    //setup UI for education
    private void setupEducation( View educationView, final Education education ){
        //get the input string
        String school_name = education.school + "(" + DateUtils.dateToString(education.startDate) +"~"+
                DateUtils.dateToString(education.endDate) +")";
        String courses = formatItems( education.courses );
        //set the layout's content
        ((TextView) educationView.findViewById(R.id.education_item_school_name)).setText( school_name );
        ((TextView) educationView.findViewById(R.id.education_item_description)).setText( courses );

        //add listener to education edit button
        ImageButton education_edit_btn = (ImageButton) educationView.findViewById(R.id.education_item_edit_btn);
        education_edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, EducationEditActivity.class );
                intent.putExtra( EducationEditActivity.KEY_EDUCATION, education);
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });
    }

    //@educations is a static variable;
    //init the educations variable from the shared preference storage
    private void loadData() {
        List<Education> savedEducation = ModelUtils.read(this, MODEL_EDUCATIONS, new TypeToken<List<Education>>(){});
        List<Project> savedProjects = ModelUtils.read(this, MODEL_PROJECT, new TypeToken<List<Project>>(){});
        List<Experience> savedExperience = ModelUtils.read(this, MODEL_EXPERIENCE, new TypeToken<List<Experience>>(){});
        BasicInfo savedBasicInfo = ModelUtils.read( this, MODEL_BASIC_INFO, new TypeToken<BasicInfo>(){} );

        basicInfo = savedBasicInfo == null? new BasicInfo():savedBasicInfo;
        educations = savedEducation == null? new ArrayList<Education>(): savedEducation;
        projects = savedProjects == null? new ArrayList<Project>(): savedProjects;
        experiences = savedExperience == null? new ArrayList<Experience>(): savedExperience;
    }

    private void updateExperience( Experience experience ){
        boolean found = false;
        for( int i = 0; i < experiences.size(); i++ ){
            Experience e = experiences.get(i);
            if( TextUtils.equals(e.id,  experience.id) ){
                found = true;
                experiences.set(i,experience);
                break;
            }
        }
        if( found == false ){
            Toast.makeText(this, "found is not found", Toast.LENGTH_SHORT).show();
            experiences.add(experience);
        }
        ModelUtils.save( this, MODEL_EXPERIENCE, experiences );
        setupExperienceUI();
    }

    private void updateBasicInfo(BasicInfo basicInfo){
        ModelUtils.save(this, MODEL_BASIC_INFO, basicInfo);
        this.basicInfo = basicInfo;
        setupBasicInfoUI();
    }

    private void updateEducation(Education education){
        boolean found = false;
        for( int i =0; i < educations.size(); i++ ){
            Education e = educations.get(i);
            if( TextUtils.equals(e.id, education.id) ){
                found = true;
                educations.set(i, education);
                break;
            }
        }
        if( !found ){
            educations.add(education);
        }
        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        //setupEducations();
        setupEducationUI();
    }

    private void updateProject(Project project){
        boolean found = false;
        for( int i = 0; i < projects.size(); i++ ){
            Project p = projects.get(i);
            if( TextUtils.equals( project.id, p.id ) ){
                found = true;
                projects.set(i, project);
            }
        }
        if( !found ){
            projects.add(project);
        }
        ModelUtils.save(this, MODEL_PROJECT, projects);
        setupProjectUI();
    }
    private void deleteExperience(String experienceId){
        for( int i = 0; i < experiences.size(); i++ ){
            Experience experience = experiences.get(i);
            if( TextUtils.equals( experience.id, experienceId ) ){
                experiences.remove(i);
                break;
            }
        }
        ModelUtils.save( this, MODEL_EXPERIENCE, experiences );
        setupExperienceUI();
    }
    private void deleteEducation(@NonNull String educationId){
        for( int i= 0; i < educations.size(); i++ ){
            Education e = educations.get(i);
            if( TextUtils.equals(e.id, educationId) ){
                educations.remove(i);
                break;
            }
        }
        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        //setupEducations();
        setupEducationUI();
    }

    private void deleteProject(@NonNull String projectId ){
        for( int i = 0; i < projects.size(); i++ ){
            Project p = projects.get(i);
            if( TextUtils.equals( projectId, p.id ) ){
                projects.remove(i);
                break;
            }
        }
        ModelUtils.save( this, MODEL_PROJECT, projects );
        setupProjectUI();
    }

    public static String formatItems(List<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item: items) {
            sb.append(' ').append('-').append(' ').append(item).append('\n');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}

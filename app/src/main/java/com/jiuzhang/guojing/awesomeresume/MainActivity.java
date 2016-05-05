package com.jiuzhang.guojing.awesomeresume;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jiuzhang.guojing.awesomeresume.model.BasicInfo;
import com.jiuzhang.guojing.awesomeresume.model.Education;
import com.jiuzhang.guojing.awesomeresume.model.Experience;
import com.jiuzhang.guojing.awesomeresume.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    private BasicInfo basicInfo;
    private Education education;
    private Experience experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fakeData();
        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        ImageButton addEducationBtn = (ImageButton) findViewById(R.id.add_education_btn);
        addEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationEditActivity.class);
                startActivity(intent);
            }
        });

        ImageButton addExperienceBtn = (ImageButton) findViewById(R.id.add_experience_btn);
        addExperienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExperienceEditActivity.class);
                startActivity(intent);
            }
        });

        setupBasicInfo(basicInfo);
        setupEducation(education);
        setupExperience(experience);
    }

    private void setupBasicInfo(BasicInfo basicInfo) {
        ((TextView) findViewById(R.id.name)).setText(basicInfo.name);
        ((TextView) findViewById(R.id.email)).setText(basicInfo.email);
    }

    private void setupEducation(Education education) {
        String dateString = DateUtils.dateToString(education.startDate)
                + " ~ " + DateUtils.dateToString(education.endDate);
        ((TextView) findViewById(R.id.education_school))
                .setText(education.school + " (" + dateString + ")");
        ((TextView) findViewById(R.id.education_courses))
                .setText(formatItems(education.courses));
    }

    private void setupExperience(Experience experience) {
        String dateString = DateUtils.dateToString(experience.startDate)
                + " ~ " + DateUtils.dateToString(experience.endDate);
        ((TextView) findViewById(R.id.experience_company))
                .setText(experience.company + " (" + dateString + ")");
        ((TextView) findViewById(R.id.experience_details))
                .setText(formatItems(experience.details));
    }

    private void fakeData() {
        basicInfo = new BasicInfo();
        basicInfo.name = "Jing Guo";
        basicInfo.email = "guojing@jiuzhang.com";

        education = new Education();
        education.school = "CMU";
        education.major = "Computer Science";
        education.startDate = DateUtils.stringToDate("09/2013");
        education.endDate = DateUtils.stringToDate("09/2015");

        List<String> courses = new ArrayList<>();
        courses.add("Database");
        courses.add("Algorithm");
        courses.add("OO Design");
        courses.add("Operating System");
        education.courses = courses;

        experience = new Experience();
        experience.company = "LinkedIn";
        experience.title = "Software Engineer";
        experience.startDate = DateUtils.stringToDate("09/2015");
        experience.endDate = DateUtils.stringToDate("04/2016");

        List<String> details = new ArrayList<>();
        details.add("Built something using some tech");
        details.add("Built something using some tech");
        details.add("Built something using some tech");
        experience.details = details;
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
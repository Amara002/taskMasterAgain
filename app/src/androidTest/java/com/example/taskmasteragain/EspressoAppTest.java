package com.example.taskmasteragain;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.contrib.RecyclerViewActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
@RunWith(AndroidJUnit4.class)
public class EspressoAppTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

//    @Rule
//    public ActivityScenarioRule<MainActivity> activityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void testSetting(){
        onView(withId(R.id.button8)).perform(click());
        onView(withId(R.id.textView9)).check(matches(withText("Setting")));
    }
    @Test
    public void assertTextChanged() {
        // type text and then press change text button
        onView(withId(R.id.button8)).perform(click());

        onView(withId(R.id.editTextTextPersonName3)).perform(typeText("Amara"), closeSoftKeyboard());

        onView(withId(R.id.button7)).perform(click());
        // check that the text was changed when the button was clicked
        onView(withId(R.id.textView)).check(matches(withText("Amara's Task")));
    }

    @Test
    public void testOpenTaskDetail(){
//        onView(ViewMatchers.withId(R.id.listTask)).check(matches(isDisplayed()));
        onView(withId(R.id.listTask)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        onView(withId(R.id.taskTitle)).check(matches(withText("Task")));
        onView(withId(R.id.taskBody)).check(matches(withText("Task")));
        onView(withId(R.id.taskState)).check(matches(withText("Task")));
    }
    @Test
    public void testAddTask(){
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.editItemTitle)).perform(typeText("DO TODAY LAB"), closeSoftKeyboard());
        onView(withId(R.id.editTextBody)).perform(typeText("Lab 30"), closeSoftKeyboard());
        onView(withId(R.id.editTextState)).perform(typeText("New"), closeSoftKeyboard());
        onView(withId(R.id.addbutton)).perform(click());
        onView(withId(R.id.listTask)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withId(R.id.taskTitle)).check(matches(withText("DO TODAY LAB")));
        onView(withId(R.id.taskBody)).check(matches(withText("Lab 30")));
        onView(withId(R.id.taskState)).check(matches(withText("New")));
    }


}

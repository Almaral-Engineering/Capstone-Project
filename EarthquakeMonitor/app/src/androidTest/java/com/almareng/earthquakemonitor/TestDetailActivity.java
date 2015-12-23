package com.almareng.earthquakemonitor;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.almareng.earthquakemonitor.details.DetailActivity;
import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.list.EarthquakeListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class TestDetailActivity extends ActivityInstrumentationTestCase2<DetailActivity> {
    public TestDetailActivity() {
        super(DetailActivity.class);
    }

    public void testThatMagnitudeIsDisplayed() {
        onView(withText("5.03")).check(matches(isDisplayed()));
    }

    public void testThatDateIsDisplayed() {
        onView(withId(R.id.fragment_detail_date_text)).check(matches(isDisplayed()));
    }

    public void testThatTimeIsDisplayed() {
        onView(withId(R.id.fragment_detail_time_text)).check(matches(isDisplayed()));
    }

    public void testThatLocationIsDisplayed(){
        onView(withText("-147.1964")).check(matches(isDisplayed()));
        onView(withText("64.4246")).check(matches(isDisplayed()));
        onView(withText("10")).check(matches(isDisplayed()));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final Earthquake earthquake = new Earthquake(5.03,
                                                     "250 Km NW of Honolulu",
                                                     "1450833295000",
                                                     "-147.1964",
                                                     "64.4246" ,
                                                     "10",
                                                     "250");
        final Intent intent = new Intent();

        intent.putExtra(EarthquakeListActivity.EARTHQUAKE_KEY, earthquake);
        setActivityIntent(intent);

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
    }
}

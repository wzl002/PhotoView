package ca.bcit.comp7082.zilong.photogallery;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.bcit.comp7082.zilong.photogallery.models.Picture;
import ca.bcit.comp7082.zilong.photogallery.services.DatabaseService;
import ca.bcit.comp7082.zilong.photogallery.services.PhotoService;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test for Main Acidity and Search Result
 */
@RunWith(AndroidJUnit4.class)
public class TestMainActivity {

    private PhotoService photoService;
    private List<Picture> pictureList;

    public static String NO_RESULT_FOUND = "No Result";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    /**
     * get the fragment manager
     */
    @Before
    public void init() {
        mActivityRule.getActivity()
                .getSupportFragmentManager().beginTransaction();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        DatabaseService.initDatabaseService(appContext);
        this.photoService = new PhotoService(appContext);

        this.pictureList = photoService.getPictures(0);

        // photoService.resetDatabase(); // reset database for test.

        if (pictureList.size() < 2) {
            throw new RuntimeException("Must take two photos first to set up for app test.");
        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ca.bcit.comp7082.zilong.photogallery", appContext.getPackageName());
    }

    /**
     * test the start of the app
     */
    @Test
    public void testPictureLoad() {
        Log.d("TestMainActivity", "testPictureLoad");
        String firstPhotoName = this.pictureList.get(0).getTitle();
        onView(withId(R.id.photo_name_text)).check(matches((isDisplayed())));
        onView(withId(R.id.photo_name_text)).check(matches(withText(firstPhotoName)));
    }

    @Test
    public void testEditCaption() throws InterruptedException {
        Log.d("TestMainActivity", "testEditCaption");
        String editName1 = "Test Photo 1";
        String editName2 = "Test Photo One";
        // onView(withId(R.id.etInput)).perform(typeText("Hello")).check(matches(withText("Hello")));
        // click edit
        onView(withId(R.id.edit_button)).check(matches((isDisplayed()))).perform(click());
        // save button and editview show
        // // input name 1
        onView(withId(R.id.photo_name_text_input)).check(matches((isDisplayed()))).perform(replaceText(editName1), closeSoftKeyboard());
        onView(withId(R.id.photo_name_text_input)).check(matches(withText(editName1)));
        onView(withId(R.id.save_button)).check(matches((isDisplayed()))).perform(click());

        // saved, show edit button and text;
        onView(withId(R.id.photo_name_text)).check(matches(withText(editName1)));
        // edit again
        onView(withId(R.id.edit_button)).check(matches((isDisplayed()))).perform(click());
        // input name 2
        onView(withId(R.id.photo_name_text_input)).check(matches((isDisplayed()))).perform(replaceText(editName2), closeSoftKeyboard());
        onView(withId(R.id.photo_name_text_input)).check(matches(withText(editName2)));
        onView(withId(R.id.save_button)).check(matches((isDisplayed()))).perform(click());

        // saved, show edit button and text;
        onView(withId(R.id.photo_name_text)).check(matches(withText(editName2)));
        // edit again
        onView(withId(R.id.edit_button)).check(matches((isDisplayed())));
    }

    // Test Left & Right Button
    @Test
    public void testLeftRight() {
        Log.d("TestMainActivity", "testLeft");

        String picName1 = this.pictureList.get(0).getTitle();
        String picName2 = this.pictureList.get(1).getTitle();
        // onView(withId(R.id.etInput)).perform(typeText("Hello")).check(matches(withText("Hello")));
        // click edit
        onView(withId(R.id.photo_name_text)).check(matches(withText(picName1)));

        onView(withId(R.id.right_button)).perform(click());
        onView(withId(R.id.photo_name_text)).check(matches(withText(picName2)));

        onView(withId(R.id.left_button)).perform(click());
        onView(withId(R.id.photo_name_text)).check(matches(withText(picName1)));


    }

    // Test Research
    @Test
    public void testSearchByCaption() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        String picName1 = this.pictureList.get(0).getTitle();
        String picName2 = this.pictureList.get(1).getTitle();

        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.MONTH, -2);
        toCalendar.add(Calendar.DATE, 1);
        String fromDate = dateFormatter.format(fromCalendar.getTime());
        String toDate = dateFormatter.format(toCalendar.getTime());

        onView(withId(R.id.menu_search)).perform(click());
        onView(withId(R.id.keyword_input)).check(matches((isDisplayed())));
        onView(withId(R.id.from_date_input)).check(matches((isDisplayed())));
        onView(withId(R.id.to_date_input)).check(matches((isDisplayed())));

        // search name 1
        onView(withId(R.id.keyword_input)).perform(replaceText(picName1), closeSoftKeyboard());
        onView(withId(R.id.from_date_input)).perform(replaceText(fromDate));
        onView(withId(R.id.to_date_input)).perform(replaceText(toDate));

        // click search and return
        onView(withId(R.id.search_button)).perform(click());

        onView(withId(R.id.photo_name_text)).check(matches((isDisplayed()))).check(matches(withText(picName1)));

        // search name 2
        onView(withId(R.id.menu_search)).perform(click());
        onView(withId(R.id.keyword_input)).check(matches((isDisplayed()))).perform(replaceText(picName2), closeSoftKeyboard());
        onView(withId(R.id.from_date_input)).perform(replaceText(fromDate));
        onView(withId(R.id.to_date_input)).perform(replaceText(toDate));
        // click search and return
        onView(withId(R.id.search_button)).perform(click());

        onView(withId(R.id.photo_name_text)).check(matches((isDisplayed()))).check(matches(withText(picName2)));
    }

    // Test Location Research
    // In Espresso, it not possible to operate the google Map object like move camera. ( uiautomator can get the marker object)
    // Instead, this test change the location data of pictures, one in search area and one out of it.
    @Test
    public void testSearchByLocation() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        LatLng loc = MapsActivity.getCurrentLocation( InstrumentationRegistry.getTargetContext());

        Picture picture1 = this.pictureList.get(0);
        Picture picture2 = this.pictureList.get(1);

        picture1.setLatitude(loc.latitude);
        picture1.setLongitude(loc.longitude);
        picture2.setLatitude(loc.latitude + 10);
        picture2.setLongitude(loc.longitude + 10);

        this.photoService.updatePicture(picture1);
        this.photoService.updatePicture(picture2);

        String picName1 = picture1.getTitle();
        String picName2 = picture2.getTitle();

        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.MONTH, -2);
        toCalendar.add(Calendar.DATE, 1);
        String fromDate = dateFormatter.format(fromCalendar.getTime());
        String toDate = dateFormatter.format(toCalendar.getTime());

        onView(withId(R.id.menu_search)).perform(click());
        onView(withId(R.id.keyword_input)).check(matches((isDisplayed())));
        onView(withId(R.id.from_date_input)).check(matches((isDisplayed())));
        onView(withId(R.id.to_date_input)).check(matches((isDisplayed())));

        // search name 1
        onView(withId(R.id.keyword_input)).perform(replaceText(picName1), closeSoftKeyboard());
        onView(withId(R.id.from_date_input)).perform(replaceText(fromDate));
        onView(withId(R.id.to_date_input)).perform(replaceText(toDate));

        // checkbox: search by area
        onView(withId(R.id.search_by_area_checkbox)).perform(click());

        // click search and return
        onView(withId(R.id.search_button)).perform(click());

        onView(withId(R.id.photo_name_text)).check(matches((isDisplayed()))).check(matches(withText(picName1)));

        // search picture 2
        onView(withId(R.id.menu_search)).perform(click());
        onView(withId(R.id.keyword_input)).check(matches((isDisplayed()))).perform(replaceText(picName2), closeSoftKeyboard());
        onView(withId(R.id.from_date_input)).perform(replaceText(fromDate));
        onView(withId(R.id.to_date_input)).perform(replaceText(toDate));

        // checkbox: search by area
        onView(withId(R.id.search_by_area_checkbox)).perform(click());

        // click search and return
        onView(withId(R.id.search_button)).perform(click());

        // No result should be found
        onView(withId(R.id.photo_name_text)).check(matches((isDisplayed()))).check(matches(withText(NO_RESULT_FOUND)));
    }
}

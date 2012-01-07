package com.madgag.agit;

import android.content.Intent;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.google.inject.util.Modules;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.R.id.*;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static com.madgag.agit.matchers.IsCheckedMatcher.checked;
import static com.madgag.agit.matchers.IsCheckedMatcher.unchecked;
import static com.xtremelabs.robolectric.matchers.TextViewHasTextMatcher.hasText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CloneLauncherActivityRobolectricTest {

    @Inject CloneLauncherActivity activity;

	final String appleProjectSourceUri="/example/apple";
	final String targetDir="/sdcard/tango";


    Checkable bareRepoCheckbox, defaultLocationCheckBox;
    TextView directoryEditText;
    GitIntentBuilder clone;

    @Before
    public void setup() {
        // Override the default RoboGuice module
        // RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new AgitIntegrationTestModule()));
        clone = new GitIntentBuilder("");
        bareRepoCheckbox = checkable(R.id.BareRepo);
        defaultLocationCheckBox = checkable(UseDefaultGitDirLocation);
        directoryEditText = textView(GitDirEditText);
    }

    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.util.reset();
    }



	@Test
	public void shouldUseSpecifiedRepoUrlFromIntentIfSupplied() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).toIntent());

        assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
        assertThat(defaultLocationCheckBox, checked());
	}

    @Test
    public void shouldUseSpecifiedRepoDirLocationFromIntentIfSupplied() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).targetDir(targetDir).toIntent());

		assertThat(textView(GitDirEditText), hasText(targetDir));
		assertThat(defaultLocationCheckBox, unchecked());
		assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
	}

//    @Test
//    public void shouldShowHelpfulMessageIfSourceUriTextBoxIsBlank() {
//        startAndResumeActivityWith(clone.toIntent());
//
//        assertThat(textOfView(CloneReadinessMessage).toString(), containsString("Enter a url"));
//	}

    @Test
    public void shouldUpdateCheckoutFolderNameToReflectBareRepo() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).toIntent());

        bareRepoCheckbox.setChecked(true);
        assertThat(textOfView(GitDirEditText), charSequence(endsWith(".git")));
        
        bareRepoCheckbox.setChecked(false);
        assertThat(textOfView(GitDirEditText), not(charSequence(endsWith(".git"))));
    }

    private void startAndResumeActivityWith(Intent intent) {
        startActivityWith(intent);
        activity.onResume();
    }

    private void startActivityWith(Intent intent) {
        activity.setIntent(intent);
        activity.onStart();
    }

    private Checkable checkable(int checkableId) {
        return (Checkable) view(checkableId);
    }

    private CharSequence textOfView(int textViewId) {
        return textView(textViewId).getText();
    }


    private TextView textView(int textViewId) {
        return (TextView) view(textViewId);
    }

    private View view(int viewId) {
        return activity.findViewById(viewId);
    }

}
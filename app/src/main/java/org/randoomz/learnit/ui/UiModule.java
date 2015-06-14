package org.randoomz.learnit.ui;

import dagger.Module;

/**
 * Created by gerard on 11/06/2015.
 */
@Module(
    injects = {
        MainActivity.class,
        MainActivityFragment.class
    },
    complete = false,
    library = true
)
public class UiModule {
}

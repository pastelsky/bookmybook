package com.example.shubhamkanodia.bookmybook;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

public class Application extends android.app.Application {

    public void onCreate() {
        Parse.enableLocalDatastore(this);

        if(!ParseCrashReporting.isCrashReportingEnabled())
            ParseCrashReporting.enable(this);

        Parse.initialize(this, "0aUlwpoTCVRhQnmACWQ6SvmnE9huiT8HwJL6UMyG", "3HEKP2rsi16yjkbKDESI65qth7DTgoF18bZfN6Wu");

    }

}

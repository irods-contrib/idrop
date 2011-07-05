package org.irods.jargon.idrop.desktop.systraygui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface Constants {

    public static final SimpleDateFormat SDF = new SimpleDateFormat("MM-dd-yyyy");

    public static final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

    public static final String PREFERENCE_KEY_DEVICE_NAME = "device.name";

    public static final String PREFERENCE_KEY_FIRST_TIME_RUN = "first.time.run";

    public static final String PREFERENCE_KEY_SHOW_UI = "show.ui";

    public static final String PREFERENCE_KEY_SHOW_SPLASH = "show.splash";

    public static final String PREFERENCE_KEY_SHOW_PREFERENCES = "show.preferences";

    public static final String PREFERENCE_KEY_DEFAULT_LOCAL_DIR = "default.local.dir";

    public static final String PREFERENCE_KEY_SHOW_HIDDEN_FILES = "show.hidden.files";

    public static final int STARTUP_SEQUENCE_PAUSE_INTERVAL = 1000;

    public static final String PREF_LOGIN_PORT = "login.port";

    public static final String PREF_LOGIN_HOST = "login.host";

    public static final String PREF_LOGIN_ZONE = "login.zone";

    public static final String PREF_LOGIN_RESOURCE = "login.resource";

    public static final String PREF_LOGIN_USERNAME = "login.username";

}

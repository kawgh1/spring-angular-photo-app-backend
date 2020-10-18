package com.kwgdev.vineyard.utility;

import java.io.File;

/**
 * created by kw on 10/17/2020 @ 2:08 PM
 */
public class Constants {

//    since we are NOT saving the images in the SQL Database as a BLOB,
    // we are saving them here on the server
    // represents the location where we will save the user's pictures
    public static final String USER_FOLDER = "src/main/resources/static/images/users//";

    public static final String POST_FOLDER = "src/main/resources/static/images/posts//";

    public static final File TEMP_USER = new File("src/main/resources/static/images/users/temp/profile.png");
}

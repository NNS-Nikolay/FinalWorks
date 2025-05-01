package helpers;

import entities.Saucedemo.User;
import configs.PropertiesHelper;
import java.io.IOException;

public class UILoginHelper {


    public User getStandardUser() throws IOException {

        String username = PropertiesHelper.getProperty("ui.standard_username");
        String password = PropertiesHelper.getProperty("ui.standard_password");

        return new User(username, password);
    }

    public User getLockedUser() throws IOException {

        String username = PropertiesHelper.getProperty("ui.locked_out_username");
        String password = PropertiesHelper.getProperty("ui.locked_out_password");

        return new User(username, password);
    }

    public User getGlitchedUser() throws IOException {

        String username = PropertiesHelper.getProperty("ui.glitch_username");
        String password = PropertiesHelper.getProperty("ui.glitch_password");

        return new User(username, password);
    }

}

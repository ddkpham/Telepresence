package theshakers.cmpt276.sfu.ca.robottelepresense.Model;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by baesubin on 2018-10-21.
 */

//Author class allows to create instance of Author of message with id, name, avatar
public class Author implements IUser {
    private String id;
    private String name;
    private String avatar;

    public Author() {
        id = "author_id";
        name = "author_name";
        avatar = "author_avatar";
    }

    public Author(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}

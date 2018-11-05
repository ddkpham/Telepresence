package theshakers.cmpt276.sfu.ca.robottelepresense.Model;

import android.support.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

/**
 * Created by baesubin on 2018-10-21.
 */

// Message class to form the message for ChatActivity
public class Message implements IMessage, MessageContentType.Image {
   private String id;
   private String text;
   private Author author;
   private Date createdAt;
   private String image;

   public Message() {
       id = "message_id";
       text = "message_text";
       author = new Author();
       createdAt = new Date();
       image = null;
   }

   public Message(String id, String text, Author author, Date date, String image) {
       this.id = id;
       this.text = text;
       this.author = author;
       this.createdAt = date;
       this.image = image;
   }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return image;
    }
}
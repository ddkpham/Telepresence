package theshakers.cmpt276.sfu.ca.robottelepresense.Model;

import com.stfalcon.chatkit.commons.models.IMessage;
import java.util.Date;

/**
 * Created by baesubin on 2018-10-21.
 */

//Message class to form the mesaage
public class Message implements IMessage {
   private String id;
   private String text;
   private Author author;
   private Date createdAt;

   public Message() {
       id = "message_id";
       text = "message_text";
       author = new Author();
       createdAt = new Date();
   }

   public Message(String id, String text, Author author, Date date) {
       this.id = id;
       this.text = text;
       this.author = author;
       this.createdAt = date;
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
}
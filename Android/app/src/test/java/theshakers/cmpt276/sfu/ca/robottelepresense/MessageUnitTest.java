package theshakers.cmpt276.sfu.ca.robottelepresense;

import org.junit.Test;

import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Author;
import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Message;

import static org.junit.Assert.assertEquals;

//unit testing for Message class
public class MessageUnitTest {
    @Test
    public void creation_isCorrect() throws Exception {
        Message messageWithDefaultConstructor = new Message();
        assertEquals(messageWithDefaultConstructor.getId(), "message_id");
        assertEquals(messageWithDefaultConstructor.getUser().getName(), "author_name");

        Author test = new Author("test", "null", "pepper");
        Message messageWithParmeterizedConstructor = new Message ("test", "null", test, null);
        assertEquals(messageWithParmeterizedConstructor.getId(), "test");
        assertEquals(messageWithParmeterizedConstructor.getText(), "null");
        assertEquals(messageWithParmeterizedConstructor.getUser().getName(), "null");
    }
}
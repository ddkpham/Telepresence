package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.text.AutoText;

import org.junit.Test;

import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Author;

import static org.junit.Assert.assertEquals;

//unit testing for Author class
public class AuthorUnitTest {
    @Test
    public void creation_with_default_constructor_isCorrect() throws Exception {
        Author authorWithDefaultConstructor = new Author();
        assertEquals(authorWithDefaultConstructor.getId(), "author_id");
        assertEquals(authorWithDefaultConstructor.getAvatar(), "author_avatar");
        assertEquals(authorWithDefaultConstructor.getName(), "author_name");
    }

    @Test
    public void creation_with_parameterized_constructor_isCorrect() throws Exception {
        Author authorWithParmeterizedConstructor = new Author("test", "null", "pepper");
        assertEquals(authorWithParmeterizedConstructor.getId(), "test");
        assertEquals(authorWithParmeterizedConstructor.getAvatar(), "null");
        assertEquals(authorWithParmeterizedConstructor.getName(), "author_name");
    }
}
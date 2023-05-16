package com.mucommander.ui.dialog.bookmark;

import com.mucommander.ui.main.MainFrame;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EditBookmarkTest {
    private EditBookmarksDialog editBookmarksDialog;
    @BeforeTest
    public void setUp() {
        editBookmarksDialog = mock(EditBookmarksDialog.class);
        when(editBookmarksDialog.getFreeNameVariation(anyString())).thenCallRealMethod();
    }

    @Test
    public void testCase1() {
        // This declaration set the if statement in node 2 to never meet
        when(editBookmarksDialog.containsName("book name")).thenReturn(true);
        // This declaration set the while statement in node 6 to never meet
        when(editBookmarksDialog.containsName("book name (2)")).thenReturn(false);

        // This input does not meet the if statement in node 3
        String name = "book name";
        String result = editBookmarksDialog.getFreeNameVariation(name);

        assert result.equals("book name (2)");
    }

    @Test
    public void testCase2() {
        // This declaration set the if statement in node 2 to never meet
        when(editBookmarksDialog.containsName("book name (2)")).thenReturn(true);
        // This declaration set the while statement in node 6 to never meet
        when(editBookmarksDialog.containsName("book name (3)")).thenReturn(false);

        // This input meet the if statement in node 3
        String name = "book name (2)";
        String result = editBookmarksDialog.getFreeNameVariation(name);

        assert result.equals("book name (3)");
    }

    @Test
    public void testCase3() {
        // This declaration set the if statement in node 2 to never meet
        when(editBookmarksDialog.containsName("book name (2)")).thenReturn(true);
        // This declaration set the while statement in node 6 to meet once
        when(editBookmarksDialog.containsName("book name (3)")).thenReturn(true);

        // This input meet the if statement in node 3
        String name = "book name (2)";
        String result = editBookmarksDialog.getFreeNameVariation(name);

        assert result.equals("book name (4)");
    }

    @Test
    public void testCase4() {
        // This declaration set the if statement in node 2 to always meet
        when(editBookmarksDialog.containsName("book name (2)")).thenReturn(false);

        String name = "book name (2)";
        String result = editBookmarksDialog.getFreeNameVariation(name);

        assert result.equals("book name (2)");
    }
}

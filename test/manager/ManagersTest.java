package manager;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void isGetDefaultReturnsNotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void isGetDefaultHistoryReturnsNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    public void isGetDefaultFileBackedReturnsNotNull() throws IOException {
        assertNotNull(Managers.getDefaultFileBacked(File.createTempFile("testFile", ".csv")));
    }
}
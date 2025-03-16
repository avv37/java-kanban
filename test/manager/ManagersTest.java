package manager;

import org.junit.jupiter.api.Test;

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
}
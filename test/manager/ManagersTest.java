package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    public void isGetDefaultReturnsNotNull() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    public void isGetDefaultHistoryReturnsNotNull() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}
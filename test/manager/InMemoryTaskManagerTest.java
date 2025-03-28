package manager;

class InMemoryTaskManagerTest extends TaskManagerAbstractTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}
package site.autzone.event.handler.task.listener;

public interface Manageable {
	enum STATUS {
		PAUSE,INTERRUPT,RUNNING
	}
	
	void pause();
	void resume();
	void interrupt();
}

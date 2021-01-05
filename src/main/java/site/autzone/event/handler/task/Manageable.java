package site.autzone.event.handler.task;

public interface Manageable {
	enum STATUS {
		PAUSE,INTERRUPT,RUNNING
	}
	
	void pause();
	void resume();
	void interrupt();
}

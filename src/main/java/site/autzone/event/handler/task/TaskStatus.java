package site.autzone.event.handler.task;

public enum TaskStatus {
	/**
	 * 该任务已初始化，但尚未被计划
	 */
	Created("创建成功", 0),
	/**
	 * 任务已经开始执行，但是被取消
	 */
	Canceled("取消执行", 1),
	/**
	 * 由于未处理异常的原因而完成的任务
	 */
	Faulted("执行异常", 2),
	/**
	 * 已成功完成执行的任务
	 */
	RanToCompletion("任务完成", 3),
	/**
	 * 该任务正在运行，但尚未完成
	 */
	Running("正在执行", 4),
	/**
	 * 该任务正在等待,
	 */
	WaitingForActivation("等待激活", 5),
	/**
	 * 该任务已完成执行，正在隐式等待附加的子任务完成
	 */
	WaitingForChildrenToComplete("等待子任务完成", 6),
	/**
	 * 该任务已被计划执行，但尚未开始真正执行
	 */
	WaitingToRun("等待执行", 7);
	
	private String name;
	private int code;
	
	private TaskStatus(String name, int code) {
		this.name = name;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}

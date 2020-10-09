package site.autzone.event.handler.task.listener;

import java.util.concurrent.TimeUnit;

public class Helper {
  /**
   * 挂起秒
   * @param seconds
   */
  public static void SLEEP_SECONDS(int seconds) {
      if(seconds > 0) {
          try {
              TimeUnit.SECONDS.sleep(seconds);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
      }
  }
  
  /**
   * 挂起毫秒
   * @param milliseconds
   */
  public static void SLEEP_MILLISECONDS(int milliseconds) {
      if(milliseconds > 0) {
          try {
              TimeUnit.MILLISECONDS.sleep(milliseconds);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
      }
  }
}

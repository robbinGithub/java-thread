package cn.itcast.heima2;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 
 *  void await():如果当前count大于0,当前线程将会wait,直到count等于0或者中断。PS：当count等于0的时候，再去调用await()，线程将不会阻塞，而是立即运行。
 * 
 *  boolean await(long timeout, TimeUnit unit)：使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断或超出了指定的等待时间。

    void countDown()： 递减锁存器的计数，如果计数到达零，则释放所有等待的线程。
	
	long getCount() ：获得计数的数量
	
	String toString() ：没什么好说的


 * @author robbin
 * @see http://www.cnblogs.com/waterystone/p/4920797.html
 */
public class CountdownLatchTest {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final CountDownLatch cdOrder = new CountDownLatch(1);
		final CountDownLatch cdAnswer = new CountDownLatch(3);		
		for(int i=0;i<3;i++){
			Runnable runnable = new Runnable(){
					public void run(){
					try {
						System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");	
						cdOrder.await();        // wait
						System.out.println("线程" + Thread.currentThread().getName() + "已接受命令");	
						Thread.sleep((long)(Math.random()*10000));	
						System.out.println("线程" + Thread.currentThread().getName() + "回应命令处理结果");	
													
						cdAnswer.countDown();	 // -1					
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}
			};
			service.execute(runnable);
		}		
		try {
			Thread.sleep((long)(Math.random()*10000));
		
			System.out.println("线程" + Thread.currentThread().getName() + "即将发布命令");
			cdOrder.countDown();
			System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待结果");
			cdAnswer.await();
			System.out.println("线程" + Thread.currentThread().getName() + "已收到所有响应结果");	
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
		service.shutdown();

	}
}

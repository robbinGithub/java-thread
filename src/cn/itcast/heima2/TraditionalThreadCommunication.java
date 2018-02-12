package cn.itcast.heima2;

/**
 * 子线程和主线程交替执行(通过全局变量控制条件)
 * 
 * T1 sub 循环(状态机)  await exec
 * 
 * T2 main 循环(状态机) await exec
 * 
 * T1和T2操作互斥
 * 
 * 
 * 
 * T1 exec 通知T2执行 (信号,没有具体处理,控制循环(状态机)),线程wait notify只能控制线程状态 wait/run T2 exec
 * 通知T1执行 T1 exec T2 exec
 * 
 * @author robbin
 * 
 */
public class TraditionalThreadCommunication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		final Business business = new Business();
		new Thread(
				new Runnable() {
					
					@Override
					public void run() {
					
						for(int i=1;i<=50;i++){
							business.sub(i);
						}
						
					}
				}
		).start();
		
		for(int i=1;i<=50;i++){
			business.main(i);
		}
		
	}

}
  class Business {
	  private boolean bShouldSub = true;
	  public synchronized void sub(int i){
		  while(!bShouldSub){
			  try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
			for(int j=1;j<=10;j++){
				System.out.println("sub thread sequence of " + j + ",loop of " + i);
			}
		  bShouldSub = false;
		  this.notify();
	  }
	  
	  public synchronized void main(int i){
		  	while(bShouldSub){
		  		try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  	}
			for(int j=1;j<=100;j++){
				System.out.println("main thread sequence of " + j + ",loop of " + i);
			}
			bShouldSub = true;
			this.notify();
	  }
  }

  
  
 /** 
 wait和notify概念理解
 
  例如:线程A
     synchronized(obj) {
                 while(!condition) {
                           obj.wait();
                   }
                  obj.doSomething();
    }
  当线程A获得了obj锁后，发现条件condition不满足，无法继续下一处理，于是线程A就wait()。在另一线程B中，如果B更改了某些条件，使得线程A的condition条件满足了，就可以唤醒线程A。
  线程B
        synchronized(obj) {
                condition = true;
                obj.notify();
          }
  需要注意的概念是：  
     1.调用obj的wait()， notify()方法前，必须获得obj锁，也就是必须写在synchronized(obj) {……} 代码段内。  
     2.调用obj.wait()后，线程A就释放了obj的锁，否则线程B无法获得obj锁，也就无法在synchronized(obj) {……} 代码段内唤醒A.  
     3.当obj.wait()方法返回后，线程A需要再次获得obj锁，才能继续执行。  
     4.如果A1，A2，A3都在obj.wait()，则B调用obj.notify()只能唤醒A1，A2，A3中的一个（具体哪一个由JVM决定）。  
     5.obj.notifyAll()则能全部唤醒A1，A2，A3，但是要继续执行obj.wait()的下一条语句，必须获得obj锁，因此，A1，A2，A3只有一个有机会获得锁继续执行，例如A1，其余的需要等待A1释放obj锁之后才能继续执行。
     6.当B调用obj.notify/notifyAll的时候，B正持有obj锁，因此，A1，A2，A3虽被唤醒，但是仍无法获得obj锁。直到B退出synchronized块，释放obj锁后，A1，A2，A3中的一个才有机会获得锁继续执行。
     
     */
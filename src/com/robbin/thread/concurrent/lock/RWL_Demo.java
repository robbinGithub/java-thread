package com.robbin.thread.concurrent.lock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 通过ReadWriteReentrantLock源代码分析AbstractQueuedSynchronizer共享模式

1.特点
      ReentrantLock能够实现共享资源的互斥访问，但是它在某些条件下效率比较低下。比如，多个线程要查询（或者说读取）某列车的余票数，
      如果使用ReentrantLock，那么多个线程的查询操作只能互斥，也就是说一个线程查询完成,下一个线程才能查询。考虑到读取操作并不会改变数据的值，
      如果能够并发的进行读取访问，既可以得到正确的结果也能提高效率。但是，当某个线程要进行购票或者退票操作时（也就是写操作），
      这个时候就需要线程间的互斥，购票或者退票操作必须等待所有查询操作完成以后才能执行（同理，如果正在进行退票或者购票，那么其它线程的查询操作也必须等待，
      直到退票或者购票完成才，这样才能查询到正确的结果），而各个线程的退票和购票操作也必须互斥的进行才能保证余票的正确。
      在此种需求下就需要通过ReadWriteReentrantLock来实现。

   ReadWriteReentrantLock的特点(读读并行 、读写互斥、 写写互斥)
      （1）读取操作是并发的
      （2）不同线程之间的读取和写入是互斥的
      （3）不同线程之间的写入和写入是互斥的

       如果一个线程【既要读取又要写入】，那么【获取写锁即可】（或者先获取写锁，然后获取读锁；反之，若先获取读锁，再去获取写锁就会造成死锁现象）。

       当一个线程先获取了写锁，然后获取了读锁，并在释放读锁前释放了写锁，那么该线程就由写锁降级为读锁。

       写锁才对应有Condition队列，而读锁没有。这就意味着，获取写锁后可以使用await和signal方法，而获取读锁后不可以使用await和signal方法。

      为了书写方便，在本博客中ReadWriteReentrantLock会简写成RWL。

2.使用
       下面的示例中定义了一个RWL_Demo类，它有三个数据成员，rwl、data、runTimes分别表示读写锁、共享数据、写操作的执行次数。在RWL_Demo的内部又定义两个内部类WriteProcess和ReadProcess，这两个内部类分别实现了Runnalbe接口，并在run方法内进行写入和读取操作。在main方法中创建了一个具有6个Worker的线程池对象，并向其中添加了4个ReadProcess对象和2个WriteProcess对象，通过运行结果我们可以发现读操作的并发性以及读写操作以及写写操作的互斥性。
 * 
 * @author robbin.zhang
 * @date 2017/03/16 09:59
 * @see http://www.cnblogs.com/nullzx/p/5114009.html
 */
public class RWL_Demo {
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private volatile Integer data;
	private volatile int runTimes = 10;
	
	public class ReadProcess implements Runnable{
		private String id;
		Random rd = new Random();
		public ReadProcess(String id){
			this.id = id; 
		}
		@Override
		public void run() {
			while(runTimes > 0){
				if(data != null){
					rwl.readLock().lock();
					
				System.out.println(id + "   read data = " + data);
					try {
						Thread.sleep(rd.nextInt(500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(id + "   read over");
					
					rwl.readLock().unlock();
				}
				try {
					Thread.sleep(rd.nextInt(100));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class WriteProcess implements Runnable{
		private String id;
		Random rd = new Random();
		public WriteProcess(String id){
			this.id = id; 
		}
		@Override
		public void run() {
			while(runTimes > 0){
				rwl.writeLock().lock();
				
				data = new Integer(rd.nextInt(20));
			       System.out.println(id + "   write data = " + data);
				try {
					Thread.sleep(rd.nextInt(500));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runTimes--;
				System.out.println(id + "   write over");
				rwl.writeLock().unlock();
				
				try {
					Thread.sleep(rd.nextInt(100));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		RWL_Demo rwl_d = new RWL_Demo();
		ExecutorService pool = Executors.newFixedThreadPool(6);
		pool.submit(rwl_d.new ReadProcess("R1"));
		pool.submit(rwl_d.new ReadProcess("R2"));
		pool.submit(rwl_d.new ReadProcess("R3"));
		pool.submit(rwl_d.new ReadProcess("R4"));
		
		pool.submit(rwl_d.new WriteProcess("W1"));
		pool.submit(rwl_d.new WriteProcess("W2"));
		
		pool.shutdown();
	}
}
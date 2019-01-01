package cn.itcast.heima2;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {
	
	 static final int SHARED_SHIFT   = 16;
     static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
     static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
     static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
     
	public static void main(String[] args) {
		
		/*System.out.println("SHARED_UNIT:" + SHARED_UNIT);      // 65536
		System.out.println("MAX_COUNT:" + MAX_COUNT);          // 65535
		System.out.println("EXCLUSIVE_MASK:" + EXCLUSIVE_MASK); // 65535
		if(true) {return;}*/
		
		/**
		 * 读写锁是通过，同步器的sate你
		 */
		
		final Queue3 q3 = new Queue3();
		for(int i=0;i<3;i++)
		{
			new Thread(){
				public void run(){
					while(true){
						q3.get();						
					}
				}
				
			}.start();

			new Thread(){
				public void run(){
					while(true){
						q3.put(new Random().nextInt(10000));
					}
				}			
				
			}.start();
		}
		
	}
}

class Queue3{
	private Object data = null;   // 每个资源上都有一把锁，而不是用锁池
	ReadWriteLock rwl = new ReentrantReadWriteLock();
	public void get(){
		rwl.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " be ready to read data!");
			Thread.sleep((long)(Math.random()*1000));
			System.out.println(Thread.currentThread().getName() + "have read data :" + data);			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			rwl.readLock().unlock();
		}
	}
	
	public void put(Object data){

		rwl.writeLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " be ready to write data!");					
			Thread.sleep((long)(Math.random()*1000));
			this.data = data;		
			System.out.println(Thread.currentThread().getName() + " have write data: " + data);					
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			rwl.writeLock().unlock();
		}
		
	
	}
}

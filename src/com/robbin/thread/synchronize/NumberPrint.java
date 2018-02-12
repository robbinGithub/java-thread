package com.robbin.thread.synchronize;
/**
 * 用多线程实现连续的1,2,1,2,1,2,1,2,1,2输出
 * @author robbin.zhang
 * @date 2017/03/15 11:27
 */
public class NumberPrint implements Runnable {
	
	private int number;  
    public byte res[];  
    public static int count = 5;  
    public NumberPrint(int number, byte a[]){  
        this.number = number;  
        res = a;  
    }  
    

	@Override
	public void run() {
		 synchronized (res){  
            while(count-- > 0){  
                try {  
                    res.notify();//唤醒等待res资源的线程，把锁交给线程（该同步锁执行完毕自动释放锁）  
                    System.out.println(" "+number);  
                    res.wait(); //释放CPU控制权，释放res的锁，本线程阻塞，等待被唤醒。  
                    System.out.println("------线程"+Thread.currentThread().getName()+"获得锁，wait()后的代码继续运行："+number);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
            }
            return;  
	    }
	 }  

	public static void main(String args[]){  
	    final byte a[] = {0};//以该对象为共享资源  
	    new Thread(new NumberPrint((1),a),"1").start();  
	    new Thread(new NumberPrint((2),a),"2").start();  
	}  
	
//	 2
//	 1
//	------线程2获得锁，wait()后的代码继续运行：2
//	 2
//	------线程1获得锁，wait()后的代码继续运行：1
//	 1
//	------线程2获得锁，wait()后的代码继续运行：2
//	 2
//	------线程1获得锁，wait()后的代码继续运行：1
//	
}

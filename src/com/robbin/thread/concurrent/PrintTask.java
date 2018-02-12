package com.robbin.thread.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 当使用Lock来保证线程同步时，需使用Condition对象来使线程保持协调。
 * Condition实例被绑定在一个Lock的对象上，使用Lock对象的方法newCondition()获取Condition的实例。Condition提供了下面三种方法，来协调不同线程的同步：
 * 
	1、await()：导致当前线程等待，直到其他线程调用该Condition的signal()或signalAll()方法唤醒该线程。
	2、signal()：唤醒在此Lock对象上等待的单个线程。
	3、signalAll()：唤醒在此Lock对象上等待的所有线程。
	
	我在进行《疯狂Java讲义（精粹）第二版》多线程一章的一道习题的编程时遇到了使用了await()和signalAll()进行线程同步，却导致了死锁的问题。
	在网络搜寻了下，原因是我对await()方法理解有误导致。
	
	题目如下：写2个线程，其中一个线程打印1-52，另一个线程打印A-Z，打印顺序应该是12A34B56C……5152Z，需使用多线程通信的知识解决。
	附上最终正确运行的代码，同时加上了开始时犯的错误的注释。
	
 *  @author robbin
 *  @see http://m.blog.csdn.net/article/details?id=46364721
 *
 */
public class PrintTask<T> implements Runnable {
	
	private List<T> charList = new ArrayList<>();	//需打印的队列。由于打印项目既包含字符也包含递增的数字，此处使用泛型
	private int period;		//每次打印的个数
	private int priority;	//优先级，即在多个队列中的打印次序
	private int total;		//打印队列总数
	public static int sequence = 0;		//当前打印的队列序号
	private static final Lock lock = new ReentrantLock();		//由于需要锁定的sequence是类成员，创建一个static锁，保证该类不同线程实例能够感知到signalAll()
	private static final Condition condition = lock.newCondition();	//类Condition成员
	//private final Lock lock = new ReentrantLock();----------->当不使用static定义Lock和Condition时，由于不同线程为不同的实例，相互之间
	//private final Condition condition = lock.newCondition();	无法感知其他类发出的signalAll()，导致线程之间相互等待却无法得到响应
    
	public PrintTask(List<T> charList, int period, int priority, int total) {
		this.charList = charList;
		this.period = period;
		this.priority = priority;
		this.total = total;
	}
	
	
	/**
	 * 只有一个线程打印，打印函数互斥 lock
	 * 
	 * T1:  打印1个字符                                 打印1个字符
	 * T2:           打印2个字符                                 打印2个字符
	 * 
	 * 
	 * T1: 打印1个字符      打印1个字符    打印1个字符
	 * T2:                           打印2个字符      打印2个字符    打印2个字符
	 * 
	 * condition await/signal 干预协调
	 * 
	 */
	@Override
	public void run() {
		Iterator<T> iter = charList.iterator();
		while(true) {
			lock.lock();	
			try {	
				
				// 满足条件->执行->signal
				if (sequence % total == priority) {
					for (int i = 0; i < period; i++) {
						if(iter.hasNext()) {
							System.out.print(iter.next());
						}
						else {
							break;
						}
					}
					++sequence;
					condition.signalAll();	//该类其他实例均能接受到该signalAll()方法
				}
				// 不满足条件->await
				else {
					condition.await();	//类成员condition的await()方法，告知类的所有实例均感知signalAll()
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				lock.unlock();
			}
			
			// 错误写法：
			
				
//				int i = 0;
//				int len = charList.size();
//				while(++i<period && sequence <len){
//					System.out.print(charList.get(++sequence));
//				}
			
//				if(sequence == len) break;
//			   执行
//				condition.signalAll();
//			 等待
//				condition.await();
		
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			
		}
	}
	
	public static void main(String[] args) {
		
		final int TOTAL = 2;
		
		//生成数字列表1-52
		List<Integer> numbers = new ArrayList<Integer>();
		for (int i = 1; i <= 52; i++) {
			numbers.add(i);
		}
		//生成字母列表A-Z
		List<Character> chars = new ArrayList<>();
		for (char i = 'A'; i <= 'Z'; i++)
		{
			chars.add(i);
		}
		//PrintTask(打印队列, 每次打印长度, 次序, 队列总数) 
		PrintTask<Integer> task1 = new PrintTask<Integer>(numbers, 2, 0, TOTAL);
		PrintTask<Character> task2 = new PrintTask<Character>(chars, 1, 1, TOTAL);
		new Thread(task1).start();
		new Thread(task2).start();
		
	}

}

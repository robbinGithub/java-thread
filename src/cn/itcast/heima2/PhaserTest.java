package cn.itcast.heima2;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @author robbin.zhang
 * @date 2019-01-15 10:17:00
 * @see https://blog.csdn.net/u010739551/article/details/51083004
 */
public class PhaserTest {
	
	/***
	 *  下面说说Phaser的高级用法，在Phaser内有2个重要状态，分别是phase和party。
	 *  phase就是阶段，初值为0，当所有的线程执行完本轮任务，同时开始下一轮任务时，
	 *  意味着当前阶段已结束，进入到下一阶段，phase的值自动加1。party就是线程，
	 *  party=4就意味着Phaser对象当前管理着4个线程。Phaser还有一个重要的方法经常需要被重载，
	 *  那就是boolean onAdvance(int phase, int registeredParties)方法。此方法有2个作用：
	 *  1、当每一个阶段执行完毕，此方法会被自动调用，因此，重载此方法写入的代码会在每个阶段执行完毕时执行，
	 *  相当于CyclicBarrier的barrierAction。
	 *  2、当此方法返回true时，意味着Phaser被终止，因此可以巧妙的设置此方法的返回值来终止所有线程。
	 * @author liujun
	 */
	public static class MyPhaser extends Phaser { /*['feizə]*/
	 
		@Override
		protected boolean onAdvance(int phase, int registeredParties) {	//在每个阶段执行完成后回调的方法
			
			switch (phase) {
			case 0:
				return studentArrived();
			case 1:
				return finishFirstExercise();
			case 2:
				return finishSecondExercise();
			case 3:
				return finishExam();
			default:
				return true;
			}
			
		}
		
		private boolean studentArrived(){
			System.out.println("学生准备好了,学生人数："+getRegisteredParties());
			return false;
		}
		
		private boolean finishFirstExercise(){
			System.out.println("第一题所有学生做完");
			return false;
		}
		
		private boolean finishSecondExercise(){
			System.out.println("第二题所有学生做完");
			return false;
		}
		
		private boolean finishExam(){
			System.out.println("第三题所有学生做完，结束考试");
			return true;
		}
		
	}
	
	public static class StudentTask implements Runnable {
		 
		private Phaser phaser;
		
		public StudentTask(Phaser phaser) {
			this.phaser = phaser;
		}
	 
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName()+"到达考试");
			phaser.arriveAndAwaitAdvance();
			
			System.out.println(Thread.currentThread().getName()+"做第1题时间...");
			doExercise1();
			System.out.println(Thread.currentThread().getName()+"做第1题完成...");
			phaser.arriveAndAwaitAdvance();
			
			System.out.println(Thread.currentThread().getName()+"做第2题时间...");
			doExercise2();
			System.out.println(Thread.currentThread().getName()+"做第2题完成...");
			phaser.arriveAndAwaitAdvance();
			
			System.out.println(Thread.currentThread().getName()+"做第3题时间...");
			doExercise3();
			System.out.println(Thread.currentThread().getName()+"做第3题完成...");
			phaser.arriveAndAwaitAdvance();
		}
	 
		private void doExercise1() {
			long duration = (long)(Math.random()*10);
			try {
				TimeUnit.SECONDS.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void doExercise2() {
			long duration = (long)(Math.random()*10);
			try {
				TimeUnit.SECONDS.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void doExercise3() {
			long duration = (long)(Math.random()*10);
			try {
				TimeUnit.SECONDS.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 题目：5个学生参加考试，一共有三道题，要求所有学生到齐才能开始考试
	 * ，全部做完第一题，才能继续做第二题，后面类似。
	 * 
	 * Phaser有phase和party两个重要状态，
	 * phase表示阶段，party表示每个阶段的线程个数，
	 * 只有每个线程都执行了phaser.arriveAndAwaitAdvance();
	 * 才会进入下一个阶段，否则阻塞等待。
	 * 例如题目中5个学生(线程)都条用phaser.arriveAndAwaitAdvance();就进入下一题
	 * @author liujun
	 */
	public static void main(String[] args) {
		
		MyPhaser phaser = new MyPhaser();
		StudentTask[] studentTask = new StudentTask[5];
		for (int i = 0; i < studentTask.length; i++) {
			studentTask[i] = new StudentTask(phaser);
			phaser.register();	//注册一次表示phaser维护的线程个数
		}
		
		Thread[] threads = new Thread[studentTask.length];
		for (int i = 0; i < studentTask.length; i++) {
			threads[i] = new Thread(studentTask[i], "Student "+i);
			threads[i].start();
		}
		
		//等待所有线程执行结束
		for (int i = 0; i < studentTask.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Phaser has finished:"+phaser.isTerminated());
		
	/*	Student 1到达考试
		Student 3到达考试
		Student 0到达考试
		Student 2到达考试
		Student 4到达考试
		学生准备好了,学生人数：5
		Student 4做第1题时间...
		Student 2做第1题时间...
		Student 3做第1题时间...
		Student 0做第1题时间...
		Student 1做第1题时间...
		Student 0做第1题完成...
		Student 4做第1题完成...
		Student 1做第1题完成...
		Student 2做第1题完成...
		Student 3做第1题完成...
		第一题所有学生做完
		Student 2做第2题时间...
		Student 3做第2题时间...
		Student 0做第2题时间...
		Student 4做第2题时间...
		Student 4做第2题完成...
		Student 1做第2题时间...
		Student 1做第2题完成...
		Student 0做第2题完成...
		Student 2做第2题完成...
*/
	}
}

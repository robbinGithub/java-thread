package com.robbin.thread.concurrent.threadpool;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
/**
 * Future及FutureTask的使用
      Future以及FutureTask是线程池实现的基础元素，但不是说Future及FutureTask只能在线程池中才能使用，下面的例子就说明了FutureTask独立使用的情况。
                  在这个例子中，我们首先随机产生了2000个整数存于数组中，然后创建了两个线程，一个线程寻找前1000个数的最大值，另个一线程寻找后1000个数的最大值。
                  主线程比较这两个线程的返回结果来确定这2000个数的最大值值。
 * @author robbin.zhang
 * @date 2017/03/16 10:30
 * @see http://www.cnblogs.com/nullzx/p/5147004.html
 *
 */
public class FutureDemo {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		int[] a = new int[2000];
		Random rd = new Random();
		for(int i = 0; i < 2000; i++){
			a[i] = rd.nextInt(20000);
		}
		
		class FindMax implements Callable<Integer>{
			private int begin,end;int a[];
			public FindMax(int a[],int begin, int end){
				this.a = a;
                this.begin = begin;
				this.end = end;
			}
			@Override
			public Integer call() throws Exception {
				int maxInPart = a[begin];
				for(int i = begin; i <= end; i++){
					if(a[i] > maxInPart){
						maxInPart = a[i];
					}
				}
				return new Integer(maxInPart);
			}
		}
		
		FutureTask<Integer> findMaxInFirstPart = 
                              new FutureTask<Integer>(new FindMax(a,0,999));
		FutureTask<Integer> findMaxInSecondPart = 
                              new FutureTask<Integer>(new FindMax(a,1000,1999));
		
		new Thread(findMaxInFirstPart).start();
		new Thread(findMaxInSecondPart).start();
		
		int maxInFirst =  (int) findMaxInFirstPart.get();
		int maxInSecond = (int) findMaxInSecondPart.get();
		System.out.println("Max is " + 
                            (maxInFirst > maxInSecond ? maxInFirst:maxInSecond));
		//验证结果是否正确
		int max = a[0];
		for(int i = 0; i < 2000; i++){
			if(a[i] > max){
				max = a[i];
			}
		}
		System.out.println(max);
	}
}
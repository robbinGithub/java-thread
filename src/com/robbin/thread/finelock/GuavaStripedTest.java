package com.robbin.thread.finelock;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.google.common.util.concurrent.Striped;

/**
 * Google Guava Striped 实现细粒度锁
 * @author robbin.zhang
 * @see https://blog.csdn.net/liyantianmin/article/details/52927313
 */
public class GuavaStripedTest {
	
	
	public static void main_01(String[] args) {
		String user1 = "张三";
		buy(user1, 10000,"1");
	}
	
	public static void main(String[] args) {
		
	  //运行开始时间
	  long startTime = System.currentTimeMillis();
	  //这个类主要是，使多个线程同时进行工作,如果不了解建议网上搜索相关的文章进行学习
	  final CyclicBarrier barrier = new CyclicBarrier(2);
	  //不限制大小的线程池
	  ExecutorService pool = Executors.newCachedThreadPool();
	  final String user1 = "张三";
	  final String user2 = "李四";
	  pool.execute(new Runnable() {
	   @Override
	   public void run() {
	    try {
	     barrier.await();
	     buy(user1, 10000, "1");
	    } catch (Exception e) {
	     e.printStackTrace();
	    }
	   }
	  });
	  pool.execute(new Runnable() {
	   @Override
	   public void run() {
	    try {
	     barrier.await();
	     buy(user2, 10000, "2");
	    } catch (Exception e) {
	     e.printStackTrace();
	    }
	   }
	  });
	  pool.shutdown();
	  while (!pool.isTerminated()) {  
	  }
	  System.out.println("运行时间为：【"+TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - startTime))+"】秒");
	}
	
	
	/**
	  * 购买产品
	  * @param user 用户
	  * @param buyAmount 购买金额
	  * @param productId 产品编号
	  */
	public static void buy (String user,Integer buyAmount,String productId){
		System.out.println(user+":开始购买【"+productId+"】的产品");
		Product product =  null ;//DB.getProduct(productId);
		if(product.getTotalAmount()>0&&product.getTotalAmount() >= buyAmount){
		     int residual = product.getTotalAmount()-buyAmount;
		     product.setTotalAmount(residual);//更新数据库
		     System.out.println(user+":成功购买【"+productId+"】产品,产品剩余价值为【"+residual+"】");
	    }else
	    {
		    System.out.println(user+":购买【"+productId+"】产品失败，产品剩余价值为【"+product.getTotalAmount()+"】");
		}
	}
	
	
	//在buy方法上加了synchronized,同时使当前线程睡眠5秒
	 
	/**
	  * 购买产品
	  * @param user 用户
	  * @param buyAmount 购买金额
	  * @param productId 产品编号
	  */
	public synchronized static void buy2(String user,Integer buyAmount,String productId)throws Exception{
	  System.out.println(user+":开始购买【"+productId+"】的产品");
	  Thread.sleep(5000);//睡眠5秒
	  Product product =  null; //DB.getProduct(productId);
	  if(product.getTotalAmount() > 0 && product.getTotalAmount() >= buyAmount){
	   int residual = product.getTotalAmount() - buyAmount;
	   product.setTotalAmount(residual);//更新数据库
	   System.out.println(user+":成功购买【"+productId+"】产品,产品剩余价值为【"+residual+"】");
	  }else{
	   System.out.println(user+":购买【"+productId+"】产品失败，产品剩余价值为【"+product.getTotalAmount()+"】");
	  }
	 }
	

	//创建一个弱引用的Striped<Lock>
	 private static final Striped<Lock> striped = Striped.lazyWeakLock(127);
	 /**
	  * 购买产品
	  * @param user 用户
	  * @param buyAmount 购买金额
	  * @param productId 产品编号
	  */
	 public static void buy3(String user,Integer buyAmount,String productId)throws Exception{
	  Lock lock = striped.get(productId);//获取锁
	  try{
	   lock.lock();//锁定
	   System.out.println(user+":开始购买【"+productId+"】的产品");
	   TimeUnit.SECONDS.sleep(5);//使当前线程睡眠5秒
	   Product product = null; //DB.getProduct(productId);
	   if(product.getTotalAmount() > 0 && product.getTotalAmount() >= buyAmount){
	    int residual = product.getTotalAmount() - buyAmount;
	    product.setTotalAmount(residual);//更新数据库
	    System.out.println(user+":成功购买【"+productId+"】产品,产品剩余价值为【"+residual+"】");
	   }else{
	    System.out.println(user+":购买【"+productId+"】产品失败，产品剩余价值为【"+product.getTotalAmount()+"】");
	   }
	  }finally{
	   lock.unlock();//释放锁
	  }
	 }
	
	
	public class Product {
		/** ID */
		private String id;
		/** 总价值 */
		private Integer totalAmount = 10000;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Integer getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(Integer totalAmount) {
			this.totalAmount = totalAmount;
		}
	}
}




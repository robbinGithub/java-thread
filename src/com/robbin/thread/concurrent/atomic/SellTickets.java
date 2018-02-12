package com.robbin.thread.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * @author robbin
 * @see http://www.cnblogs.com/nullzx/p/4967931.html
 *
 */
public class SellTickets {
	
	AtomicInteger tickets = new AtomicInteger(1);
	
	 class Seller implements Runnable{
	        @Override
	        public void run() {
	            while(tickets.get() > 0){
	                int tmp = tickets.get(); // 1  1 
	                if(tickets.compareAndSet(tmp, tmp-1)){ // 0 
	                    System.out.println(Thread.currentThread().getName()+"  "+tmp);
	                }
	            }
	        }
	        
	}
	 public static void main(String[] args) {
	        SellTickets st = new SellTickets();
	        new Thread(st.new Seller(), "SellerA").start();
	        new Thread(st.new Seller(), "SellerB").start();
	}
}
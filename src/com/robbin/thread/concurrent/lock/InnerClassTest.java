package com.robbin.thread.concurrent.lock;

import com.robbin.thread.concurrent.lock.RWL_Demo.ReadProcess;


public class InnerClassTest {
	
	public static void main(String[] args) {
		ReadProcess readProcess = new RWL_Demo().new ReadProcess("robbin");
	}
//	ReadProcess r = new ReadProcess();

}

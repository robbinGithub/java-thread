package cn.itcast.heima2;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 
 *  void await():�����ǰcount����0,��ǰ�߳̽���wait,ֱ��count����0�����жϡ�PS����count����0��ʱ����ȥ����await()���߳̽����������������������С�
 * 
 *  boolean await(long timeout, TimeUnit unit)��ʹ��ǰ�߳�������������������֮ǰһֱ�ȴ��������̱߳��жϻ򳬳���ָ���ĵȴ�ʱ�䡣

    void countDown()�� �ݼ��������ļ�����������������㣬���ͷ����еȴ����̡߳�
	
	long getCount() ����ü���������
	
	String toString() ��ûʲô��˵��


 * @author robbin
 * @see http://www.cnblogs.com/waterystone/p/4920797.html
 */
public class CountdownLatchTest {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final CountDownLatch cdOrder = new CountDownLatch(1);
		final CountDownLatch cdAnswer = new CountDownLatch(3);		
		for(int i=0;i<3;i++){
			Runnable runnable = new Runnable(){
					public void run(){
					try {
						System.out.println("�߳�" + Thread.currentThread().getName() + "��׼����������");	
						cdOrder.await();        // wait
						System.out.println("�߳�" + Thread.currentThread().getName() + "�ѽ�������");	
						Thread.sleep((long)(Math.random()*10000));	
						System.out.println("�߳�" + Thread.currentThread().getName() + "��Ӧ�������");	
													
						cdAnswer.countDown();	 // -1					
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}
			};
			service.execute(runnable);
		}		
		try {
			Thread.sleep((long)(Math.random()*10000));
		
			System.out.println("�߳�" + Thread.currentThread().getName() + "������������");
			cdOrder.countDown();
			System.out.println("�߳�" + Thread.currentThread().getName() + "�ѷ���������ڵȴ����");
			cdAnswer.await();
			System.out.println("�߳�" + Thread.currentThread().getName() + "���յ�������Ӧ���");	
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
		service.shutdown();

	}
}

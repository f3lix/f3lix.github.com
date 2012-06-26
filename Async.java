import java.util.concurrent.*;
import java.util.*;
import java.io.*;

public class Async {
		private ExecutorService ex
				= Executors.newSingleThreadExecutor();
		private Random rand = new Random(45);
		private void pause(int f) {
				try {
						TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(f));
				}
				catch(InterruptedException e) {
						System.out.println("pause() interrupted");
				}
		}
		public void shutdown() {
				ex.shutdown();
		}
		public Future<Integer> calInt(final int x, final int y) {
				return ex.submit(new Callable<Integer>() {
						public Integer call() {
								System.out.println("starting " + x + " + " + y);
								pause(500);
								return x + y;
						}
				});
		}
		public Future<Float> calFloat(final float x, final float y) {
				return ex.submit(new Callable<Float>() {
						public Float call() {
								System.out.println("starting " + x + " + " + y);
								pause(500);
								return x + y;
						}
				});
		}
		public static void main(String[] args) {
				Scanner in = new Scanner(System.in);
				int size = in.nextInt();
				int[][] list = new int[size][size];
				int i = 0;
				for(i = 0; i < size; i++)
						for(int j = 0; j < size; j++)
								list[i][j] = 0;

				int temp = 0;
				int j = 0;
				int k = 0;

				while(temp != size * size - 1) {
						for(; k < size; k++) {
								if(list[j][k] == 0) {
										list[j][k] = ++temp;
								} else {
										j += 1;
										break;
								}
						}
						
						for(k -= 1; j < size; j++) {
								if(list[j][k] == 0) {
										list[j][k] = ++temp;
								} else {
										k += 1;
										break;
								}
						}
						
						for(j -= 1; k > 0; k--) {
								if(list[j][k] == 0) {
										list[j][k] = ++temp;
								} else {
										j -= 1;
										break;
								}
						}
						for(; j > 0; j--) {
								if(list[j][k] == 0) {
										list[j][k] = ++temp;
								} else {
										k += 1;
										break;
								}
						}
				}

				for(i = 0; i < size; i++) {
						System.out.println();
						for(j = 0; j < size; j++) {
								System.out.print(list[i][j]);
								System.out.print(" ");
						}
				}
				System.out.println();
				/*
					 int a = 1;
					 int b = 1;
					 int max = 0;
					 Scanner in = new Scanner(System.in);
					 max = in.nextInt();
					 for( int i = 0; i < max; i++) {
					 System.out.println(a);
					 System.out.println(b);
					 a = a + b;
					 b = a + b;
					 }	*/
				/*
					 Async test = new Async();
					 List<Future<?>> results
					 = new CopyOnWriteArrayList<Future<?>>();
					 for(float f = 0.0f; f < 1.0f; f += 0.2f)
					 results.add(test.calFloat(f, f));
					 for(int i = 0; i < 5; i++)
					 results.add(test.calInt(i, i));
					 System.out.println("All async calls made");
					 while(results.size() > 0) {
					 for(Future<?> f : results)
					 if(f.isDone()) {
					 try {
					 System.out.println(f.get());
					 }
					 catch(Exception e) {
					 throw new RuntimeException(e);
					 }
					 results.remove(f);
					 }
					 }
					 test.shutdown();
					 */
				}
		}

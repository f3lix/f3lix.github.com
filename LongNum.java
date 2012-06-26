import java.util.*;

public class LongNum {
		private int a;
		private int b;
		private int[] num;
		public LongNum() {
		}
		public LongNum(int s) {
				num = new int[s];
				Random ran = new Random();
				int i;
				for(i = 0; i < s; i++)
						num[i] = ran.nextInt(10);
				while(num[s-1] == 0)
						num[s-1] = ran.nextInt(10);
		}
		public static LongNum add(LongNum nA, LongNum nB) {
				int aLen = nA.num.length;
				int bLen = nB.num.length;
				int cLen = Math.max(aLen, bLen);
				LongNum ret = new LongNum();
				ret.num = new	int[cLen];
				if(aLen >= bLen) {
						for (int i = 0; i < bLen; i++)
								ret.num[i] = nA.num[i] + nB.num[i];
						for (int j = bLen; j < aLen; j++)
								ret.num[j] = nA.num[j];						
				} else {
						for (int i = 0; i < aLen; i++)
								ret.num[i] = nA.num[i] + nB.num[i];
						for (int j = aLen; j < bLen; j++)
								ret.num[j] = nB.num[j];
				}
				for(int k = 0; k < cLen - 1; k++) {
						if (ret.num[k] >= 10) {
								ret.num[k] -= 10;
								ret.num[k + 1]++;
						}
				}
				return ret;
		}
		public static LongNum sub(LongNum nA, LongNum nB) {
				int aLen = nA.num.length;
				int bLen = nB.num.length;
				int cLen = Math.max(aLen, bLen);
				LongNum ret = new LongNum();
				ret.num =	new int[cLen];
				if (aLen > bLen) {
						for (int i = 0; i < bLen; i++)
								ret.num[i] = nA.num[i] - nB.num[i];
						for (int j = bLen; j < aLen; j++)
								ret.num[j] = nA.num[j];
				} else if (aLen < bLen) {
						for (int i = 0; i < aLen; i++)
								ret.num[i] = nB.num[i] - nA.num[i];
						for (int j = aLen; j < bLen; j++)
								ret.num[j] = nB.num[j];
				} else {
						if (isLonger(nA, nB)) {
								for (int i = 0; i < cLen; i++) {
										ret.num[i] = nA.num[i] - nB.num[i];
								}
						} else {
								for (int i = 0; i < cLen; i++) {
										ret.num[i] = nB.num[i] - nA.num[i];
								}
						}
				}
				for (int k = 0; k < cLen - 1; k++) {
						if (ret.num[k] < 0) {
								ret.num[k] += 10;
								ret.num[k + 1]--;
						}
				}
				return ret;
		}
		public static boolean isLonger(LongNum nA, LongNum nB) {
				boolean f = true;
				for(int i = nA.num.length - 1; i >= 0; i--) {
						if(nA.num[i] < nB.num[i]) {
								f = false;
								break;
						} else if(nA.num[i] > nB.num[i]) {
								break;
						} else
								continue;
				}
				return f;
		}
		public void printRet() {
				if(num[num.length - 1] != 0)
						System.out.print(num[num.length - 1]);
				for(int i = num.length - 2; i >= 0; i--)
						System.out.println();
		}
		public static void main(String[] args) {
				LongNum nA = new LongNum(500);
				LongNum nB = new LongNum(600);
				nA.printRet();
				nB.printRet();
				add(nA, nB).printRet();
				sub(nA, nB).printRet();
		}
}

import java.util.*;
public class StringOps {
		public static void main(String[] args) {
				String s = new String("this is an example to illustrate how to use string tokenizer");
				StringTokenizer st = new StringTokenizer(s);
				System.out.println(st.countTokens());
				while(st.hasMoreElements())
						System.out.println(st.nextToken());
		}
}

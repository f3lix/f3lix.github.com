public class Stack {      
		Object[] data;
		int maxSize;  
		int top;      
		public Stack(int maxSize) {      
				this.maxSize = maxSize;      
				data = new Object[maxSize];      
				top = -1;      
		}      
		public int getSize() {
				return maxSize;
		}
		public int getElementCount() {
				return top;
		}
		public boolean isEmpty() {
				return top == -1;
		}
		public boolean isFull() {
				return top + 1 == maxSize;
		}
		public boolean push(Object data) {      
				if(isFull()) {      
						System.out.println("stack is full");      
						return false;      
				}      
				this.data[++top] = data;      
				return true;      
		}      
		public Object pop() {      
				if(isEmpty()) {      
						System.out.println("stack is empty");      
				}      
				return this.data[top--];      
		}      
		public Object peek() {
				return this.data[getElementCount()];  
		}
		public static void main(String[] args) {      
				Stack stack = new Stack(50);      
				for(int i = 0; i < 50; i++)
						stack.push(new String(Integer.toString(i)));
				/*
				stack.push(new String("1"));      
				stack.push(new String("2"));      
				stack.push(new String("3"));      
				stack.push(new String("4"));      
				stack.push(new String("5"));
				*/
				// stack.push(new String("6"));	
				System.out.println("what's on top: " + stack.peek()); 
				while(stack.top >= 0) {      
						System.out.print(stack.pop() + " ");      
				}
				System.out.println();
		}
}

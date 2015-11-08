package com.kohlschutter.boilerpipe.demo;

public class StackTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NdContentStack contentStack=new NdContentStack();
		if(contentStack.isEmpty())
		{
			System.out.println("stack is Empty");
		}
		contentStack.push("sridhar");
		contentStack.push("tags");
		while(!contentStack.isEmpty())
		{
			System.out.println(contentStack.pop());
			}
	}

}

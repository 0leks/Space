package one;

import java.awt.Color;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MAX_VALUE+1);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(Integer.MIN_VALUE-1);
		for(int a=0; a<20; a++) {
			System.out.println((int)(Math.random()*6.5)*40+", "+(int)(Math.random()*13)*10+", "+(int)(Math.random()*13)*10);
		}
		System.out.println(Color.red.getRed());
	}

}

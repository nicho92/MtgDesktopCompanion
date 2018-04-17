package test;

import java.util.Scanner;

public class TextAnalyzer {

	public static void main(String[] args) {
		analyse("{T}: Add {C} to your mana pool.\\n{T}, Sacrifice Quicksand: Target attacking creature without flying gets -1/-2 until end of turn.");
		
	}
		
		
		
		

	private static void analyse(String text) {
		try(Scanner scanner = new Scanner(text))
		{
			System.out.println(scanner.next());
		}
		
	}

}

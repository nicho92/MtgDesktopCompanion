package unit.cardanalyse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.magic.tools.CardsPatterns;

public class PatternTest {

	
	private CardsPatterns t = CardsPatterns.ADD_MANA;
	
	
	@Test
	public void testPattern()
	{
		System.out.println("PATTERN : " + t +"\n" );
		test("Add {R}");
		test("Add {R} or {B}");
		test("{T}, Sacrifice Ancient Spring: Add {G}{B}.");
		test("Add {U}, {G}, or {B}.");
		test("{T}: Add {U}{U}, {U}{R}, or {R}{R}.");
		test("Add one mana of any color");
		test("Add three mana of any one color");
		test("Whenever enchanted land is tapped for mana, (its) controller adds an additional {G}.");
		
	}
	
	
	
	public void test(String s)
	{
		System.out.println("############### TESTING : " + s );
		Pattern p = Pattern.compile(t.getPattern());
		Matcher m = p.matcher(s);
		while(m.find())
			System.out.println(m.group());
	
	}

}
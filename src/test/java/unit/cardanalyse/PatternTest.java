package unit.cardanalyse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.magic.tools.CardsPatterns;

public class PatternTest {

	
	private CardsPatterns t ;
	
	
	@Test
	public void testPatternMana()
	{
		t = CardsPatterns.ADD_MANA;
		test("Add {R}");
		test("Add {R} or {B}");
		test("{T}, Sacrifice Ancient Spring: Add {G}{B}.");
		test("Add {U}, {G}, or {B}.");
		test("{T}: Add {U}{U}, {U}{R}, or {R}{R}.");
		test("Add one mana of any color");
		test("Add three mana of any one color");
		test("Whenever enchanted land is tapped for mana, (its) controller adds an additional {G}.");
	}
	
	
	@Test
	public void testTokens()
	{
		t = CardsPatterns.CREATE_TOKEN;
		test("Whenever an enchantment enters the battlefield under your control, create a 2/2 white Cat creature token. If that enchantment is an Aura, you may attach it to the token.");
		test("−7: You get an emblem with \"At the beginning of your end step, create three 1/1 white Cat creature tokens with lifelink.\"");
		test("Create a 3/3 green Centaur creature token.");
		test("Fabricate 2 (When this creature enters the battlefield, put two +1/+1 counters on it or create two 1/1 colorless Servo artifact creature tokens.)");
		test("When Byway Courier dies, investigate. (Create a colorless Clue artifact token with \"2, Sacrifice this artifact: Draw a card.\")");
	}
	
	public void test(String s)
	{
		System.out.println("############### TESTING : " + t.name() +"-->"+ s );
		Pattern p = Pattern.compile(t.getPattern());
		Matcher m = p.matcher(s);
		while(m.find())
			System.out.println(m.group());
	
	}

}
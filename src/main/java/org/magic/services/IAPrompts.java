package org.magic.services;

import org.magic.api.beans.MTGDeck;

public class IAPrompts {
    
    
    private static final String DECKLIST = "[DECKLIST]";

    
    private IAPrompts()
    {
	//no instantiation
    }

    public static String combos(MTGDeck d)
    {
	return  """
			Analyze this MTG deck for overlooked card interactions, synergistic packages, and potential combos.

                        For each finding:
                        
                        - Name the interacting cards.
                        - Explain the interaction step by step.
                        - Distinguish deterministic combos from conditional synergies.
                        - Identify the mana and setup requirements.
                        - Explain vulnerability to disruption.
                        - Recommend additions that strengthen the interaction.
                        
                        Verify rules interactions carefully. Do not present an interaction as a legal combo unless the card text and relevant rules support it.
                        
                        Decklist:
                        [DECKLIST]
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    public static String synergy(MTGDeck d)
    {
	return  """
			Improve the internal synergy of this MTG deck while preserving its original theme and identity.

                        Identify:
                        
                        - The core engine and key synergistic interactions.
                        - Cards that support the strategy indirectly.
                        - Missing pieces that would make existing cards more effective.
                        - Redundant effects and cards that dilute the game plan.
                        - Opportunities to increase redundancy without sacrificing flexibility.
                        - Alternative win conditions that naturally fit the strategy.
                        
                        Recommend exact card swaps and explain the interactions they create.
                        
                        Do not turn the deck into a generic good-stuff list. Preserve the theme, playstyle, and cards that are central to my enjoyment.
                        
                        Decklist:
                        [DECKLIST]
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    
    public static String competitive(MTGDeck d)
    {
	return  """
			Act as a competitive MTG player and tournament deck-building specialist.

                        Evaluate this deck for competitive performance in its intended format.
                        
                        Identify:
                        
                        - The deck's strongest and weakest matchups.
                        - Its speed and consistency compared with the expected metagame.
                        - Inefficient cards and weak interaction.
                        - Vulnerabilities to removal, counterspells, graveyard hate, and other common disruption.
                        - The most impactful upgrades within my budget.
                        
                        Provide a prioritized upgrade plan with exact card swaps. Explain how each change improves the deck against realistic opponents.
                        
                        Do not invent tournament statistics or claim that a card is currently popular without reliable evidence.
                        
                        Decklist:
                        [DECKLIST]
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    
    
    public static String manabase(MTGDeck d)
    {
	return  """
			Analyze and optimize the mana base of this Magic: The Gathering deck.

                        Calculate and evaluate:
                        
                        - Total land count and mana curve.
                        - Colored mana requirements for each spell.
                        - Sources of each color needed at different stages of the game.
                        - Probability of making land drops and casting key spells on curve, where sufficient card data is available.
                        - Risks of tapped lands, color screw, and excessive mana.
                        - Opportunities to improve ramp and fixing.
                        
                        Recommend specific land additions, removals, and replacements. Explain the trade-offs between budget, speed, and reliability.
                        
                        Decklist:
                        [DECKLIST]
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    
    
    public static String replacements(MTGDeck d)
    {
	return  """
			Optimize this MTG deck by identifying cards that should be removed and replaced.

                        For every proposed change, provide:
                        
                         - Card to remove.
                        - Recommended replacement.
                        - Strategic reason for the swap.
                        - Synergies gained or lost.
                        - Estimated improvement to consistency, power, or matchup performance.
                        - Budget alternatives, if applicable.
                        
                        Preserve the deck's core identity and primary win condition.
                        
                        Return a ranked table of changes, from highest to lowest priority. Do not suggest cards that are illegal in the selected format or exceed my budget.
                        
                        Decklist:
                        [DECKLIST]
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    
    
    public static String evaluation(MTGDeck d)
    {
	return  
			"""
			Act as an expert Magic: The Gathering deck builder and analyst.
			Analyze the following decklist in detail:
                        [DECKLIST]

                        Evaluate:
                        
                        - Overall strategy, win conditions, and game plan.
                        - Mana curve, land count, and color distribution.
                        - Ramp, card draw, removal, interaction, and protection.
                        - Synergies, redundancies, and cards that do not contribute to the strategy.
                        - Matchup weaknesses and vulnerability to disruption.
                        - Consistency and ability to execute the game plan.
                        
                        Rank the 10 most important improvements by impact. Explain the reasoning behind each recommendation and distinguish essential changes from optional upgrades.
                        
                        Do not recommend changes simply because a card is individually powerful. Prioritize synergy and the deck's actual game plan.
                        
			""".trim().replace(DECKLIST, d.toStringList());
    }
    
    
}

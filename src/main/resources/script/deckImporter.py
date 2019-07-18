from org.magic.services import MTGDeckManager

provider.init();
snifferName = "MTGoldFish"
selectedSniffer = None
for sniffer in sniffers :
	if sniffer.getName() == snifferName :
		selectedSniffer = sniffer

print "Selected Sniffer : " + str(selectedSniffer)

manager = MTGDeckManager()

for rDeck in selectedSniffer.getDeckList() : 
	try :
		manager.saveDeck(selectedSniffer.getDeck(rDeck))
	except : print "Error for " + rDeck.getName();
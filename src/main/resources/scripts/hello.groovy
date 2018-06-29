import org.magic.services.*;

MTGControler.getInstance().getEnabledCardsProvider().init();
MTGControler.getInstance().getEnabledCardsProvider().searchCardByName("Reflecting pool",null,false);
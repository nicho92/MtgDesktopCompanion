import org.magic.services.*;
import org.magic.api.interfaces.*;
import org.magic.api.exports.impl.*;
import org.magic.api.beans.*;
import java.io.File;

MTGCardsExport importer = controler.getPlugin("XMage",MTGCardsExport.class);
MTGCardsExport exporter = controler.getPlugin("Forge",MTGCardsExport.class);

MagicDeck deck = importer.importDeckFromFile(new File("D:\\Téléchargements\\Sliver.Overlord--Commander--XMage.dck.txt"));

exporter.exportDeck(deck,new File("D:\\Téléchargements\\Sliver.Overlord--Commander--Forge.dck"));
import org.magic.services.*;
import org.magic.api.interfaces.*;
import org.magic.api.exports.impl.*;
import org.magic.api.beans.*;

MTGCardsExport mkm = controler.getPlugin("MagicCardMarket",MTGCardsExport.class);
MTGCardsExport woocommerce = controler.getPlugin("WooCommerce",MTGCardsExport.class);

List<MagicCardStock> listMkm = mkm.importStockFromFile(null);

woocommerce.exportStock(listMkm,null);
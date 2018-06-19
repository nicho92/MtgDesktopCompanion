import org.magic.api.providers.impl.*
import org.magic.api.interfaces.*
import com.jayway.jsonpath.spi.cache.*;

CacheProvider.setCache(new LRUCache(400));
MTGCardsProvider prov = new MtgjsonProvider();
prov.init();
prov.searchCardByName("Reflecting pool",null,false);

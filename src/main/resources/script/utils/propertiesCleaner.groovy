
import org.magic.services.PluginRegistry;


PluginRegistry.inst().listPlugins().forEach(mtg->{
			out.println("*** Cleaning "+mtg.getName() +" " + mtg.getType());
			mtg.getProperties().keySet().removeIf(o->!mtg.getDefaultAttributes().containsKey(o));
			mtg.save();
			
		});
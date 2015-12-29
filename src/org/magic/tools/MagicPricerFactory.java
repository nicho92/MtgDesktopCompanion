package org.magic.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.pricers.impl.ChannelFireballPricer;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.api.pricers.impl.MagicTradersPricer;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;

public class MagicPricerFactory {

	private static MagicPricerFactory inst;
	private ArrayList<MagicPricesProvider> classes;
	
	public static void main(String[] args) {
		System.out.println(MagicPricerFactory.getInstance().getListPricers());
	}
	
	 
	public static MagicPricerFactory getInstance()
	{
		if(inst == null)
			inst = new MagicPricerFactory();
		
		return inst;
	}
	
	private MagicPricerFactory()
	{
		try {
			//init("org.magic.api.pricers.impl");
			classes = new ArrayList<MagicPricesProvider>();
			classes.add(new ChannelFireballPricer() );
			classes.add(new EbayPricer() );
			classes.add(new MagicTradersPricer() );
			classes.add(new MagicVillePricer() );
			classes.add(new TCGPlayerPricer() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<MagicPricesProvider> getListPricers()
	{
		  return classes;
	}
	
	/*private void init(String packageName) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<MagicPricesProvider> classes = new ArrayList<MagicPricesProvider>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
       this.classes=classes;
    }
	
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private List<MagicPricesProvider> findClasses(File directory, String packageName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)).newInstance());
            }
        }
        return classes;
    }
	
}

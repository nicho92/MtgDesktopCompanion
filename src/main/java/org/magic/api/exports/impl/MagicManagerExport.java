package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

import com.google.common.collect.Lists;

public class MagicManagerExport extends AbstractFormattedFileCardExport {

	
	private static final String COLUMNS = "Card Name,Set Code,Collector Number,Language,Foil,Count";

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var list = new ArrayList<MTGCardStock>();
		
		
		matches(content, true, aliases.getRegexFor(this,"default")).forEach(m->{
			var stock = MTGControler.getInstance().getDefaultStock();
			var mc = parseMatcherWithGroup(m, 3, 2,false, FORMAT_SEARCH.ID, FORMAT_SEARCH.NUMBER);
			if(mc!=null){
				stock.setProduct(mc);
				stock.setFoil(m.group(5).equalsIgnoreCase("True"));
				stock.setQte(Integer.parseInt(m.group(6)));
				stock.setLanguage(m.group(4));
				list.add(stock);
				notify(mc);
			}
		});
		
		return list;
	}
	
	Integer number=0;
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		Lists.partition(stock, 2000).forEach(list->{
			var temp = new StringBuilder(COLUMNS);
			temp.append(System.lineSeparator());
			
			list.forEach(st->{
				temp.append(commated(st.getProduct().getName())).append(getSeparator());
				temp.append(aliases.getSetIdFor(this,st.getProduct().getEdition()).toLowerCase()).append(getSeparator());
				temp.append(st.getProduct().getNumber()).append(getSeparator());
				temp.append(st.getLanguage().substring(0, 2)).append(getSeparator());
				temp.append(st.isFoil()?"True":"False").append(getSeparator());
				temp.append(st.getQte());
				notify(st.getProduct());
				temp.append(System.lineSeparator());
			});
			
			
			try {
					var ret = f.renameTo(new File(f.getAbsolutePath()+"-"+(number++)));
					if(ret)
						FileTools.saveFile(f, temp.toString());
					
			} catch (IOException e) {
					logger.error(e);
			}
			finally {
				number=0;		
			}
		});
		
	}
	
	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}

	@Override
	protected String getSeparator() {
		return ",";
	}
	
	@Override
	public String getName() {
		return "MagicManager";
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}


}

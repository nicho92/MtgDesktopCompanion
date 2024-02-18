package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardNames;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.POMReader;

import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class XMLExport extends AbstractCardExport {

	private XStream xstream;

	@Override
	public String getFileExtension() {
		return ".xml";
	}

	public XMLExport() {
		xstream = new XStream(new StaxDriver());
		xstream.alias("deck", MTGDeck.class);
		xstream.alias("rarity", EnumRarity.class);
		xstream.alias("color", EnumColors.class);
		xstream.alias("stock",MTGCardStock.class);
		xstream.alias("set", MTGEdition.class);
		xstream.alias("foreigneData", MTGCardNames.class);
		xstream.alias("legality", MTGFormat.class);
		xstream .addPermission(AnyTypePermission.ANY); 
		xstream.registerConverter(new NamedMapConverter(xstream.getMapper(), "entry", "card", MTGCard.class, "qty", Integer.class));
	
		xstream.registerConverter(new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type.equals(LinkedTreeMap.class);
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				return reader.getValue();
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				LinkedTreeMap<Object, Object> map= (LinkedTreeMap)source;
				for (var entry : map.entrySet()) {
		            writer.startNode(entry.getKey().toString());
		            writer.setValue(entry.getValue().toString());
		            writer.endNode();
		        }


			}
		});

	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {

		String xml = xstream.toXML(deck);
		FileTools.saveFile(dest, xml);

	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		return (MTGDeck)xstream.fromXML(f);
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		String xml = xstream.toXML(stock);
		FileTools.saveFile(f, xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		return (List<MTGCardStock>)xstream.fromXML(content);
	}


	@Override
	public String getName() {
		return "XML";
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(XStream.class, "/META-INF/maven/com.thoughtworks.xstream/xstream/pom.properties");
	}

}

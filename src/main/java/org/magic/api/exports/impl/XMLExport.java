package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.tools.FileTools;
import org.magic.tools.POMReader;

import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class XMLExport extends AbstractCardExport {

	private XStream xstream;

	@Override
	public String getFileExtension() {
		return ".xml";
	}

	public XMLExport() {
		xstream = new XStream(new StaxDriver());
		xstream.alias("deck", MagicDeck.class);
		xstream.alias("rarity", MTGRarity.class);
		xstream.alias("color", MTGColor.class);
		xstream.alias("stock",MagicCardStock.class);
		xstream.alias("set", MagicEdition.class);
		xstream.alias("foreigneData", MagicCardNames.class);
		xstream.alias("legality", MagicFormat.class);
		xstream.registerConverter(new NamedMapConverter(xstream.getMapper(), "entry", "card", MagicCard.class, "qty", Integer.class));
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
	public void exportDeck(MagicDeck deck, File dest) throws IOException {

		String xml = xstream.toXML(deck);
		FileTools.saveFile(dest, xml);

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		return (MagicDeck)xstream.fromXML(f);
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		String xml = xstream.toXML(stock);
		FileTools.saveFile(f, xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		return (List<MagicCardStock>)xstream.fromXML(content);
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

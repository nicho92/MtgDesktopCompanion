package org.magic.composer.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.exports.impl.JsonExport;
import org.magic.composer.layer.Layer;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;


public class ComposerUtils {

    protected transient static Logger logger = MTGLogger.getLogger(ComposerUtils.class);
    private static JsonExport exporter = new JsonExport();
    
    
    public static void save(File f , List<Layer> layers) throws IOException
    {
	var s = exporter .toJson(layers);
	    FileTools.saveFile(f, s);
    }

    public static List<Layer> open(File selectedFile) throws IOException {

		var s = FileTools.readFile(selectedFile);
		var layers= exporter.fromJsonList(s, Layer.class);
		layers.forEach(Layer::reload);
		return layers;
    }
    
}

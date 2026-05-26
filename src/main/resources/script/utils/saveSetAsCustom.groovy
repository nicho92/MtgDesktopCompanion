import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider; 

//SET COPIER
//Duplicate a full set in card editor



var id="4ED";
var edition = provider.getSetById(id);
var cards = provider.searchCardByEdition(edition);
var editor = new PrivateMTGSetProvider();

var picture = new ScryFallPicturesProvider();

editor.saveCustomSet(edition);

cards.forEach(c->{

	c.setUrl(picture.generateLink(c,true).toString());

	////cleaning the unused datas
	c.getEditions().clear();
	c.getRulings().clear();
	c.getForeignNames().clear();

	c.getEditions().add(edition);
	editor.saveCustomCard(edition,c);

	
})


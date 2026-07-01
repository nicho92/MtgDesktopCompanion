import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider; 

/////////SET COPIER : Duplicate a set in card editor, set the edition code here

var id="USG";

///////////////////////////////////////////

var edition = provider.getSetById(id);
var cards = provider.searchCardByEdition(edition);
var editor = new PrivateMTGSetProvider();
var picture = new ScryFallPicturesProvider();


editor.saveCustomSet(edition);

cards.forEach(c->{

	c.setUrl(picture.generateUrl(c,true).toString());

	////cleaning the unused datas
	c.getEditions().clear();
	c.getRulings().clear();
	c.getForeignNames().clear();
	c.getLegalities().clear();
	
	c.getEditions().add(edition);
	editor.saveCustomCard(edition,c);

	
})


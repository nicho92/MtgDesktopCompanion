

var source = new FileCustomManager();
var dest = new DAOCustomManager();
		
		
		
source.listCustomSets().forEach(ed->{
	try {
		dest.saveCustomSet(ed);
		
		source.listCustomsCards(ed).forEach(mc->{
			try {
				dest.saveCustomCard(ed,mc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
});
package org.magic.game.network.actions;

public class ReponseAction extends AbstractGamingAction {

	public static enum CHOICE {YES,NO};
	
	private CHOICE reponse;
	private RequestPlayAction request;
	
	
	public ReponseAction(RequestPlayAction pa, CHOICE c) {
		this.request=pa;
		this.reponse=c;
		setAct(ACTIONS.RESPONSE);
	}


	public CHOICE getReponse() {
		return reponse;
	}


	public void setReponse(CHOICE reponse) {
		this.reponse = reponse;
	}


	public RequestPlayAction getRequest() {
		return request;
	}
	
	public String toString(){
		return getRequest().getP2() + " answer to "+ getRequest().getP1() + ": "+ reponse;
	}
}

package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.interfaces.extra.MTGSerializable;
import org.magic.services.tools.UITools;


public class MTGEdition implements MTGSerializable, Comparable<MTGEdition> {
	public static final long serialVersionUID = 1L;

	private String set;


	private String url;
	private String id;
	private String releaseDate;
	private String type;
	private int cardCount;
	private int cardCountOfficial;
	private int cardCountPhysical;
	private String block;
	private boolean onlineOnly;
	private Integer mkmid;
	private String mkmname;
	private String parentCode;
	private boolean foilOnly;
	private String keyRuneCode;
	private int tcgplayerGroupId;
	private boolean preview;
	private List<EnumExtra> booster;

	private boolean foreignOnly;
	
	@Override
	public String getStoreId() {
		return getId();
	}

	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	
	public void setCardCountPhysical(int cardCountPhysical) {
		this.cardCountPhysical = cardCountPhysical;
	}
	
	public int getCardCountPhysical() {
		return cardCountPhysical;
	}
	
	public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	public int getCardCountOfficial() {
		return cardCountOfficial;
	}

	public void setCardCountOfficial(int cardCountOfficial) {
		this.cardCountOfficial = cardCountOfficial;
	}

	public boolean isFoilOnly() {
		return foilOnly;
	}

	public void setFoilOnly(boolean foilOnly) {
		this.foilOnly = foilOnly;
	}

	public Integer getMkmid() {
		return mkmid;
	}

	public void setMkmid(Integer mkmid) {
		this.mkmid = mkmid;
	}

	public String getMkmName() {
		return mkmname;
	}

	public void setMkmName(String mkmname) {
		this.mkmname = mkmname;
	}


	public boolean isOnlineOnly() {
		return onlineOnly;
	}

	public void setOnlineOnly(boolean onlineOnly) {
		this.onlineOnly = onlineOnly;
	}

	public MTGEdition(String idMe)
	{
		super();
		setId(idMe);
	}

	public MTGEdition(String idMe,String name)
	{
		super();
		setId(idMe);
		setSet(name);

	}

	public MTGEdition() {
		booster = new ArrayList<>();
	}

	public List<EnumExtra> getBooster() {
		return booster;
	}

	public String getReleaseDate() {
		return releaseDate;
	}
	
	public Date getReleaseAsDate() {
		return UITools.parseDate(releaseDate, "yyyy-MM-dd");
	}
	
	

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof MTGEdition ed)
			return getId().equalsIgnoreCase(ed.getId());
		
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getSet();
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int compare(MTGEdition o1, MTGEdition o2) {

		if(o1==null || o2==null || o1.getSet()==null || o2.getSet()==null)
			return -1;

		return o1.getSet().compareTo(o2.getSet());
	}

	@Override
	public int hashCode() {
		if (set != null)
			return set.hashCode();

		return -1;
	}

	@Override
	public int compareTo(MTGEdition o) {
		return compare(this, o);
	}

	public void setKeyRuneCode(String r) {
		keyRuneCode=r;

	}

	public String getKeyRuneCode() {
		return keyRuneCode;
	}

	public int getTcgplayerGroupId() {
		return tcgplayerGroupId;
	}

	public void setTcgplayerGroupId(int tcgplayerGroupId) {
		this.tcgplayerGroupId = tcgplayerGroupId;
	}

	public void setForeignOnly(boolean fo) {
		this.foreignOnly=fo;
		
	}

	public boolean isForeignOnly() {
		return foreignOnly;
	}
	

}

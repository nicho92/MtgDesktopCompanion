package org.magic.api.beans;

import java.io.Serializable;

import org.magic.api.interfaces.MTGNewsProvider;

public class MagicNews implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String categorie;
	private String url;
	private transient MTGNewsProvider provider;

	public MagicNews() {
		id = -1;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setProvider(MTGNewsProvider mtgNewsProvider) {
		provider = mtgNewsProvider;
	}

	public MTGNewsProvider getProvider() {
		return provider;
	}

}

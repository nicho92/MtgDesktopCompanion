package org.magic.api.interfaces;

import java.io.Serializable;

import org.magic.api.beans.MagicEdition;

public interface MTGProduct extends Serializable {

	String getProductId();

	void setProductId(String id);

	String getUrl();

	void setUrl(String url);

	MagicEdition getEdition();

	void setEdition(MagicEdition edition);

	String getName();

	void setName(String name);

}
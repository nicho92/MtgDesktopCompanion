package org.magic.api.interfaces;

import java.io.IOException;

public interface MTGAssistant {
	public String ask(String prompt) throws IOException;
}

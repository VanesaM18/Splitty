package client.utils.language.implementations;

import client.utils.language.LanguageTemplate;

public class Afrikaans extends LanguageTemplate {

    /**
     * gets the text content for Afrikaans.
     * @return text content for Afrikaans.
     */
    @Override
    public String getText() {
        return "Afrikaans";
    }

    @Override
    protected String getLanguage() {
        return "af";
    }

    @Override
    protected String getCountry() {
        return "ZA";
    }
}

package client.utils.language.implementations;

import client.utils.language.LanguageTemplate;

public class Nederlands extends LanguageTemplate {

    /**
     * gets the text content for Dutch.
     * @return text content.
     */
    @Override
    public String getText() {
        return "Nederlands";
    }

    @Override
    protected String getLanguage() {
        return "nl";
    }

    @Override
    protected String getCountry() {
        return "NL";
    }
}

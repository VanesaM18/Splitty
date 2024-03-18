package client.utils.language.implementations;

import client.utils.language.LanguageTemplate;

public class English extends LanguageTemplate {

    /**
     * getrieves the text content for English.
     * @return text content for English.
     */
    @Override
    public String getText() {
        return "English";
    }

    @Override
    protected String getLanguage() {
        return "en";
    }
    @Override
    protected String getCountry() {
        return "EN";
    }
}

package client.utils.language;

public class CustomLanguage extends LanguageTemplate {
    private final String text;
    private final  String language;
    private final String country;

    /**
     * constructs a CustomLanguage object with the specified text, language, and country.
     * @param text     language title
     * @param language language code.
     * @param country  country code.
     */
    public CustomLanguage(String text, String language, String country) {
        this.text = text;
        this.language = language;
        this.country = country;
    }

    /**
     * gets the text content in the custom language.
     * @return text content.
     */
    @Override
    public String getText() {
        return this.text;
    }

    @Override
    protected String getLanguage() {
        return this.language;
    }

    @Override
    protected String getCountry() {
        return this.country;
    }
}

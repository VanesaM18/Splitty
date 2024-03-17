package client.utils.language;

public class CustomLanguage extends LanguageTemplate {
    private final String text;
    private final  String language;
    private final String country;

    public CustomLanguage(String text, String language, String country) {
        this.text = text;
        this.language = language;
        this.country = country;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    protected String getLanguage() {
        return null;
    }

    @Override
    protected String getCountry() {
        return null;
    }
}

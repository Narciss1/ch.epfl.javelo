package ch.epfl.javelo.parameters;


/**
 * Represents the translation of the sentences used in the interface in English,
 * French and German.
 * @author Sadgal Lina (342075)
 */
public enum Language {

    ENGLISH("Language", "No route nearby",
            "Map background", "File"),
    FRENCH("Langue", "Aucune route à proximité",
            "Fond de carte", "Fichier"),
    GERMAN("Sprache", "Keine Straße in der Nähe",
            "Kartenhintergrund", "Datei");

    private final String languageTranslation;
    private final String routeMessage;
    private final String backgroundMessage;
    private final String file;

    Language(String language, String routeMessage, String backgroundMessage, String file) {
        this.languageTranslation = language;
        this.routeMessage = routeMessage ;
        this.backgroundMessage = backgroundMessage;
        this.file = file;

    }

    public String getRouteMessage() {
        return  this.routeMessage;
    }

    public String getBackgroundMessage() {
        return this.backgroundMessage;
    }

    public String getFile() {
        return file;
    }

    public String getLanguageTranslation() {
        return languageTranslation;
    }

}
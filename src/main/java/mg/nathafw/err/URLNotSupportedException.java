package mg.nathafw.err;

import java.util.HashMap;
import mg.nathafw.mapping.URLControllerMap;
import mg.nathafw.mapping.URLKey;

public class URLNotSupportedException extends Exception {
    private URLKey url;
    private HashMap<URLKey, URLControllerMap> availableUrls;

    public URLNotSupportedException(URLKey url, HashMap<URLKey, URLControllerMap> availableUrls) {
        super("URL " + url + " n'est pas supportée. URLs disponibles : " + availableUrls.keySet());
        this.url = url;
        this.availableUrls = availableUrls;
    }

    public URLKey getUrl() {
        return url;
    }

    public HashMap<URLKey, URLControllerMap> getAvailableUrls() {
        return availableUrls;
    }
}

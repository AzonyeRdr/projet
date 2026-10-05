package mg.nathafw.mapping;

public class URLKey {
    private String urlString;
    private HTTPMethod methodHttp;
    

    public URLKey(String urlString, HTTPMethod methodHttp) {
        this.urlString = urlString;
        this.methodHttp = methodHttp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((urlString == null) ? 0 : urlString.hashCode());
        result = prime * result + ((methodHttp == null) ? 0 : methodHttp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        URLKey other = (URLKey) obj;
        if (urlString == null) {
            if (other.urlString != null)
                return false;
        } else if (!urlString.equals(other.urlString))
            return false;
        if (methodHttp != other.methodHttp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "URLKey [urlString=" + urlString + ", methodHttp=" + methodHttp + "]";
    }

    public String getUrlString() {
        return urlString;
    }

    public HTTPMethod getMethodHttp() {
        return methodHttp;
    }
}

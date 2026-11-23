package mg.nathafw.mapping;

public class FileUpload {
    String nom;
    Byte[] contenu;
    
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Byte[] getContenu() {
        return contenu;
    }
    public void setContenu(Byte[] contenu) {
        this.contenu = contenu;
    }
    public FileUpload(String nom, Byte[] contenu) {
        this.nom = nom;
        this.contenu = contenu;
    }
}

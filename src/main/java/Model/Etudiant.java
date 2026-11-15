package Model;

public class Etudiant {
    private String idEtudiant;
    private String nom;
    private String prenom;

    public Etudiant(String idEtudiant, String nom, String prenom) {
        this.idEtudiant = idEtudiant;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }

}

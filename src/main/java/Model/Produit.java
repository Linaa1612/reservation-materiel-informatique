package Model;

import java.math.BigDecimal;

public class Produit {

    private String idProduit;
    private String libelle;
    private BigDecimal tarifJournalier;
    private BigDecimal caution;

    public Produit(String idProduit, String libelle,
                   BigDecimal tarifJournalier, BigDecimal caution) {

        if (tarifJournalier == null || tarifJournalier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tarif journalier invalide");
        }

        if (caution == null || caution.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La caution doit être positive ou nulle.");
        }

        this.idProduit = idProduit;
        this.libelle = libelle;
        this.tarifJournalier = tarifJournalier;
        this.caution = caution;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public String getLibelle() {
        return libelle;
    }

    public BigDecimal getTarifJournalier() {
        return tarifJournalier;
    }

    public BigDecimal getCaution() {
        return caution;
    }
}

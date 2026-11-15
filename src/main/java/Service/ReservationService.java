
package Service;

import Model.Commande;
import Model.Etudiant;
import Model.Produit;
import Exception.ReservationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationService {

    private List<Etudiant> etudiants = new ArrayList<>();
    private List<Produit> produits = new ArrayList<>();
    private List<Commande> commandes = new ArrayList<>();

    private static final int DUREE_MAXIMALE = 7;

    // RG02 : vérifier la date de début
    private void verifierDateDebut(LocalDate dateDebut) {
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new ReservationException(
                    "Date de début invalide"
            );
        }
    }

    // RG02 : vérifier la cohérence des dates
    private void verifierDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateFin.isBefore(dateDebut)) {
            throw new ReservationException(
                    "Date de fin antérieure à la date de début"
            );
        }
    }

    // RG08 : vérifier la disponibilité du produit
    private void verifierDisponibilite(
            String idProduit,
            LocalDate dateDebut,
            LocalDate dateFin) {

        for (Commande commande : commandes) {

            if (commande.getIdProduit().equals(idProduit)) {

                LocalDate debutExistant = commande.getDateDebut();
                LocalDate finExistante = commande.getDateFin();

                boolean chevauchement =
                        !dateFin.isBefore(debutExistant)
                                && !dateDebut.isAfter(finExistante);

                if (chevauchement) {
                    throw new ReservationException(
                            "Produit indisponible"
                    );
                }
            }
        }
    }

    // Ajouter un étudiant
    public void ajouterEtudiant(Etudiant etudiant) {
        etudiants.add(etudiant);
    }

    // Ajouter un produit
    public void ajouterProduit(Produit produit) {
        produits.add(produit);
    }

    // Calculer la durée de réservation
    public int calculerDuree(
            LocalDate dateDebut,
            LocalDate dateFin) {

        return (int) ChronoUnit.DAYS.between(
                dateDebut,
                dateFin
        ) + 1;
    }

    // Rechercher un étudiant
    public Etudiant rechercherEtudiant(String idEtudiant) {

        for (Etudiant etudiant : etudiants) {
            if (etudiant.getIdEtudiant().equals(idEtudiant)) {
                return etudiant;
            }
        }

        return null;
    }

    public BigDecimal calculerMontantTotal(Produit produit, int duree) {
        BigDecimal montantLocation =
                calculerMontantLocation(produit, duree);

        return montantLocation.add(produit.getCaution());
    }

    public String genererReference() {
        return UUID.randomUUID().toString();
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public BigDecimal calculerMontantLocation(Produit produit, int duree) {
        return produit.getTarifJournalier()
                .multiply(BigDecimal.valueOf(duree))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Rechercher un produit
    public Produit rechercherProduit(String idProduit) {

        for (Produit produit : produits) {
            if (produit.getIdProduit().equals(idProduit)) {
                return produit;
            }
        }

        return null;
    }

    // Ajouter une commande
    public void ajouterCommande(Commande commande) {
        commandes.add(commande);
    }

    // Réserver un produit
    public Commande reserverProduit(
            String idEtudiant,
            String idProduit,
            LocalDate dateDebut,
            LocalDate dateFin) {

        // RG01.2 : vérifier l'identifiant étudiant
        if (idEtudiant == null || idEtudiant.trim().isEmpty()) {
            throw new ReservationException(
                    "Identifiant étudiant obligatoire"
            );
        }

        // RG01.3 : vérifier l'identifiant produit
        if (idProduit == null || idProduit.trim().isEmpty()) {
            throw new ReservationException(
                    "Identifiant produit obligatoire"
            );
        }

        // RG01.4 : vérifier les dates
        if (dateDebut == null || dateFin == null) {
            throw new ReservationException(
                    "Les dates sont obligatoires"
            );
        }

        // RG02.1 : vérifier la date de début
        verifierDateDebut(dateDebut);

        // RG02.2 : vérifier les dates
        verifierDates(dateDebut, dateFin);

        // RG06 : vérifier la durée maximale
        int duree = calculerDuree(dateDebut, dateFin);

        if (duree > DUREE_MAXIMALE) {
            throw new ReservationException(
                    "Durée maximale de 7 jours dépassée"
            );
        }

        // RG05 : rechercher l'étudiant
        Etudiant etudiant = rechercherEtudiant(idEtudiant);

        if (etudiant == null) {
            throw new ReservationException(
                    "Étudiant introuvable"
            );
        }

        // RG03 : vérifier le quota maximal
        long nombreReservations = commandes.stream()
                .filter(c -> c.getIdEtudiant().equals(idEtudiant))
                .count();

        if (nombreReservations >= 2) {
            throw new ReservationException(
                    "Quota maximal de réservations atteint"
            );
        }

        // RG05 : rechercher le produit
        Produit produit = rechercherProduit(idProduit);

        if (produit == null) {
            throw new ReservationException(
                    "Produit introuvable"
            );
        }

        // RG08 : vérifier la disponibilité
        verifierDisponibilite(idProduit, dateDebut, dateFin);



        return null;
    }
}

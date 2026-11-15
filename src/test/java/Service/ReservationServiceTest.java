package Service;


import Model.Commande;
import Model.Etudiant;
import Exception.ReservationException;
import Model.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {
    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }

    @Test
    void testIdentifiantEtudiantObligatoire() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "",
                        "P001",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );

        assertEquals(
                "Identifiant étudiant obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testIdentifiantProduitObligatoire() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );

        assertEquals(
                "Identifiant produit obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testDateDebutObligatoire() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        null,
                        LocalDate.now()
                )
        );

        assertEquals(
                "Les dates sont obligatoires",
                exception.getMessage()
        );
    }

    @Test
    void testDateDebutAnterieureAujourdhui() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 11, 14),
                        LocalDate.of(2026, 11, 16)
                )
        );

        assertEquals(
                "Date de début invalide",
                exception.getMessage()
        );
    }


    @Test
    void testDateDebutEgaleAujourdhui() {

        LocalDate aujourdHui = LocalDate.now();

        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "kmd")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertDoesNotThrow(
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui
                )
        );
    }


    @Test
    void testDateFinAnterieureADateDebut() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 11, 18),
                        LocalDate.of(2026, 11, 16)
                )
        );

        assertEquals(
                "Date de fin antérieure à la date de début",
                exception.getMessage()
        );
    }



    @Test
    void testDateDebutEgaleDateFin() {
        LocalDate date = LocalDate.now();

        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "kmd")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertDoesNotThrow(() ->
                service.reserverProduit(
                        "E001",
                        "P001",
                        date,
                        date
                )
        );
    }


    @Test
    void testCalculerDureeUneJournee() {

        int duree = service.calculerDuree(
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 16)
        );

        assertEquals(1, duree);
    }


    @Test
    void testEtudiantIntrouvable() {

        // Arrange : ajouter uniquement le produit
        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        service.ajouterProduit(produit);

        // Act + Assert
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E999",
                        "P001",
                        LocalDate.of(2026, 11, 16),
                        LocalDate.of(2026, 11, 16)
                )
        );

        assertEquals(
                "Étudiant introuvable",
                exception.getMessage()
        );
    }


    @Test
    void testProduitIntrouvable() {

        // Arrange : ajouter uniquement l'étudiant
        Etudiant etudiant = new Etudiant(
                "E001",
                "Lina",
                "Ysf"
        );

        service.ajouterEtudiant(etudiant);

        // Act + Assert
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P999",
                        LocalDate.of(2026, 11, 16),
                        LocalDate.of(2026, 11, 16)
                )
        );

        assertEquals(
                "Produit introuvable",
                exception.getMessage()
        );
    }


    @Test
    void testRechercherEtudiantExistant() {

        Etudiant etudiant = new Etudiant(
                "E001", "Lina", "Ysf"
        );

        service.ajouterEtudiant(etudiant);

        Etudiant resultat = service.rechercherEtudiant("E001");

        assertNotNull(resultat);
        assertEquals("E001", resultat.getIdEtudiant());
    }

    @Test
    void testRechercherProduitExistant() {

        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        service.ajouterProduit(produit);

        Produit resultat = service.rechercherProduit("P001");

        assertNotNull(resultat);
        assertEquals("P001", resultat.getIdProduit());
    }


    @Test
    void testDureeMaximaleDepassee() {

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 11, 16),
                        LocalDate.of(2026, 11, 23)
                )
        );

        assertEquals(
                "Durée maximale de 7 jours dépassée",
                exception.getMessage()
        );
    }


    @Test
    void testDureeEgaleA7JoursAcceptee() {

        int duree = service.calculerDuree(
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 22)
        );

        assertEquals(7, duree);
    }


    @Test
    void testQuotaMaximalAtteint() {

        // Arrange : ajouter l'étudiant
        Etudiant etudiant = new Etudiant(
                "E001", "Lina", "Ysf"
        );

        service.ajouterEtudiant(etudiant);

        // Créer deux commandes pour E001
        Commande commande1 = new Commande(
                "REF001", "E001", "P001",
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 17),
                2,
                new BigDecimal("20.00"),
                new BigDecimal("100.00"),
                new BigDecimal("120.00")
        );

        Commande commande2 = new Commande(
                "REF002", "E001", "P002",
                LocalDate.of(2026, 11, 18),
                LocalDate.of(2026, 11, 19),
                2,
                new BigDecimal("20.00"),
                new BigDecimal("100.00"),
                new BigDecimal("120.00")
        );

        service.ajouterCommande(commande1);
        service.ajouterCommande(commande2);

        // Act + Assert
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P003",
                        LocalDate.of(2026, 11, 20),
                        LocalDate.of(2026, 11, 20)
                )
        );

        assertEquals(
                "Quota maximal de réservations atteint",
                exception.getMessage()
        );
    }


    @Test
    void testQuotaAvecAucuneCommande() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "Ysf")
        );

        service.ajouterProduit(
                new Produit(
                        "P001", "Ordinateur",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertDoesNotThrow(() ->
                service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
    }


    @Test
    void testProduitIndisponibleChevauchement() {

        // Arrange
        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "Ysf")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        Commande commandeExistante = new Commande(
                "REF001",
                "E002",
                "P001",
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 20),
                5,
                new BigDecimal("50.00"),
                new BigDecimal("100.00"),
                new BigDecimal("150.00")
        );

        service.ajouterCommande(commandeExistante);

        // Act + Assert
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 11, 18),
                        LocalDate.of(2026, 11, 22)
                )
        );

        assertEquals(
                "Produit indisponible",
                exception.getMessage()
        );
    }


    @Test
    void testProduitSansReservationExistante() {

        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "Ysf")
        );

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertDoesNotThrow(() ->
                service.reserverProduit(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 11, 16),
                        LocalDate.of(2026, 11, 18)
                )
        );
    }

    @Test
    void testTarifJournalierNegatif() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("-10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertEquals("Tarif journalier invalide", exception.getMessage());
    }

    @Test
    void testCautionNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("-100.00")
                )
        );

        assertEquals("La caution doit être positive ou nulle.", exception.getMessage());
    }

    @Test
    void testCalculerMontantLocation() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        BigDecimal montant = service.calculerMontantLocation(produit, 5);

        assertEquals(new BigDecimal("50.00"), montant);
    }

    @Test
    void testCalculerMontantTotal() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        BigDecimal montantTotal =
                service.calculerMontantTotal(produit, 5);

        assertEquals(new BigDecimal("150.00"), montantTotal);
    }

    @Test
    void testArrondiMontantLocation() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("1.005"),
                new BigDecimal("0.00")
        );

        BigDecimal montant =
                service.calculerMontantLocation(produit, 1);

        assertEquals(new BigDecimal("1.01"), montant);
    }

    @Test
    void testCalculCompletAvecCaution() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur portable",
                new BigDecimal("10.25"),
                new BigDecimal("50.00")
        );

        BigDecimal montantTotal =
                service.calculerMontantTotal(produit, 3);

        assertEquals(new BigDecimal("80.75"), montantTotal);
    }

    @Test
    void testGenererReferencesUniques() {
        String reference1 = service.genererReference();
        String reference2 = service.genererReference();

        assertNotNull(reference1);
        assertNotNull(reference2);
        assertNotEquals(reference1, reference2);
    }

    @Test
    void testReservationRefuseeSansModifierCommandes() {
        int nombreCommandesAvant = service.getCommandes().size();

        LocalDate aujourdHui = LocalDate.now();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E999",
                        "P001",
                        aujourdHui,
                        aujourdHui
                )
        );

        assertEquals("Étudiant introuvable", exception.getMessage());

        assertEquals(
                nombreCommandesAvant,
                service.getCommandes().size()
        );
    }

    @Test
    void testProduitIndisponibleSansModifierCommandes() {
        LocalDate debut = LocalDate.now().plusDays(1);
        LocalDate fin = debut.plusDays(2);

        // Ajouter un étudiant
        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "kmd")
        );

        // Ajouter un produit
        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur portable",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );

        // Ajouter une commande qui réserve déjà le produit
        service.ajouterCommande(
                new Commande(
                        "REF001",
                        "E002",
                        "P001",
                        debut,
                        fin,
                        3,
                        new BigDecimal("30.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("130.00")
                )
        );

        int nombreCommandesAvant = service.getCommandes().size();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        debut,
                        fin
                )
        );

        assertEquals("Produit indisponible", exception.getMessage());

        assertEquals(
                nombreCommandesAvant,
                service.getCommandes().size()
        );
    }

    @Test
    void testQuotaAtteintSansModifierCommandes() {
        LocalDate debut = LocalDate.now().plusDays(1);
        LocalDate fin = debut.plusDays(2);

        service.ajouterEtudiant(
                new Etudiant("E001", "Lina", "kmd")
        );

        service.ajouterProduit(
                new Produit(
                        "P003",
                        "Clavier",
                        new BigDecimal("10.00"),
                        new BigDecimal("50.00")
                )
        );

        // Deux commandes actives pour le même étudiant
        service.ajouterCommande(
                new Commande(
                        "REF001", "E001", "P001",
                        debut, fin, 3,
                        new BigDecimal("30.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("130.00")
                )
        );

        service.ajouterCommande(
                new Commande(
                        "REF002", "E001", "P002",
                        debut, fin, 3,
                        new BigDecimal("30.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("130.00")
                )
        );

        int nombreCommandesAvant = service.getCommandes().size();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P003",
                        debut,
                        fin
                )
        );

        assertEquals(
                "Quota maximal de réservations atteint",
                exception.getMessage()
        );

        assertEquals(
                nombreCommandesAvant,
                service.getCommandes().size()
        );
    }

}

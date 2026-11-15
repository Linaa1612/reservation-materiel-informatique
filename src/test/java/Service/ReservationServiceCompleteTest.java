package Service;


import Model.Commande;
import Model.Etudiant;
import Model.Produit;
import Exception.ReservationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceCompleteTest {

    private ReservationService service;

    private final LocalDate aujourdHui = LocalDate.now();

    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }

    // Méthodes utilitaires

    private void ajouterEtudiantEtProduit() {
        service.ajouterEtudiant(new Etudiant("E001", "Lina", "kmd"));

        service.ajouterProduit(
                new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00")
                )
        );
    }

    private Commande creerCommande(
            String idEtudiant,
            String idProduit,
            LocalDate debut,
            LocalDate fin) {

        return new Commande(
                "REF-TEST",
                idEtudiant,
                idProduit,
                debut,
                fin,
                service.calculerDuree(debut, fin),
                new BigDecimal("50.00"),
                new BigDecimal("100.00"),
                new BigDecimal("150.00")
        );
    }

    // =====================================================
    // RG01 : Validation des paramètres
    // =====================================================

    @Test
    void testIdentifiantEtudiantVide() {
        ajouterEtudiantEtProduit();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        assertEquals(
                "Identifiant étudiant obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testIdentifiantEtudiantNull() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        null,
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        assertEquals(
                "Identifiant étudiant obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testTarifJournalierNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur",
                        null,
                        new BigDecimal("100.00")
                )
        );

        assertEquals("Tarif journalier invalide", exception.getMessage());
    }

    @Test
    void testTarifJournalierZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur",
                        BigDecimal.ZERO,
                        new BigDecimal("100.00")
                )
        );

        assertEquals("Tarif journalier invalide", exception.getMessage());
    }

    @Test
    void testTarifJournalierNegatif() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("-10.00"),
                        new BigDecimal("100.00")
                )
        );

        assertEquals("Tarif journalier invalide", exception.getMessage());
    }

    @Test
    void testTarifJournalierPositifAccepte() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        assertEquals(
                new BigDecimal("10.00"),
                produit.getTarifJournalier()
        );
    }

    @Test
    void testIdentifiantProduitVide() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "",
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        assertEquals(
                "Identifiant produit obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testIdentifiantProduitNull() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        null,
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        assertEquals(
                "Identifiant produit obligatoire",
                exception.getMessage()
        );
    }

    @Test
    void testDateDebutNull() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        null,
                        aujourdHui
                )
        );

        assertEquals("Les dates sont obligatoires", exception.getMessage());
    }

    @Test
    void testDateFinNull() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui,
                        null
                )
        );

        assertEquals("Les dates sont obligatoires", exception.getMessage());
    }

    // =====================================================
    // RG02 : Validation de la date de début
    // =====================================================

    @Test
    void testDateDebutDansLePasse() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui.minusDays(1),
                        aujourdHui
                )
        );

        assertEquals("Date de début invalide", exception.getMessage());
    }

    @Test
    void testDateDebutEgaleAujourdhui() {
        ajouterEtudiantEtProduit();

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui
        );

        assertNotNull(commande);
    }

    // =====================================================
    // RG03 : Cohérence des dates
    // =====================================================

    @Test
    void testDateFinAvantDateDebut() {
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui.plusDays(2),
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals(
                "Date de fin antérieure à la date de début",
                exception.getMessage()
        );
    }

    @Test
    void testDateDebutEgaleDateFin() {
        ajouterEtudiantEtProduit();

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui
        );

        assertNotNull(commande);
        assertEquals(1, commande.getDuree());
    }

    // =====================================================
    // RG04 : Calcul de la durée
    // =====================================================

    @Test
    void testCalculDureeUneJournee() {
        int duree = service.calculerDuree(
                aujourdHui,
                aujourdHui
        );

        assertEquals(1, duree);
    }

    @Test
    void testCalculDureeCinqJours() {
        int duree = service.calculerDuree(
                aujourdHui,
                aujourdHui.plusDays(4)
        );

        assertEquals(5, duree);
    }

    // =====================================================
    // RG05 : Recherche étudiant et produit
    // =====================================================

    @Test
    void testEtudiantExistant() {
        ajouterEtudiantEtProduit();

        Etudiant etudiant = service.rechercherEtudiant("E001");

        assertNotNull(etudiant);
        assertEquals("E001", etudiant.getIdEtudiant());
    }

    @Test
    void testEtudiantIntrouvable() {
        ajouterEtudiantEtProduit();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E999",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals("Étudiant introuvable", exception.getMessage());
    }

    @Test
    void testProduitExistant() {
        ajouterEtudiantEtProduit();

        Produit produit = service.rechercherProduit("P001");

        assertNotNull(produit);
        assertEquals("P001", produit.getIdProduit());
    }

    @Test
    void testProduitIntrouvable() {
        service.ajouterEtudiant(new Etudiant("E001", "Lina", "kmd"));

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P999",
                        aujourdHui,
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals("Produit introuvable", exception.getMessage());
    }

    // =====================================================
    // RG06 : Durée maximale de 7 jours
    // =====================================================

    @Test
    void testDureeSeptJoursAcceptee() {
        ajouterEtudiantEtProduit();

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(6)
        );

        assertNotNull(commande);
        assertEquals(7, commande.getDuree());
    }

    @Test
    void testDureeHuitJoursRefusee() {
        ajouterEtudiantEtProduit();

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(7)
                )
        );

        assertEquals(
                "Durée maximale de 7 jours dépassée",
                exception.getMessage()
        );
    }

    // =====================================================
    // RG07 : Quota maximal de 2 réservations
    // =====================================================

    @Test
    void testQuotaZeroReservation() {
        ajouterEtudiantEtProduit();

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        assertNotNull(commande);
    }

    @Test
    void testQuotaUneReservation() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P002",
                        aujourdHui.plusDays(5),
                        aujourdHui.plusDays(6)
                )
        );

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        assertNotNull(commande);
    }

    @Test
    void testQuotaDeuxReservationsRefusees() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P002",
                        aujourdHui.plusDays(5),
                        aujourdHui.plusDays(6)
                )
        );

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P003",
                        aujourdHui.plusDays(8),
                        aujourdHui.plusDays(9)
                )
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals(
                "Quota maximal de réservations atteint",
                exception.getMessage()
        );
    }

    @Test
    void testQuotaIgnoreReservationsTerminees() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P002",
                        aujourdHui.minusDays(5),
                        aujourdHui.minusDays(1)
                )
        );

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        assertNotNull(commande);
    }

    @Test
    void testQuotaCompteReservationSeTerminantAujourdhui() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P002",
                        aujourdHui.minusDays(2),
                        aujourdHui
                )
        );

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui.plusDays(1),
                aujourdHui.plusDays(2)
        );

        assertNotNull(commande);
    }

    // =====================================================
    // RG08 : Disponibilité du produit
    // =====================================================

    @Test
    void testProduitDisponibleSansReservation() {
        ajouterEtudiantEtProduit();

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        assertNotNull(commande);
    }

    @Test
    void testProduitDisponibleSurPeriodeDifferente() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        // Le produit est déjà réservé par E001.
        // On utilise un autre étudiant pour isoler le test de disponibilité.
        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abcd"));

        Commande commande = service.reserverProduit(
                "E002",
                "P001",
                aujourdHui.plusDays(4),
                aujourdHui.plusDays(5)
        );

        assertNotNull(commande);
    }

    @Test
    void testProduitIndisponibleChevauchement() {
        ajouterEtudiantEtProduit();

        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abcd"));

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(4)
                )
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E002",
                        "P001",
                        aujourdHui.plusDays(2),
                        aujourdHui.plusDays(5)
                )
        );

        assertEquals("Produit indisponible", exception.getMessage());
    }

    @Test
    void testProduitIndisponibleDatesAdjacentes() {
        ajouterEtudiantEtProduit();

        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abcd"));

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(2)
                )
        );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E002",
                        "P001",
                        aujourdHui.plusDays(2),
                        aujourdHui.plusDays(4)
                )
        );

        assertEquals("Produit indisponible", exception.getMessage());
    }

    @Test
    void testAutreProduitResteDisponible() {
        ajouterEtudiantEtProduit();

        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abcd"));

        service.ajouterProduit(
                new Produit(
                        "P002",
                        "Souris",
                        new BigDecimal("5.00"),
                        new BigDecimal("20.00")
                )
        );

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(3)
                )
        );

        Commande commande = service.reserverProduit(
                "E002",
                "P002",
                aujourdHui,
                aujourdHui.plusDays(3)
        );

        assertNotNull(commande);
        assertEquals("P002", commande.getIdProduit());
    }

    // =====================================================
    // RG10, RG11 et RG12 : Calculs financiers
    // =====================================================

    @Test
    void testCalculMontantLocation() {
        ajouterEtudiantEtProduit();

        Produit produit = service.rechercherProduit("P001");

        BigDecimal montant = service.calculerMontantLocation(
                produit,
                5
        );

        assertEquals(new BigDecimal("50.00"), montant);
    }

    @Test
    void testCalculMontantTotalAvecCaution() {
        ajouterEtudiantEtProduit();

        Produit produit = service.rechercherProduit("P001");

        BigDecimal montant = service.calculerMontantTotal(
                produit,
                5
        );

        assertEquals(new BigDecimal("150.00"), montant);
    }

    @Test
    void testCautionNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("10.00"),
                        null
                )
        );

        assertEquals(
                "La caution doit être positive ou nulle.",
                exception.getMessage()
        );
    }

    @Test
    void testCautionNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Produit(
                        "P001",
                        "Ordinateur",
                        new BigDecimal("10.00"),
                        new BigDecimal("-50.00")
                )
        );

        assertEquals(
                "La caution doit être positive ou nulle.",
                exception.getMessage()
        );
    }

    @Test
    void testCautionZeroAcceptee() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur",
                new BigDecimal("10.00"),
                BigDecimal.ZERO
        );

        assertEquals(
                BigDecimal.ZERO,
                produit.getCaution()
        );
    }

    @Test
    void testCautionPositiveAcceptee() {
        Produit produit = new Produit(
                "P001",
                "Ordinateur",
                new BigDecimal("10.00"),
                new BigDecimal("100.00")
        );

        assertEquals(
                new BigDecimal("100.00"),
                produit.getCaution()
        );
    }

    @Test
    void testArrondiHalfUp() {
        Produit produit = new Produit(
                "P003",
                "Clavier",
                new BigDecimal("1.005"),
                BigDecimal.ZERO
        );

        BigDecimal montant = service.calculerMontantLocation(
                produit,
                1
        );

        assertEquals(new BigDecimal("1.01"), montant);
    }

    @Test
    void testCalculMontantAvecTarifDecimal() {
        Produit produit = new Produit(
                "P004",
                "Ecran",
                new BigDecimal("10.25"),
                new BigDecimal("50.00")
        );

        BigDecimal location = service.calculerMontantLocation(
                produit,
                3
        );

        BigDecimal total = service.calculerMontantTotal(
                produit,
                3
        );

        assertEquals(new BigDecimal("30.75"), location);
        assertEquals(new BigDecimal("80.75"), total);
    }

    // =====================================================
    // RG13 : Génération de référence
    // =====================================================

    @Test
    void testGenererReferenceNonNull() {
        String reference = service.genererReference();

        assertNotNull(reference);
        assertFalse(reference.isEmpty());
    }

    @Test
    void testGenererDeuxReferencesDifferentes() {
        String reference1 = service.genererReference();
        String reference2 = service.genererReference();

        assertNotEquals(reference1, reference2);
    }

    @Test
    void testDeuxReservationsOntDesReferencesDifferentes() {
        ajouterEtudiantEtProduit();

        service.ajouterProduit(
                new Produit(
                        "P002",
                        "Souris",
                        new BigDecimal("5.00"),
                        new BigDecimal("20.00")
                )
        );

        Commande commande1 = service.reserverProduit(
                "E001",
                "P001",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        Commande commande2 = service.reserverProduit(
                "E001",
                "P002",
                aujourdHui,
                aujourdHui.plusDays(1)
        );

        assertNotEquals(
                commande1.getReference(),
                commande2.getReference()
        );
    }

    // =====================================================
    // RG14 : Aucun enregistrement en cas de refus
    // =====================================================

    @Test
    void testRefusEtudiantInexistantSansEnregistrement() {
        ajouterEtudiantEtProduit();

        assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E999",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals(0, service.getCommandes().size());
    }

    @Test
    void testReservationAvantPeriodeExistante() {
        ajouterEtudiantEtProduit();
        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abccd"));

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        LocalDate.of(2026, 12, 10),
                        LocalDate.of(2026, 12, 15)
                )
        );

        Commande commande = service.reserverProduit(
                "E002",
                "P001",
                LocalDate.of(2026, 12, 3),
                LocalDate.of(2026, 12, 8)
        );

        assertNotNull(commande);
    }

    @Test
    void testRefusProduitIndisponibleSansEnregistrement() {
        ajouterEtudiantEtProduit();

        service.ajouterEtudiant(new Etudiant("E002", "Sara", "abcd"));

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(3)
                )
        );

        int nombreAvant = service.getCommandes().size();

        assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E002",
                        "P001",
                        aujourdHui.plusDays(1),
                        aujourdHui.plusDays(2)
                )
        );

        assertEquals(nombreAvant, service.getCommandes().size());
    }

    @Test
    void testRefusQuotaSansEnregistrement() {
        ajouterEtudiantEtProduit();

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P002",
                        aujourdHui.plusDays(5),
                        aujourdHui.plusDays(6)
                )
        );

        service.ajouterCommande(
                creerCommande(
                        "E001",
                        "P003",
                        aujourdHui.plusDays(8),
                        aujourdHui.plusDays(9)
                )
        );

        int nombreAvant = service.getCommandes().size();

        assertThrows(
                ReservationException.class,
                () -> service.reserverProduit(
                        "E001",
                        "P001",
                        aujourdHui,
                        aujourdHui.plusDays(1)
                )
        );

        assertEquals(nombreAvant, service.getCommandes().size());
    }

    // =====================================================
    // Scénario de référence de l'énoncé
    // =====================================================

    @Test
    void testReservationCompleteExempleReference() {
        ajouterEtudiantEtProduit();

        LocalDate dateDebut = LocalDate.of(2026, 12, 10);
        LocalDate dateFin = LocalDate.of(2026, 12, 14);

        Commande commande = service.reserverProduit(
                "E001",
                "P001",
                dateDebut,
                dateFin
        );

        assertNotNull(commande);
        assertNotNull(commande.getReference());

        assertEquals("E001", commande.getIdEtudiant());
        assertEquals("P001", commande.getIdProduit());

        assertEquals(5, commande.getDuree());

        assertEquals(
                new BigDecimal("50.00"),
                commande.getMontantLocation()
        );

        assertEquals(
                new BigDecimal("100.00"),
                commande.getCaution()
        );

        assertEquals(
                new BigDecimal("150.00"),
                commande.getMontantTotal()
        );

        assertEquals(1, service.getCommandes().size());
    }
}


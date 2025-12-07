-- ================================================================
-- SecureDoc - Catégories et Tags en Français
-- Date: 2025-12-06
-- ================================================================

-- ================================================================
-- CATÉGORIES (Organisation principale)
-- ================================================================

-- 📁 Administration
INSERT INTO categories (name, description, created_at, updated_at) VALUES
('Contrats', 'Tous les contrats et accords', NOW(), NOW()),
('Factures', 'Factures émises et reçues', NOW(), NOW()),
('Devis', 'Devis et estimations', NOW(), NOW()),
('Reçus', 'Reçus et justificatifs', NOW(), NOW()),
('Documents légaux', 'Documents juridiques administratifs', NOW(), NOW()),
('Documents fiscaux', 'Déclarations et documents fiscaux', NOW(), NOW()),
('Procédures internes', 'Procédures et politiques internes', NOW(), NOW()),

-- 🧾 Finance
('Comptabilité', 'Documents comptables', NOW(), NOW()),
('Budget', 'Budgets et prévisions financières', NOW(), NOW()),
('Paiements', 'Ordres de paiement et virements', NOW(), NOW()),
('Audit financier', 'Rapports d''audit financier', NOW(), NOW()),
('Déclarations fiscales', 'Déclarations TVA, impôts, taxes', NOW(), NOW()),

-- ⚖️ Juridique
('NDA', 'Accords de confidentialité', NOW(), NOW()),
('Contrats juridiques', 'Contrats légaux et accords', NOW(), NOW()),
('Litiges', 'Documents de litiges et contentieux', NOW(), NOW()),
('Propriété intellectuelle', 'Brevets, marques, droits d''auteur', NOW(), NOW()),
('Conformité RGPD', 'Documents de conformité et protection des données', NOW(), NOW()),

-- 👥 Ressources Humaines
('Dossiers employés', 'Dossiers personnels des employés', NOW(), NOW()),
('Onboarding', 'Documents d''intégration nouveaux employés', NOW(), NOW()),
('Offboarding', 'Documents de départ et fin de contrat', NOW(), NOW()),
('Paie', 'Fiches de paie et documents salariaux', NOW(), NOW()),
('Congés', 'Demandes de congés et absences', NOW(), NOW()),
('Formations', 'Documents de formation et certifications', NOW(), NOW()),

-- 📊 Projets
('Cahier des charges', 'Spécifications et exigences projet', NOW(), NOW()),
('Planning', 'Plannings et calendriers projet', NOW(), NOW()),
('Documentation technique', 'Documentation technique détaillée', NOW(), NOW()),
('Documentation fonctionnelle', 'Spécifications fonctionnelles', NOW(), NOW()),
('Livrables', 'Livrables et résultats projet', NOW(), NOW()),
('Comptes-rendus réunions', 'CR de réunions et décisions', NOW(), NOW()),
('Tests QA', 'Documents de tests et assurance qualité', NOW(), NOW()),

-- 🖥️ Technique / IT
('Architecture', 'Architecture système et infrastructure', NOW(), NOW()),
('API', 'Documentation API et endpoints', NOW(), NOW()),
('Serveurs', 'Configuration et maintenance serveurs', NOW(), NOW()),
('DevOps', 'Documentation DevOps et déploiement', NOW(), NOW()),
('Backups', 'Sauvegardes et restaurations', NOW(), NOW()),
('Sécurité IT', 'Sécurité informatique et protocoles', NOW(), NOW()),
('Certificats Clés', 'Certificats SSL et clés d''accès', NOW(), NOW()),

-- 🎨 Créatif / Médiatique
('Artwork', 'Créations graphiques et visuelles', NOW(), NOW()),
('Collections', 'Collections et portfolios', NOW(), NOW()),
('Conception UX-UI', 'Design d''interface et expérience utilisateur', NOW(), NOW()),
('Prototypes Maquettes', 'Prototypes et maquettes design', NOW(), NOW()),
('Médias', 'Images, vidéos et contenus multimédias', NOW(), NOW()),

-- 🗃️ Templates / Modèles
('Modèles contrats', 'Templates de contrats réutilisables', NOW(), NOW()),
('Modèles factures', 'Templates de factures', NOW(), NOW()),
('Modèles internes', 'Templates pour usage interne', NOW(), NOW()),

-- 🏛️ Entreprise
('Marketing', 'Documents marketing et communication', NOW(), NOW()),
('Vente', 'Documents commerciaux et ventes', NOW(), NOW()),
('Support', 'Documentation support client', NOW(), NOW()),
('Fournisseurs', 'Documents fournisseurs et prestataires', NOW(), NOW()),
('Partenaires', 'Documents partenariats et collaborations', NOW(), NOW()),
('Stock Inventaire', 'Gestion stock et inventaires', NOW(), NOW()),

-- 📦 Divers
('Brouillons', 'Documents en cours de rédaction', NOW(), NOW()),
('Archives', 'Documents archivés', NOW(), NOW()),
('Documents personnels', 'Documents personnels et divers', NOW(), NOW());


-- ================================================================
-- TAGS (Mots-clés transversaux)
-- ================================================================

-- 🔥 Tags généraux
INSERT INTO tags (name, created_at, updated_at) VALUES
('Urgent', NOW(), NOW()),
('Important', NOW(), NOW()),
('À vérifier', NOW(), NOW()),
('À signer', NOW(), NOW()),
('À archiver', NOW(), NOW()),
('En attente', NOW(), NOW()),
('Terminé', NOW(), NOW()),
('Interne', NOW(), NOW()),
('Externe', NOW(), NOW()),

-- 🏢 Tags entreprise
('Marketing', NOW(), NOW()),
('Finance', NOW(), NOW()),
('RH', NOW(), NOW()),
('Juridique', NOW(), NOW()),
('Vente', NOW(), NOW()),
('Client', NOW(), NOW()),
('Fournisseur', NOW(), NOW()),
('Partenaire', NOW(), NOW()),

-- 🛠️ Tags techniques
('Backend', NOW(), NOW()),
('Frontend', NOW(), NOW()),
('API', NOW(), NOW()),
('Serveur', NOW(), NOW()),
('DevOps', NOW(), NOW()),
('Base de données', NOW(), NOW()),
('Sécurité', NOW(), NOW()),
('Docker', NOW(), NOW()),
('Kubernetes', NOW(), NOW()),
('CI-CD', NOW(), NOW()),

-- 📅 Tags temporels
('2025', NOW(), NOW()),
('2024', NOW(), NOW()),
('2023', NOW(), NOW()),
('Q1', NOW(), NOW()),
('Q2', NOW(), NOW()),
('Q3', NOW(), NOW()),
('Q4', NOW(), NOW()),

-- 🎨 Tags créatifs
('Design', NOW(), NOW()),
('UX', NOW(), NOW()),
('UI', NOW(), NOW()),
('Media', NOW(), NOW()),
('Artwork', NOW(), NOW()),
('Prototype', NOW(), NOW()),

-- 📄 Tags documentaires
('PDF', NOW(), NOW()),
('Word', NOW(), NOW()),
('Excel', NOW(), NOW()),
('Image', NOW(), NOW()),
('Vidéo', NOW(), NOW()),
('Blueprint', NOW(), NOW()),
('Draft', NOW(), NOW()),
('Version 1', NOW(), NOW()),
('Version 2', NOW(), NOW()),

-- ⚖️ Tags légaux
('NDA', NOW(), NOW()),
('Contrat', NOW(), NOW()),
('Clause', NOW(), NOW()),
('Confidential', NOW(), NOW()),
('RGPD', NOW(), NOW()),

-- 🔐 Tags sécurité / confidentialité
('Privé', NOW(), NOW()),
('Sensible', NOW(), NOW()),
('Confidentiel', NOW(), NOW()),
('Public', NOW(), NOW()),
('Restreint', NOW(), NOW()),

-- 🧑‍💼 Tags RH
('Employé', NOW(), NOW()),
('Manager', NOW(), NOW()),
('Candidat', NOW(), NOW()),
('Stage', NOW(), NOW()),
('CDD', NOW(), NOW()),
('CDI', NOW(), NOW());

-- ================================================================
-- Résumé des insertions
-- ================================================================
-- Total Catégories: 56
-- Total Tags: 71
-- ================================================================

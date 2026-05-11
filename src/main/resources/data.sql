-- ============================================================
-- Seed de contas bancárias para o projeto banking-batch
-- 20 contas: 11 SAVINGS (com juros) + 9 CHECKING (sem juros)
-- Todas as contas são referenciadas em transactions.csv
-- ============================================================

INSERT INTO accounts (account_number, owner_name, email, account_type, balance, interest_rate, created_at)
VALUES
    ('ACC-001', 'Alice Mendes', 'alice.mendes@email.com', 'SAVINGS', 12500.00, 0.0650, NOW()),
    ('ACC-002', 'Bruno Costa', 'bruno.costa@email.com', 'CHECKING', 8300.00, 0.0000, NOW()),
    ('ACC-003', 'Carla Souza', 'carla.souza@email.com', 'SAVINGS', 45000.00, 0.0700, NOW()),
    ('ACC-004', 'Daniel Lima', 'daniel.lima@email.com', 'SAVINGS', 3200.00, 0.0620, NOW()),
    ('ACC-005', 'Elena Ferreira', 'elena.ferreira@email.com', 'CHECKING', 22000.00, 0.0000, NOW()),
    ('ACC-006', 'Felipe Rocha', 'felipe.rocha@email.com', 'SAVINGS', 9800.00, 0.0680, NOW()),
    ('ACC-007', 'Gabriela Nunes', 'gabriela.nunes@email.com', 'CHECKING', 5100.00, 0.0000, NOW()),
    ('ACC-008', 'Henrique Alves', 'henrique.alves@email.com', 'SAVINGS', 78000.00, 0.0720, NOW()),
    ('ACC-009', 'Isabela Castro', 'isabela.castro@email.com', 'CHECKING', 14200.00, 0.0000, NOW()),
    ('ACC-010', 'Joao Pereira', 'joao.pereira@email.com', 'SAVINGS', 1500.00, 0.0600, NOW()),
    ('ACC-011', 'Karina Dias', 'karina.dias@email.com', 'SAVINGS', 33000.00, 0.0710, NOW()),
    ('ACC-012', 'Leonardo Martins', 'leonardo.martins@email.com', 'CHECKING', 6700.00, 0.0000, NOW()),
    ('ACC-013', 'Mariana Oliveira', 'mariana.oliveira@email.com', 'SAVINGS', 19500.00, 0.0660, NOW()),
    ('ACC-014', 'Nicolas Santos', 'nicolas.santos@email.com', 'CHECKING', 11000.00, 0.0000, NOW()),
    ('ACC-015', 'Olivia Ribeiro', 'olivia.ribeiro@email.com', 'SAVINGS', 55000.00, 0.0730, NOW()),
    ('ACC-016', 'Paulo Carvalho', 'paulo.carvalho@email.com', 'CHECKING', 4400.00, 0.0000, NOW()),
    ('ACC-017', 'Quintino Araujo', 'quintino.araujo@email.com', 'SAVINGS', 7200.00, 0.0640, NOW()),
    ('ACC-018', 'Renata Gomes', 'renata.gomes@email.com', 'CHECKING', 28000.00, 0.0000, NOW()),
    ('ACC-019', 'Sergio Barros', 'sergio.barros@email.com', 'SAVINGS', 62000.00, 0.0750, NOW()),
    ('ACC-020', 'Tatiana Moreira', 'tatiana.moreira@email.com', 'CHECKING', 3900.00, 0.0000, NOW());

-- ============================================================
-- Total: 20 contas inseridas
-- SAVINGS : 11 contas
-- CHECKING: 9 contas
-- ============================================================
-- ============================================================
-- DDL implícito: a tabela marketplace_offers é criada pelo JPA
-- (spring.jpa.hibernate.ddl-auto=create-drop)
-- Nenhum seed necessário — dados chegam via importação CSV.
-- ============================================================

package server.database;

import commons.Debts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtRepository extends JpaRepository<Debts, Long> {}

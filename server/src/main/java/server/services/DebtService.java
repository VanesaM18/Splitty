package server.services;

import org.springframework.stereotype.Service;
import server.database.DebtRepository;

import java.util.Optional;
import java.util.Random;

@Service
public class DebtService {
    private final DebtRepository debtRepository;

    /**
     * constructor for DebtService
     * @param debtRepository repository for accessing debt data
     */
    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    /**
     * gets all debts from the repository
     * @return list of all debts
     */
    public java.util.List<commons.Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    /**
     * gets a debt by its ID
     * @param id ID of the debt to retrieve
     * @return an Optional containing the debt if found, otherwise empty
     */
    public Optional<commons.Debt> getDebtById(long id) {
        if (id < 0 || !debtRepository.existsById(id)) {
            return Optional.empty();
        }
        return debtRepository.findById(id);
    }

    /**
     * deletes a debt by its ID
     * @param id ID of the debt to delete
     * @return an Optional containing the deleted debt if found and deleted, otherwise empty
     */
    public Optional<commons.Debt> deleteDebtById(long id) {
        if (!debtRepository.existsById(id)) {
            return Optional.empty();
        }
        var debt = debtRepository.findById(id);
        debtRepository.deleteById(id);
        return debt;
    }

    /**
     * adds a new debt to the repository
     * @param debt debt to add
     * @return an Optional containing the added debt if successfully added, otherwise empty
     */
    public Optional<commons.Debt> addDebt(commons.Debt debt) {
        if (debt.getDebtor() == null || debt.getCreditor() == null
                || debt.getAmount() == null) {
            return Optional.empty();
        }
        return Optional.of(debtRepository.save(debt));
    }

    /**
     * gets a random debt from the repository
     * @return an Optional containing a randomly selected debt if available, otherwise empty
     */
    public Optional<commons.Debt> getRandomDebt() {
        java.util.List<commons.Debt> debts = debtRepository.findAll();
        if (debts.isEmpty()) {
            return Optional.empty();
        }
        int index = new Random().nextInt(debts.size());
        return Optional.of(debts.get(index));
    }
}

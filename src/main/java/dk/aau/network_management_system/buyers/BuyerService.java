package dk.aau.network_management_system.buyers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BuyerService {

    private static final Logger log = LoggerFactory.getLogger(BuyerService.class);

    private final BuyerRepository repository;

    @Autowired
    public BuyerService(BuyerRepository repository) {
        this.repository = repository;
    }

    public List<BuyerDTO> listBuyers() {
        try {
            return repository.findAllIdAndName().stream()
                .map(row -> new BuyerDTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1]
                ))
                .collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("Database error while fetching buyers", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving buyers");
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error while fetching buyers", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing buyers");
        }
    }
}

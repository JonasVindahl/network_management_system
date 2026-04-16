package dk.aau.network_management_system.Cooperative_Analytics;

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
public class CooperativeService {

    private static final Logger log = LoggerFactory.getLogger(CooperativeService.class);

    private final CooperativeRepository repository;

    @Autowired
    public CooperativeService(CooperativeRepository repository) {
        this.repository = repository;
    }

    public List<CooperativeDTO> listCooperatives() {
        try {
            return repository.findAllIdAndName().stream()
                .map(row -> new CooperativeDTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1]
                ))
                .collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("Database error while fetching cooperatives", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving cooperatives");
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error while fetching cooperatives", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing cooperatives");
        }
    }
}

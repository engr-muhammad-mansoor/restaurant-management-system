package restaurant.management.system.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.DTOs.TableDTO;
import restaurant.management.system.backend.entities.Reservation;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.entities.Table;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.repositories.TableRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public TableDTO addTable(TableDTO tableDTO, Restaurant restaurant) {
        Table table = tableDTO.toEntity(restaurant);
        table = tableRepository.save(table);
        return TableDTO.fromEntity(table);
    }

    public Page<TableDTO> getTablesByRestaurant(Restaurant restaurant, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Create a Pageable object
        Page<Table> tablePage = tableRepository.findByRestaurant(restaurant, pageable); // Fetch paginated data

        return tablePage.map(TableDTO::fromEntity); // Convert entities to DTOs while maintaining pagination
    }


    public Optional<Table> findById(Long tableId) {
        return tableRepository.findById(tableId);
    }

    public TableDTO updateTable(Long tableId, TableDTO tableDTO, Restaurant restaurant) {
        Table table = tableRepository.findById(tableId).orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));
        Table tableDTOUpdated = tableDTO.toEntity(restaurant);
        tableDTOUpdated.setId(tableId);
        tableDTOUpdated.setReservations(table.getReservations());
        tableDTOUpdated = tableRepository.save(tableDTOUpdated);
        return TableDTO.fromEntity(tableDTOUpdated);
    }

    public void deleteTable(Long tableId) {
        if (!tableRepository.existsById(tableId)) {
            throw new ResourceNotFoundException("Table not found with ID: " + tableId);
        }
        tableRepository.deleteById(tableId);
    }

    public Page<TableDTO> getUnreservedTablesByRestaurant(Restaurant restaurant, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Reservation.Status> statuses = Arrays.asList(Reservation.Status.PENDING, Reservation.Status.CONFIRMED);
        Page<Table> tablePage = tableRepository.findUnreservedTablesByRestaurant(restaurant, startDateTime, endDateTime, statuses, pageable); // Fetch paginated data

        return tablePage.map(TableDTO::fromEntity);
    }
}

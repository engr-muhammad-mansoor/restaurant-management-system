package restaurant.management.system.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurant.management.system.backend.DTOs.TableDTO;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.services.RestaurantService;
import restaurant.management.system.backend.services.TableService;
import restaurant.management.system.backend.utils.ApiResponse;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;
    private final RestaurantService restaurantService;

    public TableController(TableService tableService, RestaurantService restaurantService) {
        this.tableService = tableService;
        this.restaurantService = restaurantService;
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping
    public ApiResponse<TableDTO> addTable(@RequestBody TableDTO tableDTO) {
        Restaurant restaurant = restaurantService.findById(tableDTO.getRestaurantId()).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + tableDTO.getRestaurantId()));
        TableDTO createdTable = tableService.addTable(tableDTO, restaurant);
        return new ApiResponse<>(true, "Table added successfully", createdTable);
    }

    @GetMapping
    public ApiResponse<Page<TableDTO>> getTablesByRestaurant(@RequestParam Long restaurantId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Restaurant restaurant = restaurantService.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        Page<TableDTO> tables = tableService.getTablesByRestaurant(restaurant, page, size);

        if (tables.isEmpty()) {
            throw new ResourceNotFoundException("No tables found for restaurant ID: " + restaurantId);
        }

        return new ApiResponse<>(true, "Fetched tables successfully", tables);
    }

    @GetMapping("/unreserved")
    public ApiResponse<Page<TableDTO>> getUnreservedTablesByRestaurant(@RequestParam Long restaurantId, @RequestParam(required = true) LocalDateTime startDateTime, @RequestParam(required = true) LocalDateTime endDateTime, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Restaurant restaurant = restaurantService.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        Page<TableDTO> tables = tableService.getUnreservedTablesByRestaurant(restaurant,startDateTime,endDateTime, page, size);

        if (tables.isEmpty()) {
            throw new ResourceNotFoundException("No tables found for the given time");
        }

        return new ApiResponse<>(true, "Fetched tables successfully for the selected time.", tables);
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PutMapping
    public ApiResponse<TableDTO> updateTable(@RequestParam Long tableId, @RequestBody TableDTO tableDTO) {
        Restaurant restaurant = restaurantService.findById(tableDTO.getRestaurantId()).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + tableDTO.getRestaurantId()));
        TableDTO createdTable = tableService.updateTable(tableId, tableDTO, restaurant);
        return new ApiResponse<>(true, "Table updated successfully", createdTable);
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping
    public ApiResponse<TableDTO> deleteTable(@RequestParam Long tableId) {
        tableService.deleteTable(tableId);
        return new ApiResponse<>(true, "Table deleted successfully", null);
    }

}

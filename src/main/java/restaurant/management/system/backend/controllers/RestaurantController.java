package restaurant.management.system.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurant.management.system.backend.DTOs.RestaurantDTO;
import restaurant.management.system.backend.DTOs.UserDTO;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.services.RestaurantService;
import restaurant.management.system.backend.services.UserService;
import restaurant.management.system.backend.utils.ApiResponse;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ApiResponse<RestaurantDTO> addRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        return new ApiResponse<>(true, "Restaurant added successfully", restaurantService.addRestaurant(restaurantDTO));
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping
    public ApiResponse<?> deleteRestaurant(@RequestParam Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return new ApiResponse<>(true, "Deleted successfully", null);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ApiResponse<?> updateRestaurant(@RequestParam Long restaurantId, @RequestBody RestaurantDTO restaurantDTO) {
        return new ApiResponse<>(true, "Updated successfully", restaurantService.updateRestaurant(restaurantId, restaurantDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ApiResponse<Page<RestaurantDTO>> getRestaurants(@RequestParam Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        User owner = UserDTO.toEntity(userService.getUserById(userId));
        Page<RestaurantDTO> restaurants = restaurantService.getRestaurantsByOwner(owner, page, size);
        return new ApiResponse<>(true, "Fetched successfully", restaurants);
    }

    @GetMapping("/all")
    public ApiResponse<Page<RestaurantDTO>> getAllActiveRestaurants(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<RestaurantDTO> restaurants = restaurantService.getAllActiveRestaurants(page, size);
        return new ApiResponse<>(true, "Fetched successfully", restaurants);
    }

    @GetMapping("/search")
    public ApiResponse<Page<RestaurantDTO>> searchRestaurants(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<RestaurantDTO> restaurants = restaurantService.searchRestaurants(keyword, page, size);
        return new ApiResponse<>(true, "Search results fetched successfully", restaurants);
    }
}

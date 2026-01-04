package restaurant.management.system.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.DTOs.RestaurantDTO;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.repositories.RestaurantRepository;
import restaurant.management.system.backend.repositories.UserRepository;

import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public RestaurantDTO addRestaurant(RestaurantDTO restaurantDTO) {
        if (restaurantRepository.existsByNameOrPhoneNumber(restaurantDTO.getName(), restaurantDTO.getPhoneNumber())) {
            throw new ResourceNotFoundException("Restaurant with name or phone number already exists");
        }

        Restaurant restaurant = restaurantDTO.toEntity();
        User owner = userRepository.findById(restaurantDTO.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + restaurantDTO.getOwnerId()));
        restaurant.setOwner(owner);
        restaurant = restaurantRepository.save(restaurant);
        return RestaurantDTO.fromEntity(restaurant);
    }


    public Optional<Restaurant> findById(Long restaurantId) {
        return Optional.ofNullable(restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId)));
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        restaurantRepository.delete(restaurant);
    }


    public RestaurantDTO updateRestaurant(Long restaurantId, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        Restaurant updatedRestaurant = restaurantDTO.toEntity();
        updatedRestaurant.setId(restaurantId);
        updatedRestaurant.setOwner(restaurant.getOwner());
        updatedRestaurant.setTables(restaurant.getTables());
        updatedRestaurant = restaurantRepository.save(updatedRestaurant);
        return RestaurantDTO.fromEntity(updatedRestaurant);
    }

    public Page<RestaurantDTO> getAllActiveRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Create a Pageable object
        Page<Restaurant> activeRestaurantsPage = restaurantRepository.findByActive(true, pageable); // Fetch paginated data

        if (activeRestaurantsPage.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found");
        }

        return activeRestaurantsPage.map(RestaurantDTO::fromEntity); // Convert entities to DTOs while maintaining pagination
    }

    public Page<RestaurantDTO> searchRestaurants(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // If the keyword is null or empty, return all restaurants
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<Restaurant> allRestaurantsPage = restaurantRepository.findByActive(true, pageable);
            return allRestaurantsPage.map(RestaurantDTO::fromEntity);
        }
        Page<Restaurant> restaurantPage = restaurantRepository.searchByKeyword(keyword, pageable);

        if (restaurantPage.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found for the given search criteria");
        }

        return restaurantPage.map(RestaurantDTO::fromEntity); // Convert entities to DTOs
    }

    public Page<RestaurantDTO> getRestaurantsByOwner(User owner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Create a Pageable object
        Page<Restaurant> restaurantPage = restaurantRepository.findByOwner(owner, pageable); // Fetch paginated data

        if (restaurantPage.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found for owner with ID: " + owner.getId());
        }

        return restaurantPage.map(RestaurantDTO::fromEntity); // Convert entities to DTOs while maintaining pagination
    }


    public void activateRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        restaurant.setActive(true);
        restaurantRepository.save(restaurant);
    }
}

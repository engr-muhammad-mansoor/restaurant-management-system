package restaurant.management.system.backend.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.management.system.backend.entities.Restaurant;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String type = String.valueOf(Restaurant.Type.RESTAURANT);
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean active;
    private Long ownerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TableDTO> tables = new ArrayList<>();

    public static RestaurantDTO fromEntity(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setType(restaurant.getType().name());
        dto.setActive(restaurant.isActive());
        dto.setOwnerId(restaurant.getOwner().getId());
        return dto;
    }

    public Restaurant toEntity() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(this.id);
        restaurant.setName(this.name);
        restaurant.setAddress(this.address);
        restaurant.setPhoneNumber(this.phoneNumber);
        restaurant.setType(Restaurant.Type.valueOf(this.type));
        return restaurant;
    }
}

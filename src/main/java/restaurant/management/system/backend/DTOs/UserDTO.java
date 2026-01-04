package restaurant.management.system.backend.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.management.system.backend.entities.User;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role = String.valueOf(User.Role.OWNER);
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<RestaurantDTO> restaurants = new ArrayList<>();

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.id);
        user.setName(userDTO.name);
        user.setEmail(userDTO.email);
        user.setPhoneNumber(userDTO.phoneNumber);
        user.setRole(User.Role.valueOf(userDTO.role));
        user.setAddress(userDTO.address);
        user.setPassword(userDTO.password);
        return user;
    }
}

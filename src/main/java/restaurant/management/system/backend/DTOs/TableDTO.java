package restaurant.management.system.backend.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.entities.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String shortName;
    private String description;
    private int capacity;
    private BigDecimal depositAmount;
    private BigDecimal noShowFee;
    private int noShowExpiringTime;
//    private int defaultDurationInHours;
    private Long restaurantId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ReservationDTO> reservations = new ArrayList<>();

    public static TableDTO fromEntity(Table table) {
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setShortName(table.getShortName());
        dto.setDescription(table.getDescription());
        dto.setCapacity(table.getCapacity());
        dto.setDepositAmount(table.getDepositAmount());
        dto.setNoShowFee(table.getNoShowFee());
        dto.setNoShowExpiringTime(table.getNoShowExpiringTime());
//        dto.setDefaultDurationInHours(table.getDefaultDurationInHours());
        dto.setRestaurantId(table.getRestaurant().getId());
        return dto;
    }

    public Table toEntity(Restaurant restaurant) {
        Table table = new Table();
        table.setId(this.id);
        table.setShortName(this.shortName);
        table.setDescription(this.description);
        table.setCapacity(this.capacity);
        table.setDepositAmount(this.depositAmount);
        table.setNoShowFee(this.noShowFee);
        table.setNoShowExpiringTime(this.noShowExpiringTime);
//        table.setDefaultDurationInHours(this.defaultDurationInHours);
        table.setRestaurant(restaurant);
        return table;
    }
}

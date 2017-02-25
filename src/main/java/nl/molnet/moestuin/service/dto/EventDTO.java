package nl.molnet.moestuin.service.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Event entity.
 */
public class EventDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private ZonedDateTime eventOpenTime;

    private ZonedDateTime eventCloseTime;

    private Boolean active;

    private Integer throughputPerMinute;

    private Integer maxPassages;

    private String redirectUrl;


    private Long tenantId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ZonedDateTime getEventOpenTime() {
        return eventOpenTime;
    }

    public void setEventOpenTime(ZonedDateTime eventOpenTime) {
        this.eventOpenTime = eventOpenTime;
    }
    public ZonedDateTime getEventCloseTime() {
        return eventCloseTime;
    }

    public void setEventCloseTime(ZonedDateTime eventCloseTime) {
        this.eventCloseTime = eventCloseTime;
    }
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    public Integer getThroughputPerMinute() {
        return throughputPerMinute;
    }

    public void setThroughputPerMinute(Integer throughputPerMinute) {
        this.throughputPerMinute = throughputPerMinute;
    }
    public Integer getMaxPassages() {
        return maxPassages;
    }

    public void setMaxPassages(Integer maxPassages) {
        this.maxPassages = maxPassages;
    }
    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;

        if ( ! Objects.equals(id, eventDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", eventOpenTime='" + eventOpenTime + "'" +
            ", eventCloseTime='" + eventCloseTime + "'" +
            ", active='" + active + "'" +
            ", throughputPerMinute='" + throughputPerMinute + "'" +
            ", maxPassages='" + maxPassages + "'" +
            ", redirectUrl='" + redirectUrl + "'" +
            '}';
    }
}

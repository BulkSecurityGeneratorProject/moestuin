package nl.molnet.moestuin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "event_open_time")
    private ZonedDateTime eventOpenTime;

    @Column(name = "event_close_time")
    private ZonedDateTime eventCloseTime;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "throughput_per_minute")
    private Integer throughputPerMinute;

    @Column(name = "max_passages")
    private Integer maxPassages;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @ManyToOne
    private Tenant tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Event name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getEventOpenTime() {
        return eventOpenTime;
    }

    public Event eventOpenTime(ZonedDateTime eventOpenTime) {
        this.eventOpenTime = eventOpenTime;
        return this;
    }

    public void setEventOpenTime(ZonedDateTime eventOpenTime) {
        this.eventOpenTime = eventOpenTime;
    }

    public ZonedDateTime getEventCloseTime() {
        return eventCloseTime;
    }

    public Event eventCloseTime(ZonedDateTime eventCloseTime) {
        this.eventCloseTime = eventCloseTime;
        return this;
    }

    public void setEventCloseTime(ZonedDateTime eventCloseTime) {
        this.eventCloseTime = eventCloseTime;
    }

    public Boolean isActive() {
        return active;
    }

    public Event active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getThroughputPerMinute() {
        return throughputPerMinute;
    }

    public Event throughputPerMinute(Integer throughputPerMinute) {
        this.throughputPerMinute = throughputPerMinute;
        return this;
    }

    public void setThroughputPerMinute(Integer throughputPerMinute) {
        this.throughputPerMinute = throughputPerMinute;
    }

    public Integer getMaxPassages() {
        return maxPassages;
    }

    public Event maxPassages(Integer maxPassages) {
        this.maxPassages = maxPassages;
        return this;
    }

    public void setMaxPassages(Integer maxPassages) {
        this.maxPassages = maxPassages;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Event redirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Event tenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        if(event.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Event{" +
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

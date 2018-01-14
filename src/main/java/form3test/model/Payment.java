package form3test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by rhall on 12/01/2018.
 */
@Entity
public  class Payment {

    public  String type;

    @Id
    @JsonInclude
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy ="org.hibernate.id.UUIDGenerator"
    )
    public  String id;

    public  long version;
    public  String organisationId;

    @Embedded
    public  Attributes attributes;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        return id.equals(payment.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

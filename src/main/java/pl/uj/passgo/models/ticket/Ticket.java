package pl.uj.passgo.models.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// mock object. To be replaced
@Entity
@Table
public class Ticket {
	@Id
	private Long id;
}

package pl.uj.passgo.models.transaction;

import jakarta.persistence.*;
import lombok.*;
import pl.uj.passgo.models.Ticket;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "transaction_component")
public class TransactionComponent {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_component_id_seq")
	@SequenceGenerator(name = "transaction_component_id_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "transaction_id", nullable = false)
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;
}

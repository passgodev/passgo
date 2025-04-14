package pl.uj.passgo.models.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "transaction")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
	@SequenceGenerator(name = "transaction_id_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@Column(name = "total_price", nullable = false, precision = 2)
	private BigDecimal totalPrice;

	@CreationTimestamp
	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
	private List<TransactionComponent> transactionComponents;
}

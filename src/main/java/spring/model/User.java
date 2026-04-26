package spring.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "userr")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class User {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(nullable = false)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Builder.Default
	@Column(nullable = false)
	private Boolean enabled = false;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Builder.Default
	@Column(name = "failed_attempts", nullable = false)
	private Integer failedAttempts = 0;

	@Column(name = "blocking_time")
	private LocalDateTime blockingTime;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@ToString.Exclude
	private List<Role> roles = new ArrayList<>();

	public String getRolesAsString() {
		return roles.stream().map(Role::getName).collect(Collectors.joining(", "));
	}
}

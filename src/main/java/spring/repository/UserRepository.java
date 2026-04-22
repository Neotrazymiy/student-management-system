package spring.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import spring.model.Role;
import spring.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUserName(String userName);

	@Override
	Page<User> findAll(Pageable pageable);

	@Query("SELECT DISTINCT u FROM User u WHERE u.id NOT IN ("
			+ " SELECT u2.id FROM User u2 JOIN u2.roles r2 WHERE r2 IN :roles)")
	Page<User> findAllByRolesNotIn(@Param("roles") List<Role> roles, Pageable pageable);

	List<User> findAllByRolesContains(Role role);

	Optional<User> findByEmail(String email);
}
